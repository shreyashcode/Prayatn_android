package com.example.theproductivityapp.Utils

import com.example.theproductivityapp.db.GraphTodo
import com.example.theproductivityapp.db.Todo
import kotlin.properties.Delegates

class Common {

    companion object {
        var version = 0
        lateinit var todos: List<Todo>
        val imp = "Important"
        val uimp = "Not Important"
        val urgent = "High"
        val notUrgent = "Low"
        var todos_size: Int = 0
        var reqId: Int = 0
        var tag: String = "tag"
        const val remindAction = "REMINDER_ACTION"
        var loginReq = true
        var reqTimeStamp: Long = 0L
    }
}