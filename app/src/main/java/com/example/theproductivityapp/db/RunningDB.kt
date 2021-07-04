package com.example.theproductivityapp.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Todo::class,
        GraphTodo::class
    ],
    version = 6
)
abstract class RunningDB : RoomDatabase() {

    abstract fun getDao(): TodoDao
}