package com.example.theproductivityapp.ui.ViewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theproductivityapp.Repository.MainRepository
import com.example.theproductivityapp.db.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel(){

    var todos: LiveData<List<Todo>> = mainRepository.getAllTodos()

    fun getById(reqId: Int) = mainRepository.getById(reqId)

    fun insert(todo: Todo):Long {
        var k: Long = 0
        viewModelScope.launch {
            k = mainRepository.insertTodo(todo)
        }
        return k
    }

    fun getHighestOrder(): Int {
        var order: Int = 0
        viewModelScope.launch {
            order = mainRepository.getHighestOrder()
        }
        return order
    }

    fun update(todo: Todo) = viewModelScope.launch {
        mainRepository.updateTodo(todo)
    }

    fun delete(todo: Todo) = viewModelScope.launch {
        mainRepository.deleteTodo(todo)
    }
}