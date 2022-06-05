package com.example.theproductivityapp.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatMessage (
    var sender: Sender,
    var description: String,
    var timeStamp: Long,

    @PrimaryKey(autoGenerate = true)
    var rowId: Int? = null
){
    override fun toString() = "[$rowId, $sender]"
}

enum class Sender{
    USER,
    BOT
}