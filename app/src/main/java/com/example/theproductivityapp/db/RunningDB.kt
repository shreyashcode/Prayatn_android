package com.example.theproductivityapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.theproductivityapp.db.tables.GraphTodo
import com.example.theproductivityapp.db.tables.Reminder
import com.example.theproductivityapp.db.tables.Todo

@Database(
    entities = [
        Todo::class,
        GraphTodo::class,
        Reminder::class
    ],
    version = 9
)
abstract class RunningDB : RoomDatabase() {

    abstract fun getDao(): TodoDao
}