package com.example.theproductivityapp.ui.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theproductivityapp.Repository.MainRepository
import com.example.theproductivityapp.Repository.StandUpRepository
import com.example.theproductivityapp.db.tables.ChatMessage
import com.example.theproductivityapp.db.tables.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StandUpViewModel @Inject constructor(
    private val repository: StandUpRepository
): ViewModel() {
    var questions: LiveData<List<Question>> = repository.getAllQuestion()
    var chatMessages: LiveData<List<ChatMessage>> = repository.getAllChatMessages()
    var pairMediator = MediatorLiveData<Pair<LiveData<List<Question>>, LiveData<List<ChatMessage>>>>()
    init {
        pairMediator = MediatorLiveData()
        pairMediator.value = Pair(questions, chatMessages)
    }

    fun insertQuestion(question: Question){
        viewModelScope.launch {
            repository.insertQuestion(question)
        }
    }

    fun insertMessage(chatMessage: ChatMessage){
        viewModelScope.launch {
            repository.insertChatMessage(chatMessage)
        }
    }
}