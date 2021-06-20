package com.example.theproductivityapp.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoDao {

    @Insert
    suspend fun insertTodo(todo: Todo): Long

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("SELECT * FROM Todos WHERE id = :reqId")
    fun getById(reqId: Int): LiveData<Todo>

    @Query("SELECT MAX(displayOrder) FROM Todos")
    suspend fun getHighestOrder(): Int

    @Query("SELECT * FROM Todos ORDER BY displayOrder DESC")
    fun getAllTodos(): LiveData<List<Todo>>

}