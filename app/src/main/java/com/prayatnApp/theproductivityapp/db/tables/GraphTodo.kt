package com.prayatnApp.theproductivityapp.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GraphTodo(

    var timestamp: Long,
    var added_count: Int,
    var done_count: Int,
    var date: Int,
    var month: Int,
    @PrimaryKey(autoGenerate = true)
    var entry_no: Int? = null

) {
    override fun toString(): String {
        return "$timestamp || $date || $month"
    }
}
