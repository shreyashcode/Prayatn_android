package com.example.theproductivityapp.repository

import com.example.theproductivityapp.db.StandUpDao
import com.example.theproductivityapp.db.tables.Category
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

    fun getQuestionByCategory(category: Category) = standUpDao.getQuestionByCategory(category)
}