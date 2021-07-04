package com.example.theproductivityapp.ui.Layouts

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.ActivityMainBinding
import com.example.theproductivityapp.db.GraphTodo
import com.example.theproductivityapp.db.Todo
import com.example.theproductivityapp.db.TodoDao
import com.example.theproductivityapp.ui.UIHelper.Common
import com.example.theproductivityapp.ui.ViewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var todoDao: TodoDao
    lateinit var nav: NavController
    lateinit var binding : ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var graphTodo: GraphTodo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.bottomNavView.menu.getItem(2).isEnabled = false
        binding.bottomBar.background = null
        setContentView(binding.root)
//        setUpDatabase()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hosting) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arg ->
            binding.background.setBackgroundColor(ContextCompat.getColor(this, R.color.ui_light2))
            statusBar(R.color.ui_light2)
            when(destination.id){
                R.id.loginFragment, R.id.tagFilterFragment, R.id.addTodoFragment -> {
                    binding.fabButton.visibility = View.INVISIBLE
                    binding.bottomBar.visibility = View.INVISIBLE
                    binding.bottomNavView.visibility = View.INVISIBLE
                }

                R.id.homeTodo -> {
                    binding.fabButton.visibility = View.VISIBLE
                    binding.bottomNavView.visibility = View.VISIBLE
                    binding.bottomBar.visibility = View.VISIBLE
                }

                R.id.graphFragment, R.id.quadrantFragment ->{
                    statusBar(R.color.ui_dark2)
                    binding.fabButton.visibility = View.INVISIBLE
                    binding.background.setBackgroundColor(ContextCompat.getColor(this, R.color.ui_dark2))
                }

                else -> {
                    Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show()
                    binding.bottomNavView.visibility = View.VISIBLE
                    binding.bottomBar.visibility = View.VISIBLE
                    binding.fabButton.visibility = View.INVISIBLE
                }
            }
        }

        binding.fabButton.setOnClickListener {
            val action = HomeTodoFragmentDirections.actionHomeTodoToAddTodoFragment()
            navController.navigate(R.id.action_homeTodo_to_addTodoFragment)
            Common.reqId = -1
        }
    }

    private fun statusBar(color_id: Int){
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(color_id)
        }
    }

    private fun setUpDatabase(){
        viewModel.graphTodos.observe(this, Observer{
            var month: Int = 0
            var date: Int = 0
            if(Build.VERSION.SDK_INT >= 26){
                var timeInstance: LocalDateTime? = null
                timeInstance = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                month = timeInstance.monthValue
                date = timeInstance.dayOfMonth
            } else {
                var timeInstance = Date(System.currentTimeMillis())
                month = timeInstance.month
                date = timeInstance.date
            }
            var found = false
            for(graphTodo in it){
                if(graphTodo.month == month && graphTodo.date == date){
                    found = true
                    break
                }
            }
            if(found == false){
                insertGraphTodo(GraphTodo(System.currentTimeMillis(), 0, 0, date, month))
            }
//            if(it.size == 31){
//                last 20 days record!
//                viewModel.deleteGraph(it[0])
//            }
//            if(it.isEmpty() || it[it.size-1].date != date.date){
//                Timber.d("VedicCricket: Date: Insertion!")
//                viewModel.insertGraph(GraphTodo(System.currentTimeMillis(), 0, 0, date.date))
//            }
        })
    }

    private fun insertGraphTodo(graphTodo: GraphTodo){
        viewModel.insertGraph(graphTodo)
    }
}