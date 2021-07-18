package com.example.theproductivityapp.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoDao {

    @Insert
    suspend fun insertTodo(todo: Todo): Long

    @Insert
    suspend fun insertGraph(graphTodo: GraphTodo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Query("SELECT * FROM TODOS WHERE timestamp = :timeStamp")
    fun getTodoByTimeStamp(timeStamp: Long): LiveData<List<Todo>>

    @Query("SELECT * FROM TODOS WHERE tag = :tag")
    fun getTodoByTag(tag: String): LiveData<List<Todo>>

    @Update
    suspend fun updateGraph(graphTodo: GraphTodo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("DELETE from GraphTodo")
    suspend fun deleteAllGraphEntries()

    @Delete
    suspend fun deleteGraph(graphTodo: GraphTodo)

    @Query("SELECT * FROM Todos WHERE id = :reqId")
    fun getById(reqId: Int): LiveData<Todo>

    @Query("SELECT MAX(displayOrder) FROM Todos")
    suspend fun getHighestOrder(): Int

    @Query("SELECT * FROM Todos ORDER BY displayOrder DESC")
    fun getAllTodos(): LiveData<List<Todo>>

    @Query("SELECT * FROM GraphTodo ORDER BY date ASC")
    fun getAllGraphTodos(): LiveData<List<GraphTodo>>
}