package com.example.theproductivityapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Utils.DB_NAME)
data class Todo (
    var title: String,
    var description: String,
    var priority: String,
    var timestamp: Long,
    var status: Boolean,
    var tag: String = "New",
    var displayOrder: Int = 0,
    var importance: String){

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    override fun toString() = "ID: $id | TITLE: $title | ORDER: $displayOrder TAG: $tag | DESC: $description"
}