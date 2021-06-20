package com.example.theproductivityapp.ui.Layouts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.ActivityMainBinding
import com.example.theproductivityapp.db.TodoDao
import com.example.theproductivityapp.ui.UIHelper.Common
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var todoDao: TodoDao
    lateinit var nav: NavController
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
//        binding.bottomNavView.background = null
        binding.bottomNavView.menu.getItem(2).isEnabled = false
        binding.bottomBar.background = null
        setContentView(binding.root)

        Timber.d("Hello ${todoDao.hashCode()}")

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hosting) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arg ->
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

                else -> {
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
}