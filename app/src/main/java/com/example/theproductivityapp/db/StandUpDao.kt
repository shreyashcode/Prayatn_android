package com.example.theproductivityapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.theproductivityapp.db.tables.ChatMessage
import com.example.theproductivityapp.db.tables.Question

@Dao
interface StandUpDao {
    @Insert
    suspend fun insertQuestion(question: Question)

    @Insert
    suspend fun insertMessage(chatMessage: ChatMessage)

    @Query("SELECT * FROM Question")
    fun get(): LiveData<List<Question>>

    @Query("SELECT * FROM CHATMESSAGE")
    fun getChatMessages(): LiveData<List<ChatMessage>>

    @Query("SELECT * FROM CHATMESSAGE WHERE timeStamp > :mTimestamp")
    fun get(mTimestamp: Long) :LiveData<List<ChatMessage>>
}