package com.example.theproductivityapp.ui.ViewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theproductivityapp.Repository.MainRepository
import com.example.theproductivityapp.db.tables.GraphTodo
import com.example.theproductivityapp.db.tables.Question
import com.example.theproductivityapp.db.tables.Reminder
import com.example.theproductivityapp.db.tables.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel(){

    var todos: LiveData<List<Todo>> = mainRepository.getAllTodos()
    var graphTodos: LiveData<List<GraphTodo>> = mainRepository.getAllGraphTodos()
    var reminders: LiveData<List<Reminder>> = mainRepository.getAllReminders()

    fun reminderByTimestamp(timeStamp: Long): LiveData<List<Reminder>> = mainRepository.getReminder(timeStamp)

    fun insertReminder(reminder: Reminder){
        viewModelScope.launch {
            mainRepository.insertReminder(reminder)
        }
    }

    fun deleteReminder(reminder: Reminder){
        viewModelScope.launch {
            mainRepository.deleteReminder(reminder)
        }
    }

    fun updateReminder(reminder: Reminder){
        viewModelScope.launch {
            mainRepository.updateReminder(reminder)
        }
    }

    fun deleteAllGraphEntries() {
        viewModelScope.launch {
            mainRepository.deleteAll()
        }
    }

    fun getTodoByTAG(tag: String) = mainRepository.getTodoByTag(tag)
    fun getTodoByTimeStamp(timeStamp: Long) = mainRepository.getTodoByTimeStamp(timeStamp)

    fun getById(reqId: Int) = mainRepository.getById(reqId)

    fun insert(todo: Todo):Long {
        var k: Long = 0
        viewModelScope.launch {
            k = mainRepository.insertTodo(todo)
        }
        return k
    }

    fun insertGraph(graphTodo: GraphTodo) = viewModelScope.launch {
        mainRepository.insertGraph(graphTodo)
    }

    fun deleteGraph(graphTodo: GraphTodo) = viewModelScope.launch {
        mainRepository.deleteGraph(graphTodo)
    }

    fun updateGraph(graphTodo: GraphTodo) = viewModelScope.launch {
        mainRepository.updateGraph(graphTodo)
    }

    fun update(todo: Todo) = viewModelScope.launch {
        mainRepository.updateTodo(todo)
    }

    fun delete(todo: Todo) = viewModelScope.launch {
        mainRepository.deleteTodo(todo)
    }
}