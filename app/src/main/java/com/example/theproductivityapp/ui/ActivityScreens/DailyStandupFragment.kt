package com.example.theproductivityapp.ui.ActivityScreens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theproductivityapp.Adapter.ChatAdapter
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.FragmentDailyStandupBinding
import com.example.theproductivityapp.db.tables.Category
import com.example.theproductivityapp.db.tables.ChatMessage
import com.example.theproductivityapp.db.tables.Question
import com.example.theproductivityapp.db.tables.Sender
import com.example.theproductivityapp.ui.ViewModels.StandUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DailyStandupFragment : Fragment(R.layout.fragment_daily_standup) {
    private lateinit var binding: FragmentDailyStandupBinding
    private lateinit var chatAdapter: ChatAdapter
    private val viewModel: StandUpViewModel by viewModels()
    private lateinit var questions: List<Question>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDailyStandupBinding.bind(view)
        setUpViews()
        initiateListeners()
        Timber.d("SIZE= ${questions.toString()} | ${questions.size}")
    }
    private fun initiateListeners(){
        viewModel.questions.observe(viewLifecycleOwner){
            questions = it
            Timber.d("SIZE= ${it.size}")
            for(i in it)
                Timber.d("i= $i")
        }
    }

    private fun setUpViews(){
        binding.chatRView.apply {
            chatAdapter = ChatAdapter();
            adapter = chatAdapter;
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}