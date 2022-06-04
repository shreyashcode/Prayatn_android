package com.example.theproductivityapp.db

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.theproductivityapp.db.tables.Category
import com.example.theproductivityapp.db.tables.RowType

class Converters {
    @TypeConverter
    fun fromCategory(category: Category) = category.name

    @TypeConverter
    fun toCategory(category: String) = Category.valueOf(category)

    @TypeConverter
    fun fromRowType(rowType: RowType) = rowType.name

    @TypeConverter
    fun toRowType(rowType: String) = RowType.valueOf(rowType)
}