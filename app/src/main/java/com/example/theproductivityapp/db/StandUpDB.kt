package com.example.theproductivityapp.db

import androidx.room.*
import com.example.theproductivityapp.db.tables.Question
import com.example.theproductivityapp.db.tables.chatQnA


@Database(
    entities = [Question::class, chatQnA::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class StandUpDB : RoomDatabase() {
    abstract fun getDao(): StandUpDao
}