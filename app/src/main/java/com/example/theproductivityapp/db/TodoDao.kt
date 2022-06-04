package com.example.theproductivityapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.theproductivityapp.db.tables.GraphTodo
import com.example.theproductivityapp.db.tables.Reminder
import com.example.theproductivityapp.db.tables.Todo

@Dao
interface TodoDao {

    @Insert
    suspend fun insertTodo(todo: Todo): Long

    @Insert
    suspend fun insertGraph(graphTodo: GraphTodo)

    @Query("SELECT * FROM REMINDER")
    fun getAllReminders():LiveData<List<Reminder>>

    @Insert
    suspend fun insertReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("DELETE FROM REMINDER WHERE timeStampOfTodo = :timeStamp")
    suspend fun deleteReminderByTimeStamp(timeStamp: Long)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Query("SELECT * FROM TODOS WHERE timestamp = :timeStamp")
    fun getTodoByTimeStamp(timeStamp: Long): LiveData<List<Todo>>

    @Query("SELECT * FROM TODOS WHERE tag = :tag")
    fun getTodoByTag(tag: String): LiveData<List<Todo>>

    @Query("SELECT * FROM reminder WHERE timeStampOfTodo = :timeStamp")
    fun getReminderByTimeStamp(timeStamp: Long): LiveData<List<Reminder>>

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