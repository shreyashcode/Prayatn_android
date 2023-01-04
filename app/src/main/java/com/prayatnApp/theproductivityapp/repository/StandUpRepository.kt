package com.prayatnApp.theproductivityapp.repository

import com.prayatnApp.theproductivityapp.db.StandUpDao
import com.prayatnApp.theproductivityapp.db.tables.Category
import com.prayatnApp.theproductivityapp.db.tables.ChatMessage
import com.prayatnApp.theproductivityapp.db.tables.Question
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