package com.example.theproductivityapp.db.tables

import android.text.format.DateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder(
    var remindTimeInMillis: Long,
    var title: String,
    @PrimaryKey(autoGenerate = false)
    var timeStampOfTodo: Long


) {
    override fun toString(): String {
        return "$title | ${convertDate(remindTimeInMillis)}"
    }
    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM hh:mm", timeInMillis).toString()

}
