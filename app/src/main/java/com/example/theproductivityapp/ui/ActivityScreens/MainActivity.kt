package com.example.theproductivityapp.ui.ActivityScreens

import android.R.attr.left
import android.R.attr.right
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.theproductivityapp.R
import com.example.theproductivityapp.Utils.Common
import com.example.theproductivityapp.databinding.ActivityMainBinding
import com.example.theproductivityapp.db.TodoDao
import com.example.theproductivityapp.db.tables.GraphTodo
import com.example.theproductivityapp.ui.ViewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
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
    val CHANNEL_ID = "CHANNEL_ID"
    val CHANNEL_NAME = "CHANNEL_NAME"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.bottomBar.background = null
        setContentView(binding.root)
        createNotification(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hosting) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)

        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->

                binding.background.setBackgroundColor(ContextCompat.getColor(this, R.color.ui_light2))
                if(destination.id == R.id.dailyStandupFragment)
                    binding.background.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_dark))
                statusBar(R.color.ui_light2)

                when(destination.id){
                    R.id.dailyStandupFragment ->{
                        val params = CoordinatorLayout.LayoutParams(
                            CoordinatorLayout.LayoutParams.MATCH_PARENT,
                            CoordinatorLayout.LayoutParams.MATCH_PARENT
                        )
                        // todo reset on fragment back pressed
                        params.setMargins(0, 0, 0, 0)
                        binding.framelayout.layoutParams = params
                        binding.fabButton.visibility = View.GONE
                        binding.bottomBar.visibility = View.GONE
                        binding.bottomNavView.visibility = View.GONE
                    }
                    R.id.loginFragment, R.id.tagFilterFragment, R.id.addTodoFragment -> {
                        val params = CoordinatorLayout.LayoutParams(
                            CoordinatorLayout.LayoutParams.MATCH_PARENT,
                            CoordinatorLayout.LayoutParams.MATCH_PARENT
                        )
                        // todo reset on fragment back pressed
                        params.setMargins(0, 0, 0, 50)
                        binding.fabButton.visibility = View.GONE
                        binding.bottomBar.visibility = View.GONE
                        binding.bottomNavView.visibility = View.GONE
                    }

                    R.id.homeTodo -> {
                        val params = CoordinatorLayout.LayoutParams(
                            CoordinatorLayout.LayoutParams.MATCH_PARENT,
                            CoordinatorLayout.LayoutParams.MATCH_PARENT
                        )
                        // todo reset on fragment back pressed
                        params.setMargins(0, 0, 0, 50)
                        binding.fabButton.visibility = View.VISIBLE
                        binding.bottomNavView.visibility = View.VISIBLE
                        binding.bottomBar.visibility = View.VISIBLE
                    }

                    R.id.graphFragment, R.id.quadrantFragment ->{
                        val params = CoordinatorLayout.LayoutParams(
                            CoordinatorLayout.LayoutParams.MATCH_PARENT,
                            CoordinatorLayout.LayoutParams.MATCH_PARENT
                        )
                        // todo reset on fragment back pressed
                        params.setMargins(0, 0, 0, 50)
                        statusBar(R.color.ui_dark2)
                        binding.bottomNavView.visibility = View.VISIBLE
                        binding.bottomBar.visibility = View.VISIBLE
                        binding.fabButton.visibility = View.INVISIBLE
                        binding.background.setBackgroundColor(ContextCompat.getColor(this, R.color.ui_dark2))
                    }

                    else -> {
                        val params = CoordinatorLayout.LayoutParams(
                            CoordinatorLayout.LayoutParams.MATCH_PARENT,
                            CoordinatorLayout.LayoutParams.MATCH_PARENT
                        )
                        // todo reset on fragment back pressed
                        params.setMargins(0, 0, 0, 50)
                        binding.bottomNavView.visibility = View.VISIBLE
                        binding.bottomBar.visibility = View.VISIBLE
                        binding.fabButton.visibility = View.INVISIBLE
                    }
                }
            }

        binding.fabButton.setOnClickListener {
            val action = HomeTodoFragmentDirections.actionHomeTodoToAddTodoFragment()
            navController.navigate(R.id.action_homeTodo_to_addTodoFragment)
            Common.reqTimeStamp = 0L
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
                val timeInstance = Date(System.currentTimeMillis())
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
            if(!found){
                insertGraphTodo(GraphTodo(System.currentTimeMillis(), 0, 0, date, month))
            }
            if(it.size == 31){
                viewModel.deleteGraph(it[0])
            }
//            if(it.isEmpty() || it[it.size-1].date != date.date){
//                viewModel.insertGraph(GraphTodo(System.currentTimeMillis(), 0, 0, date.date))
//            }
        })
    }

    private fun insertGraphTodo(graphTodo: GraphTodo){
        viewModel.insertGraph(graphTodo)
    }
    private fun createNotification(context: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}