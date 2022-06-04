package com.example.theproductivityapp.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class chatQnA (
    var rowType: RowType,
    var description: String,
    var timeStamp: Long,

    @PrimaryKey(autoGenerate = true)
    var rowId: Int? = null
){
    override fun toString() = "[$rowId, $rowType]"
}

enum class RowType{
    QUESTION,
    ANSWER
}