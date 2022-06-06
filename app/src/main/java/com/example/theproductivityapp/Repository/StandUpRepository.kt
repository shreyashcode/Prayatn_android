package com.example.theproductivityapp.Repository

import androidx.lifecycle.LiveData
import com.example.theproductivityapp.db.StandUpDao
import com.example.theproductivityapp.db.tables.ChatMessage
import com.example.theproductivityapp.db.tables.Question
import javax.inject.Inject

class StandUpRepository @Inject constructor(
    private val standUpDao: StandUpDao
){
    suspend fun insertQuestion(question: Question) = standUpDao.insertQuestion(question)
    suspend fun insertChatMessage(chatMessage: ChatMessage) = standUpDao.insertMessage(chatMessage)

    fun getAllQuestion() = standUpDao.get()
    fun getAllChatMessages() = standUpDao.getChatMessages()
}