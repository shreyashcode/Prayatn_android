package com.example.theproductivityapp.ui.UIHelper

import com.example.theproductivityapp.db.Todo
import kotlin.properties.Delegates

class Common {

    companion object {
        var version = 0
        lateinit var todos: List<Todo>
        var todos_size: Int = 0
        var reqId: Int = 0
    }
}