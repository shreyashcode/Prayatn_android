package com.example.theproductivityapp.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.theproductivityapp.db.Utils

@Entity
data class Question (
    var questionDescription: String,
    var category: Category,
    var sequenceNumber: Int,

    @PrimaryKey(autoGenerate = true)
    var questionId: Int? = null
){
    override fun toString() = "[$questionId, $sequenceNumber, $questionDescription, $category]"
}

enum class Category{
    MORNING,
    EVENING
}