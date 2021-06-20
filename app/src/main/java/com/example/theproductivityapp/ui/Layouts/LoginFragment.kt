package com.example.theproductivityapp.ui.Layouts

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.FragmentLoginBinding
import com.example.theproductivityapp.db.Todo
import com.example.theproductivityapp.ui.ViewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var binding: FragmentLoginBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        binding.existing.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_homeTodo)
        }

        binding.button.setOnClickListener{
//            viewModel.insert(Todo("NULL", "NULL", "", System.currentTimeMillis(), true, "TAG_1", 0, ""))
//            viewModel.insert(Todo("NULL1", "NULL1", "", System.currentTimeMillis(), true, "TAG_2", 1, ""))
            findNavController().navigate(R.id.action_loginFragment_to_homeTodo)
        }
    }
}