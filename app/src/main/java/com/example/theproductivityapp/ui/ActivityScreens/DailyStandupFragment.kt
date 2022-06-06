package com.example.theproductivityapp.ui.ActivityScreens

import android.app.Activity
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
import com.example.theproductivityapp.databinding.FragmentDailyStandupBinding
import com.example.theproductivityapp.db.tables.ChatMessage
import com.example.theproductivityapp.db.tables.Question
import com.example.theproductivityapp.db.tables.Sender
import com.example.theproductivityapp.ui.ViewModels.StandUpViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DailyStandupFragment : Fragment(R.layout.fragment_daily_standup) {
    private lateinit var binding: FragmentDailyStandupBinding
    private lateinit var chatAdapter: ChatAdapter
    private val viewModel: StandUpViewModel by viewModels()
    private var questions: MutableList<Question> = mutableListOf()
    private var chatMessages = ArrayList<ChatMessage>()
    private var areMessagesInserted: Boolean = false
    private var sessionChatMessage: ArrayList<ChatMessage> = ArrayList()
    var questionIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDailyStandupBinding.bind(view)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setUpViews()
        initiateListeners()
    }

    private fun initiateListeners(){
        viewModel.questions.observe(viewLifecycleOwner){
            questions = it as MutableList<Question>
        }
        viewModel.chatMessages.observe(viewLifecycleOwner){ it ->
            if(!areMessagesInserted){
                Timber.d("Shreyash= APPLYING....")
                chatMessages = it as ArrayList<ChatMessage>
                chatAdapter.items = chatMessages
                chatAdapter.notifyDataSetChanged()
                areMessagesInserted = true
                initiateBot(questions)
            }
        }
    }

    private fun setUpViews(){
        binding.chatRView.apply {
            chatAdapter = ChatAdapter();
            adapter = chatAdapter;
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initiateBot(questions: MutableList<Question>){
        binding.send.visibility = View.VISIBLE
        binding.messageEt.visibility = View.VISIBLE
        (activity as MainActivity).binding.bottomBar.visibility = View.GONE
        // posting first message

        chatMessages.add(ChatMessage(Sender.BOT, questions[questionIndex++].questionDescription, System.currentTimeMillis()))
        sessionChatMessage.add(chatMessages.last())

        chatAdapter.items = chatMessages
        Timber.d("Shreyash= asking ques")
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