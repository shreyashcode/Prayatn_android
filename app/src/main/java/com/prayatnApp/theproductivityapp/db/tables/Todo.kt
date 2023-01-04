package com.prayatnApp.theproductivityapp.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prayatnApp.theproductivityapp.db.Utils

@Entity(tableName = Utils.DB_TODO)
data class Todo (
    var title: String,
    var description: String,
    var priority: String,
    var timestamp: Long,
    var status: Boolean,
    var tag: String = "New",
    var displayOrder: Int = 0,
    var importance: String,
    var emoji: String = "✅",
    var isReminderSet: Boolean = false){

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    override fun toString() = "TODO= $title"
}