package com.prayatnApp.theproductivityapp.ui.activityAndFragments

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.prayatnApp.theproductivityapp.databinding.GraphMarkerBinding
import com.prayatnApp.theproductivityapp.db.tables.GraphTodo
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class GraphMarkerView (
    val graphTodos: List<GraphTodo>,
    context: Context,
    LayoutId: Int,
    var stats: TextView,
    var date_: TextView
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
        val input_ = "Added ${graphTodo.added_count}\nDone ${graphTodo.done_count}"
        stats.text = input_
        date_.text = "${graphTodo.date} ${months[graphTodo.month - 1]}"
    }

}