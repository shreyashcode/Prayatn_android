package com.example.theproductivityapp.ui.Layouts

import android.content.Context
import android.view.LayoutInflater
import com.example.theproductivityapp.databinding.GraphMarkerBinding
import com.example.theproductivityapp.db.GraphTodo
import com.example.theproductivityapp.db.Utils
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class GraphMarkerView (
    val graphTodos: List<GraphTodo>,
    context: Context,
    LayoutId: Int
) : MarkerView(context, LayoutId){

    private lateinit var binding: GraphMarkerBinding

    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        binding = GraphMarkerBinding.inflate(LayoutInflater.from(context), this, true)
        if(e == null){
            return
        }
        var index = 0
        for(graphTodo in graphTodos){
            if(graphTodo.date == e.x.toInt()){
                break
            }
            index++
        }
        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "July", "Aug", "Sep", "Oct", "Nov", "Dec")

        val graphTodo = graphTodos[index]
        binding.added.text = "Added ${graphTodo.added_count}"
        binding.done.text = "Done ${graphTodo.done_count}"
        binding.date.text = "${graphTodo.date} ${months[graphTodo.month-1]}"
    }

}