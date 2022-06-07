package com.example.theproductivityapp.ui.ActivityScreens

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theproductivityapp.Adapter.ChatAdapter
import com.example.theproductivityapp.R
import com.example.theproductivityapp.Utils.SharedPrefUtil
import com.example.theproductivityapp.databinding.FragmentDailyStandupBinding
import com.example.theproductivityapp.db.tables.*
import com.example.theproductivityapp.ui.ViewModels.StandUpViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DailyStandupFragment : Fragment(R.layout.fragment_daily_standup) {
    private lateinit var binding: FragmentDailyStandupBinding
    private lateinit var chatAdapter: ChatAdapter
    private val viewModel: StandUpViewModel by viewModels()
    private var questions: ArrayList<Question>? = null
    private var chatMessages: ArrayList<ChatMessage>? = null
    private var areMessagesInserted: Boolean = false
    private var sessionChatMessage: ArrayList<ChatMessage> = ArrayList()
    private val USER_KEY = "USER"
    private val USER_MORNING_TIME = "MORNING"
    private val MORNING_ANSWERED = "MORNING_ANSWER"
    private val USER_EVENING_TIME = "EVENING"
    private val EVENING_ANSWERED = "EVENING_ANSWER"
    var questionIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDailyStandupBinding.bind(view)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setUpViews()
        onboardUser()

        // todo: check time and call for question accordingly, here it is ensured that user is onboarded.
        val questionCategory = getQuestionCategory()
        initiateListeners(questionCategory != Category.NONE)
    }

    private fun getQuestionCategory(): Category{
        val morningTime = SharedPrefUtil.readSharedPrefInt(requireContext(), USER_MORNING_TIME)
        val eveningTime = SharedPrefUtil.readSharedPrefInt(requireContext(), USER_EVENING_TIME)
        val calendar = Calendar.getInstance();
        val currentTime = calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE)
        return if(currentTime in morningTime until eveningTime  ){
            Category.MORNING
        } else if(currentTime > eveningTime) {
            Category.EVENING
        } else Category.NONE
    }

    private fun onboardUser(){
        val isNewUser = SharedPrefUtil.readSharedPrefBoolean(requireContext(), USER_KEY)
        if(isNewUser){
            putTimeDetails(USER_MORNING_TIME)
            putTimeDetails(USER_EVENING_TIME)
            activateNotificationAlarm()
        }
    }

    private fun activateNotificationAlarm(){
        // todo put alarms
    }

    private fun putTimeDetails(type: String){
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(requireContext(),
            { _, hourOfDay, minute ->
                SharedPrefUtil.writeSharedPref(requireContext(), type,hourOfDay*60+minute)
            }, hour, minute, false)
        mTimePicker.show()
    }

    private fun initiateListeners(viewQuestion: Boolean = true){
        if(viewQuestion){
            viewModel.questions.observe(viewLifecycleOwner){
                questions = it as ArrayList<Question>
                if(questions != null && chatMessages != null){
                    initiateBot(questions!!, chatMessages!!)
                }
            }
        }
        viewModel.chatMessages.observe(viewLifecycleOwner){ it ->
            if(!areMessagesInserted){
                chatMessages = it as ArrayList<ChatMessage>
                areMessagesInserted = true
                if(questions != null && chatMessages != null){
                    initiateBot(questions!!, chatMessages!!)
                } else if(!viewQuestion){
                    initializeRecyclerView()
                }
            }
        }
        viewModel.pairMediator.observe(viewLifecycleOwner){
            val first = it.first
            val second = it.second
        }
    }

    private fun initializeRecyclerView(){
        chatAdapter.items = chatMessages!!
        chatAdapter.notifyDataSetChanged()
    }

    private fun setUpViews(){
        binding.chatRView.apply {
            chatAdapter = ChatAdapter();
            adapter = chatAdapter;
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initiateBot(questions: MutableList<Question>, chatMessages: ArrayList<ChatMessage>){
        binding.send.visibility = View.VISIBLE
        binding.messageEt.visibility = View.VISIBLE
        (activity as MainActivity).binding.bottomBar.visibility = View.GONE
        chatAdapter.items = chatMessages
        chatAdapter.notifyDataSetChanged()

        chatMessages.add(ChatMessage(Sender.BOT, questions[questionIndex++].questionDescription, System.currentTimeMillis()))
        sessionChatMessage.add(chatMessages.last())

        chatAdapter.items = chatMessages
        binding.chatRView.scrollToPosition(chatMessages.size-1)
        chatAdapter.notifyItemInserted(chatAdapter.items.size-1)
        binding.send.setOnClickListener{
            val userMessage = binding.messageEt.editableText.toString();
            if(userMessage.isEmpty()){
                Snackbar.make(requireView(), "Invalid", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            chatMessages.add(ChatMessage(Sender.USER, userMessage.toString(), System.currentTimeMillis()))
            sessionChatMessage.add(ChatMessage(Sender.USER, userMessage.toString(), System.currentTimeMillis()))

            chatMessages.add(ChatMessage(Sender.BOT, questions[questionIndex++].questionDescription, System.currentTimeMillis()))
            sessionChatMessage.add(chatMessages.last())

            chatAdapter.items = chatMessages
            binding.chatRView.scrollToPosition(chatMessages.size-1)
            chatAdapter.notifyDataSetChanged()

            binding.messageEt.editableText.clear()

            if(questionIndex == questions.size) {
                areMessagesInserted = true
                val imm =
                    requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)


                binding.messageEt1.editText!!.inputType = InputType.TYPE_NULL
                binding.send.visibility = View.GONE
                binding.messageEt.visibility = View.GONE
                (requireActivity() as MainActivity).binding.apply {
                    bottomBar.visibility = View.VISIBLE
                    bottomNavView.visibility = View.VISIBLE
                }
                addMessagesToDB(sessionChatMessage)
            }
        }
    }

    private fun addMessagesToDB(messages: List<ChatMessage>){
        messages.forEach {
            viewModel.insertMessage(it)
        }
    }
}