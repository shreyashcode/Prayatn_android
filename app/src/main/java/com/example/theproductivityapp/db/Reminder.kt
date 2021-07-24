package com.example.theproductivityapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder(
    var remindTimeInMillis: Long,
    @PrimaryKey(autoGenerate = false)
    var timeStampOfTodo: Long
)
