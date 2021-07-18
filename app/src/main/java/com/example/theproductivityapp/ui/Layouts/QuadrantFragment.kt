package com.example.theproductivityapp.ui.Layouts

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.FragmentQuadrantBinding
import com.example.theproductivityapp.db.Utils
import com.example.theproductivityapp.ui.ViewModels.MainViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class QuadrantFragment : Fragment(R.layout.fragment_quadrant) {
    private lateinit var pieEntries: ArrayList<PieEntry>
    private lateinit var binding: FragmentQuadrantBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var pieColor: ArrayList<Int>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentQuadrantBinding.bind(view)

        pieEntries = ArrayList<PieEntry>()

        setUpPieEntries()
//        setUpPie()
    }

    private fun setUpPieEntries() {
//        var map: MutableMap<String, Float> = HashMap<String, Float>()
//        map[IMP_UR] = 0f
//        map[IMP_NUR] = 0f
//        map[NIMP_UR] = 0f
//        map[NIMP_NUR] = 0f
//        for (todo in it) {
//            if (todo.importance == Common.imp) {
//                if (todo.priority == Common.urgent) {
//                    map[IMP_UR] = map[IMP_UR]!!.plus(1f)
//                } else {
//                    map[IMP_NUR] = map[IMP_NUR]!!.plus(1f)
//                }
//            } else {
//                if (todo.priority == Common.urgent) {
//                    map[NIMP_UR] = map[NIMP_UR]!!.plus(1f)
//                } else {
//                    map[NIMP_NUR] = map[NIMP_NUR]!!.plus(1f)
//                }
//            }
//        }
        Timber.d("APJ Abdul Kalam Sir | ${Utils.IMP_UR} ${Utils.IMP_NUR} ${Utils.NIMP_UR} ${Utils.NIMP_NUR}")
        pieEntries.add(PieEntry(readSharedPref(Utils.IMP_UR), "IMP URGENT"))
        pieEntries.add(PieEntry(readSharedPref(Utils.IMP_NUR), "IMP NON-URGENT"))
        pieEntries.add(PieEntry(readSharedPref(Utils.NIMP_UR), "NON-IMP URGENT"))
        pieEntries.add(PieEntry(readSharedPref(Utils.NIMP_NUR), "NON-IMP NON-URGENT"))
        setUpPie()
    }

    private fun setUpPie() {
        val pieDataSet = PieDataSet(pieEntries, "LABEL").apply {
            pieColor = ArrayList()
            pieColor.add(Color.parseColor("#e35f41"))
            pieColor.add(Color.parseColor("#4ec2f7"))
            pieColor.add(Color.parseColor("#fec166"))
            pieColor.add(Color.parseColor("#81c784"))
            this.colors = pieColor
            valueFormatter = PercentFormatter()
            sliceSpace = 3f
            xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            valueTextColor = Color.WHITE
            valueLineColor = Color.WHITE
        }
        val pieData = PieData(pieDataSet)
        binding.piechart.data = pieData.apply {
            setValueTextSize(15f)
        }
        binding.piechart.apply {
            holeRadius = 30f
            legend.isEnabled = false
//            isDrawEntryLabelsEnabled = false
            transparentCircleRadius = 33f
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setDrawCenterText(true)
            centerText = "Time distribution"
            animateX(1000, Easing.EaseInBounce)
        }
        binding.piechart.invalidate()
    }

    private fun readSharedPref(key: String): Float {
        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences(Utils.QuadrantSharedPrefs, Context.MODE_PRIVATE)
        val value = sharedPref.getFloat(key, 0f)
        Timber.d("APJ Abdul Kalam Sir | $key | ${value}")
        return value
    }

}