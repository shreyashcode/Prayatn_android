package com.prayatnApp.theproductivityapp.ui.activityAndFragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.prayatnApp.theproductivityapp.R
import com.prayatnApp.theproductivityapp.databinding.FragmentLoginBinding
import com.prayatnApp.theproductivityapp.db.tables.GraphTodo
import com.prayatnApp.theproductivityapp.db.Utils
import com.prayatnApp.theproductivityapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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


        viewModel.graphTodos.observe(viewLifecycleOwner) {
            val calendar = Calendar.getInstance();
            val month = calendar.get(Calendar.MONTH)
            val date = calendar.get(Calendar.DAY_OF_MONTH)
            if (date == 1 && (it.isNotEmpty() && it[it.size - 1].month != month)) {
                viewModel.deleteAllGraphEntries()
            }
            var found = false
            for (graphTodo in it) {
                if (graphTodo.month == month && graphTodo.date == date) {
                    found = true
                    break
                }
            }
            if (!found) {
                viewModel.insertGraph(GraphTodo(System.currentTimeMillis(), 0, 0, date, month+1))
            }
        }
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