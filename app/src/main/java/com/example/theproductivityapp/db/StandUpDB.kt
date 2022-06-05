package com.example.theproductivityapp.db

import androidx.room.*
import com.example.theproductivityapp.db.tables.Question
import com.example.theproductivityapp.db.tables.ChatMessage


@Database(
    entities = [Question::class, ChatMessage::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class StandUpDB : RoomDatabase() {
    abstract fun getDao(): StandUpDao
}