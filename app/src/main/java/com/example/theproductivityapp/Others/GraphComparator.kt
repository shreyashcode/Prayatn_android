package com.example.theproductivityapp.Others

import com.example.theproductivityapp.db.tables.GraphTodo

class GraphComparator : Comparator<GraphTodo>{
    override fun compare(o1: GraphTodo?, o2: GraphTodo?): Int {
        if(o1 == null || o2 == null){
            return 0
        }
        if(o1.month == o2.month){
            return o1.date-o2.date
        } else {
            return o1.month-o2.month
        }
    }

}