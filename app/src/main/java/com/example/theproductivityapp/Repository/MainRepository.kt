package com.example.theproductivityapp.Repository

import com.example.theproductivityapp.db.GraphTodo
import com.example.theproductivityapp.db.Todo
import com.example.theproductivityapp.db.TodoDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    val todoDao: TodoDao
) {
    suspend fun insertTodo(todo: Todo): Long{
        return todoDao.insertTodo(todo)
    }

    fun getTodoByTag(tag: String) = todoDao.getTodoByTag(tag)

    suspend fun deleteAll() = todoDao.deleteAllGraphEntries()

    suspend fun insertGraph(graphTodo: GraphTodo) = todoDao.insertGraph(graphTodo)

    suspend fun updateGraph(graphTodo: GraphTodo) = todoDao.updateGraph(graphTodo)

    suspend fun deleteGraph(graphTodo: GraphTodo) = todoDao.deleteGraph(graphTodo)

    fun getAllGraphTodos() = todoDao.getAllGraphTodos()

    fun getById(reqId: Int) = todoDao.getById(reqId)

    suspend fun deleteTodo(todo: Todo) = todoDao.deleteTodo(todo)

    suspend fun updateTodo(todo: Todo) = todoDao.updateTodo(todo)

    suspend fun getHighestOrder() = todoDao.getHighestOrder()

    fun getAllTodos() = todoDao.getAllTodos()
}