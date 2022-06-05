package com.example.theproductivityapp.ui.ActivityScreens

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.FragmentLoginBinding
import com.example.theproductivityapp.db.tables.GraphTodo
import com.example.theproductivityapp.db.Utils
import com.example.theproductivityapp.ui.ViewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var binding: FragmentLoginBinding
    val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        setUpDatabase()
        lifecycleScope.launchWhenResumed {
            delay(2000)
            findNavController().navigate(R.id.action_loginFragment_to_homeTodo)
        }
    }

    private fun setUpDatabase() {
        val month: Int
        val date: Int
        if (Build.VERSION.SDK_INT >= 26) {
            var timeInstance: LocalDateTime? = null
            timeInstance = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
            month = timeInstance.monthValue
            date = timeInstance.dayOfMonth
        } else {
            val timeInstance = Date(System.currentTimeMillis())
            month = timeInstance.month
            date = timeInstance.date
        }
        if(!readSharedPref(date, month)){
            if(date == 1){
                viewModel.deleteAllGraphEntries()
            }
            viewModel.insertGraph(GraphTodo(System.currentTimeMillis(), 0, 0, date, month))
            writeSharedPref(date, month)
        }


//        viewModel.graphTodos.observe(viewLifecycleOwner, {
//            var month: Int = 0
//            var date: Int = 0
//            if (Build.VERSION.SDK_INT >= 26) {
//                var timeInstance: LocalDateTime? = null
//                timeInstance = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
//                month = timeInstance.monthValue
//                date = timeInstance.dayOfMonth
//            } else {
//                var timeInstance = Date(System.currentTimeMillis())
//                month = timeInstance.month
//                date = timeInstance.date
//            }
//
//            if(it.isNotEmpty()){
//                Timber.d("Login__ $date | ${it.size} | ${it[it.size-1].month} | $month")
//            } else {
//                Timber.d("Login__ $date | ${it.size} | $month")
//            }
//            if(date == 1 && (it.isNotEmpty() && it[it.size-1].month != month)){
//                viewModel.deleteAllGraphEntries()
//            }
//            var found = false
//            for (graphTodo in it) {
//                Timber.d("ComputerScience: $graphTodo")
//                if (graphTodo.month == month && graphTodo.date == date) {
//                    found = true
//                    break
//                }
//            }
//            if (found == false) {
//                viewModel.insertGraph(GraphTodo(System.currentTimeMillis(), 0, 0, date, month))
//            }
//        })
    }

    private fun writeSharedPref(date: Int, month: Int){
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(Utils.LOGIN_CRED, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("Date", date)
        editor.putInt("Month", month)
        editor.apply()
    }

    private fun readSharedPref(date: Int, mon: Int): Boolean{
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(Utils.LOGIN_CRED, Context.MODE_PRIVATE)
        val date_ = sharedPref.getInt("Date", -1)
        val mon_ = sharedPref.getInt("Month", -1)
        return date == date_ && mon == mon_
    }
}