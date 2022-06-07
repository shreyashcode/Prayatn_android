package com.example.theproductivityapp.Utils

import android.content.Context
import android.content.SharedPreferences
import com.example.theproductivityapp.db.Utils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SharedPrefUtil {
    companion object{
        fun writeSharedPref(context: Context, key: String, value: Int){
            val sharedPref: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun readSharedPrefInt(context: Context, key: String): Int{
            val sharedPref: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            return sharedPref.getInt(key, 0)
        }

        fun readSharedPrefBoolean(context: Context, key: String): Boolean{
            val sharedPref: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            return sharedPref.getBoolean(key, false)
        }
    }
}