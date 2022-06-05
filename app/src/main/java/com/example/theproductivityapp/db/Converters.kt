package com.example.theproductivityapp.db

import androidx.room.TypeConverter
import com.example.theproductivityapp.db.tables.Category
import com.example.theproductivityapp.db.tables.Sender

class Converters {
    @TypeConverter
    fun fromCategory(category: Category) = category.name

    @TypeConverter
    fun toCategory(category: String) = Category.valueOf(category)

    @TypeConverter
    fun fromRowType(sender: Sender) = sender.name

    @TypeConverter
    fun toRowType(rowType: String) = Sender.valueOf(rowType)
}