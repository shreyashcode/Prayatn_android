package com.example.theproductivityapp.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Todo::class],
    version = 4
)
abstract class RunningDB : RoomDatabase() {

    abstract fun getDao(): TodoDao
}