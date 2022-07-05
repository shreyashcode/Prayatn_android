package com.example.theproductivityapp.ui.ActivityScreens

import android.app.Activity
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theproductivityapp.Adapter.ChatAdapter
import com.example.theproductivityapp.R
import com.example.theproductivityapp.Service.ReminderService
import com.example.theproductivityapp.Utils.SharedPrefUtil
import com.example.theproductivityapp.databinding.ActivityMainBinding
import com.example.theproductivityapp.databinding.FragmentDailyStandupBinding
import com.example.theproductivityapp.db.tables.Category
import com.example.theproductivityapp.db.tables.ChatMessage
import com.example.theproductivityapp.db.tables.Question
import com.example.theproductivityapp.db.tables.Sender
import com.example.theproductivityapp.ui.ViewModels.StandUpViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.properties.Delegates

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
    private var eveningTime: Long = 0L
    private var morningTime: Long = 0L
    var questionIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDailyStandupBinding.bind(view)
        statusBar(R.color.primary_dark)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        onboardUser()
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.remove.setOnClickListener{
            ReminderService(requireContext()).cancelRepeatingAlarm()
        }
    }

    private fun areQuestionAnswered(questionCategory: Category): Boolean{
        val key = when (questionCategory) {
            Category.MORNING -> {
                MORNING_ANSWERED
            }
            Category.EVENING -> {
                EVENING_ANSWERED
            }
            else -> return true
        }
        val today = SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)
        val mostRecentAnsweredString = SharedPrefUtil.readSharedPrefString(requireContext(), key)
        if(mostRecentAnsweredString == "NA") return false
        return mostRecentAnsweredString == today
    }

    private fun getQuestionCategory(): Category{
        val morningTime = SharedPrefUtil.readSharedPrefInt(requireContext(), USER_MORNING_TIME)
        val eveningTime = SharedPrefUtil.readSharedPrefInt(requireContext(), USER_EVENING_TIME)
        val calendar = Calendar.getInstance();
        val currentTime = calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE)
        return if(currentTime in morningTime until eveningTime  ){
            Category.MORNING
        } else if(currentTime >= eveningTime) {
            Category.EVENING
        } else Category.NONE
    }

    private fun onboardUser(){
        val isRegisteredUser = SharedPrefUtil.readSharedPrefBoolean(requireContext(), USER_KEY)
        if(!isRegisteredUser){
            binding.messageEt.visibility = View.INVISIBLE
            binding.send.visibility = View.INVISIBLE
            binding.chatRView.visibility = View.INVISIBLE
            binding.submit.visibility = View.INVISIBLE

            binding.morningtv.visibility = View.VISIBLE
            binding.morningbutton.visibility = View.VISIBLE
            binding.eveningtv.visibility = View.VISIBLE
            binding.eveningbutton.visibility = View.VISIBLE
            binding.messageBot.visibility = View.VISIBLE
            binding.bot.visibility = View.VISIBLE

            binding.morningbutton.setOnClickListener {
                putTimeDetails(Category.MORNING)
            }
            binding.eveningbutton.setOnClickListener {
                putTimeDetails(Category.EVENING)
            }

            binding.submit.setOnClickListener {
                if(eveningTime < morningTime){
                    Snackbar.make(requireView(), "Invalid", Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val mTime = SharedPrefUtil.readSharedPrefInt(requireContext(), USER_MORNING_TIME)
                val eTime = SharedPrefUtil.readSharedPrefInt(requireContext(), USER_EVENING_TIME)
                val morningHr = mTime/60
                val morningMin = mTime% 60
                val eveningHr = eTime/60
                val eveningMin = eTime % 60
                val reminderService = ReminderService(requireContext())
                reminderService.setRepeatingAlarm(morningHr, morningMin, Category.MORNING)
                reminderService.setRepeatingAlarm(eveningHr, eveningMin, Category.EVENING)
                initiateObservers()
            }
        } else {
            initiateObservers()
        }
    }

    private fun statusBar(color_id: Int){
        if (Build.VERSION.SDK_INT >= 21) {
            val window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(color_id)
        }
    }

    private fun initiateObservers(){
        binding.morningtv.visibility = View.INVISIBLE
        binding.morningbutton.visibility = View.INVISIBLE
        binding.messageBot.visibility = View.INVISIBLE
        binding.bot.visibility = View.INVISIBLE
        binding.eveningtv.visibility = View.INVISIBLE
        binding.eveningbutton.visibility = View.INVISIBLE
        binding.submit.visibility = View.INVISIBLE

        binding.send.visibility = View.VISIBLE
        binding.messageEt.visibility = View.VISIBLE
        binding.chatRView.visibility = View.VISIBLE
        setUpViews()
        val questionCategory = getQuestionCategory()
        val notAnswered = !areQuestionAnswered(questionCategory)
//        Toast.makeText(requireContext(), "NONE?= ${questionCategory == Category.NONE} Answered= ${!notAnswered}", Toast.LENGTH_SHORT).show()
        initiateListeners(questionCategory != Category.NONE &&  notAnswered, questionCategory)
    }

    private fun initiateListeners(viewQuestion: Boolean = true, questionCategory: Category){
//        Snackbar.make(requireView(), "quesByCategory, ${questionCategory.toString()}", Snackbar.LENGTH_SHORT).show()
        if(viewQuestion){
            viewModel.getQuestionsByCategory(questionCategory).observe(viewLifecycleOwner){
                questions = it as ArrayList<Question>
                if(questions != null && chatMessages != null){
                    initiateBot(questions!!, chatMessages!!, questionCategory)
                }
            }
        } else {
//            Snackbar.make(requireView(), "Already responded!", Snackbar.LENGTH_LONG).show()
        }
        viewModel.chatMessages.observe(viewLifecycleOwner){ it ->
            if(!areMessagesInserted){
                chatMessages = it as ArrayList<ChatMessage>
                areMessagesInserted = true
                if(questions != null && chatMessages != null){
                    initiateBot(questions!!, chatMessages!!, questionCategory)
                } else if(!viewQuestion){
                    initializeRecyclerView()
                }
            }
        }
    }

    private fun activateNotificationAlarm(timeInMillis: Long){

    }

    private fun putTimeDetails(category: Category){
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)
        var time: String = ""
        mTimePicker = TimePickerDialog(requireContext(),
            { _, hr, min ->
                if(hr < 10) time += "0"
                time += hr.toString()
                time += ":"
                if(min < 10) time+="0"
                time += min
                if(category == Category.MORNING) binding.morningtv.text = time
                else binding.eveningtv.text = time
                val now = Calendar.getInstance();
                now.set(Calendar.HOUR_OF_DAY, hr)
                now.set(Calendar.MINUTE, min)
                val timeInMilli = now.timeInMillis
                activateNotificationAlarm(timeInMilli)
                if(category == Category.MORNING) morningTime = timeInMilli
                else eveningTime = timeInMilli
                SharedPrefUtil.writeSharedPrefInt(requireContext(), key= if(category == Category.MORNING) USER_MORNING_TIME else USER_EVENING_TIME, hr*60+min)
                putUserKey()
            }, hour, minute, true)
        mTimePicker.show()
    }

    private fun putUserKey(){
        if(morningTime != 0L && eveningTime != 0L){
            SharedPrefUtil.writeSharedPrefBoolean(requireContext(), USER_KEY, true)
            binding.submit.visibility = View.VISIBLE
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

    private fun onBackPressed(){
        val params = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT
        )
        // todo reset on fragment back pressed
        params.setMargins(0, 0, 0, 48)
        (requireActivity() as MainActivity).binding.framelayout.layoutParams = params
        findNavController().popBackStack()
    }

    private fun initiateBot(questions: MutableList<Question>, chatMessages: ArrayList<ChatMessage>, questionCategory: Category){
        binding.send.visibility = View.VISIBLE
        binding.messageEt.visibility = View.VISIBLE
        (activity as MainActivity).binding.bottomBar.visibility = View.GONE
        chatAdapter.items = chatMessages
        chatAdapter.notifyDataSetChanged()
        binding.chatRView.scrollToPosition(chatMessages.size-1)

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
                SharedPrefUtil.writeSharedPrefString(requireContext(),
                    key= if(questionCategory == Category.MORNING) MORNING_ANSWERED else EVENING_ANSWERED,
                    SimpleDateFormat("dd/MM/yyyy").format(Date())
                )
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