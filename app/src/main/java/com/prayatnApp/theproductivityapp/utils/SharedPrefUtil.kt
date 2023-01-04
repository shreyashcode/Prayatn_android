package com.prayatnApp.theproductivityapp.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefUtil {
    companion object{
        fun writeSharedPrefInt(context: Context, key: String, value: Int){
            val sharedPref: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putInt(key, value)
            editor.apply()
        }
        fun writeSharedPrefString(context: Context, key: String, value: String){
            val sharedPref: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun writeSharedPrefBoolean(context: Context, key: String, value: Boolean){
            val sharedPref: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean(key, value)
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
        fun readSharedPrefString(context: Context, key: String): String {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(key, Context.MODE_PRIVATE)
            return sharedPref.getString(key, "NA") ?: "NA"
        }
    }
}