package com.example.theproductivityapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.theproductivityapp.db.tables.Question

@Dao
interface StandUpDao {
    @Insert
    suspend fun insertQuestion(question: Question)

    @Query("SELECT * FROM Question")
    fun get(): LiveData<List<Question>>
}