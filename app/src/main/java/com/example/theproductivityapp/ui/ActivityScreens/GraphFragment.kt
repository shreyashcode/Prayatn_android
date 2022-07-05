package com.example.theproductivityapp.ui.ActivityScreens

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.theproductivityapp.Others.GraphComparator
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.FragmentGraphBinding
import com.example.theproductivityapp.ui.ViewModels.MainViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class GraphFragment : Fragment(R.layout.fragment_graph) {
    private lateinit var binding: FragmentGraphBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var valueFormatter: ValueFormatter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGraphBinding.bind(view)
        valueFormatter = object : ValueFormatter(){
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}"
            }
        }
        setUpGraph()
        setUpData()
        binding.graph.setOnClickListener {
            binding.graph.invalidate()
        }
    }

    private fun setUpData(){
        viewModel.graphTodos.observe(viewLifecycleOwner) {
            it?.let {
                Collections.sort(it, GraphComparator())
                val data = it.indices.map { i ->
                    BarEntry(
                        it[i].date.toFloat(),
                        it[i].done_count.toFloat()
                    )
                }
                val dataSet = BarDataSet(data, "Task completed Vs Date").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.ui_light2)
                }
                binding.graph.legend.textSize = 15f
                binding.graph.legend.isEnabled = false
                binding.graph.data = BarData(dataSet)
                binding.graph.data.setValueFormatter(valueFormatter)
                binding.graph.data.setValueTextSize(10f)
                binding.graph.description.isEnabled = false
                binding.graph.isDoubleTapToZoomEnabled = false
                binding.graph.marker = GraphMarkerView(
                    it,
                    requireContext(),
                    R.layout.graph_marker,
                    binding.stats,
                    binding.date
                )
                binding.graph.invalidate()
            }
        }
    }



    private fun setUpGraph(){
        binding.graph.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(true)
            textColor = Color.WHITE
            axisLineColor = Color.WHITE
            setDrawGridLines(false)
            textSize = 15f
        }

        binding.graph.axisLeft.apply {
            axisLineColor = Color.TRANSPARENT
            textColor = Color.TRANSPARENT
            setDrawGridLines(false)
        }
        binding.graph.axisRight.apply {
            axisLineColor = Color.TRANSPARENT
            textColor = Color.TRANSPARENT
            setDrawGridLines(false)
        }
        binding.graph.apply {
            legend.isEnabled = true
            animateX(1500, Easing.EaseInBack)
        }
    }
}