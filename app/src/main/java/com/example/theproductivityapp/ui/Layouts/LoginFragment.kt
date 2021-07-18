package com.example.theproductivityapp.ui.Layouts

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.theproductivityapp.R
import com.example.theproductivityapp.Utils.Common
import com.example.theproductivityapp.databinding.FragmentLoginBinding
import com.example.theproductivityapp.db.GraphTodo
import com.example.theproductivityapp.db.Todo
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
        Timber.d("Computer Start")
        setUpDatabase()
        Handler().postDelayed({
            findNavController().navigate(R.id.action_loginFragment_to_homeTodo)
            Timber.d("Computer End")
        }, 1000)
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
        Toast.makeText(requireContext(),"FOUND! ${readSharedPref(date, month)}", Toast.LENGTH_SHORT).show()
        if(readSharedPref(date, month) == false){
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