package com.example.theproductivityapp.ui.Layouts

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.FragmentAddTodoBinding
import com.example.theproductivityapp.db.Todo
import com.example.theproductivityapp.ui.UIHelper.Common
import com.example.theproductivityapp.ui.ViewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddTodoFragment : Fragment(R.layout.fragment_add_todo) {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentAddTodoBinding
    private lateinit var todo: Todo
    private lateinit var priority: String
    private lateinit var priorities: Array<String>
    private lateinit var importance: Array<String>
    private lateinit var imp: String
    private lateinit var args: AddTodoFragmentArgs
    private val arg: AddTodoFragmentArgs by navArgs()
    private var id_: Int = 0
    private lateinit var gTodo: Todo


    override fun onResume() {
        super.onResume()
        id_ = Common.reqId
        if(id_ > 0){
            prepareUI(id_)
        }
        importance = resources.getStringArray(R.array.Importance)
        priorities = resources.getStringArray(R.array.Priority)
        binding.dropDownImp.setAdapter(ArrayAdapter(requireContext(), R.layout.custom_spinner, importance))
        binding.dropDown.setAdapter(ArrayAdapter(requireContext(), R.layout.custom_spinner, priorities))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onBack = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Toast.makeText(requireContext(), "ONBACK!", Toast.LENGTH_SHORT).show()

                if(validate(view)){
//                    binding.toggleButton.visibility = View.INVISIBLE
                    var notify: String? = null
                    if(id_ > 0){
                        notify = "Todo updated!"
                        updateTodo()
                    } else {
                        notify = "Todo Saved!"
                        insertTodo()
                    }
                    Snackbar.make(view, notify, Snackbar.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }

                findNavController().popBackStack()
            }
        }



//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBack)

        id_ = Common.reqId

        if(id_ > 0){
            prepareUI(id_)
        }

        binding = FragmentAddTodoBinding.bind(view)
        importance = resources.getStringArray(R.array.Importance)
        priorities = resources.getStringArray(R.array.Priority)
        priority = priorities[0]
        imp = importance[0]

        Timber.d("NUMBER IS FAST: $id_")

        binding.toggleButton.bind(binding.title, binding.description, binding.tag)

        binding.toggleButton.setOnClickListener {
            if(binding.title.getEditing()){
                if(validate(view)){
                    binding.toggleButton.visibility = View.INVISIBLE
                    var notify: String? = null
                    if(id_ > 0){
                        notify = "Todo updated!"
                        updateTodo()
                    } else {
                        notify = "Todo Saved!"
                        insertTodo()
                    }
                    Snackbar.make(view, notify!!, Snackbar.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }

        binding.dropDownImp.setAdapter(ArrayAdapter(requireContext(), R.layout.custom_spinner, importance))
        binding.dropDown.setAdapter(ArrayAdapter(requireContext(), R.layout.custom_spinner, priorities))

        binding.dropDown.setOnItemClickListener { parent, view, position, id ->
            priority = priorities[position]
        }

        binding.dropDownImp.setOnItemClickListener { _, view, position, id ->
            imp = importance[position]
        }
    }

    private fun prepareUI(present_id: Int){
        Timber.d("AASHI: Observer_int: ${present_id}")
        viewModel.getById(present_id).observe(viewLifecycleOwner, Observer {
            Timber.d("AASHI: Observer: $it")
            gTodo = it
            binding.description.textView.text = it.description
            binding.description.editText.setText(it.description)
            binding.title.textView.text = it.title
            binding.title.editText.setText(it.title)
            binding.tag.textView.text = it.tag
            binding.tag.editText.setText(it.tag)
            binding.dropDown.setText(it.priority, false)
            binding.dropDownImp.setText(it.importance, false)
        })
    }

    private fun validate(view: View): Boolean{
        val title = binding.title.editText.text
        return if(title.isEmpty()){
            Snackbar.make(view, "Enter valid title!", Snackbar.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun insertTodo(){
        val todo = buildTodo()
        todo.displayOrder = Common.todos_size
        Common.todos_size++
        Timber.d("DAO_ENTITY_ ${todo}")
        viewModel.insert(todo)
    }

    private fun buildTodo() = Todo(
            binding.title.editText.text.toString(),
            binding.description.editText.text.toString(),
            priority,
            System.currentTimeMillis(),
            false,
            binding.tag.editText.text.toString(),
            0,
            imp
        )

    private fun updateTodo(){
        val todo = buildTodo()
        todo.id = gTodo.id
        todo.displayOrder = gTodo.displayOrder
        Timber.d("AASHI: UPDATE: $todo")
        viewModel.update(todo)
    }



}