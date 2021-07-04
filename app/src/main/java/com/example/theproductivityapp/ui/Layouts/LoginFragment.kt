package com.example.theproductivityapp.ui.Layouts

import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.FragmentLoginBinding
import com.example.theproductivityapp.db.GraphTodo
import com.example.theproductivityapp.db.Todo
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
        viewModel.graphTodos.observe(viewLifecycleOwner, {
            var month: Int = 0
            var date: Int = 0
            if (Build.VERSION.SDK_INT >= 26) {
                var timeInstance: LocalDateTime? = null
                timeInstance = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                month = timeInstance.monthValue
                date = timeInstance.dayOfMonth
            } else {
                var timeInstance = Date(System.currentTimeMillis())
                month = timeInstance.month
                date = timeInstance.date
            }

            if(it.isNotEmpty()){
                Timber.d("Login__ $date | ${it.size} | ${it[it.size-1].month} | $month")
            } else {
                Timber.d("Login__ $date | ${it.size} | $month")
            }
            if(date == 1 && (it.isNotEmpty() && it[it.size-1].month != month)){
                viewModel.deleteAllGraphEntries()
            }
            var found = false
            for (graphTodo in it) {
                Timber.d("ComputerScience: $graphTodo")
                if (graphTodo.month == month && graphTodo.date == date) {
                    found = true
                    break
                }
            }
            if (found == false) {
                viewModel.insertGraph(GraphTodo(System.currentTimeMillis(), 0, 0, date, month))
            }
        })
    }
}