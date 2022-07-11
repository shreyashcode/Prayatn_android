package com.example.theproductivityapp.ui.activityAndFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theproductivityapp.utils.Common
import com.example.theproductivityapp.Adapter.TodoAdapter
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.FragmentTagFilterBinding
import com.example.theproductivityapp.db.tables.Todo
import com.example.theproductivityapp.utils.ItemClickListener
import com.example.theproductivityapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TagFilterFragment : Fragment(R.layout.fragment_tag_filter), ItemClickListener {

    private lateinit var binding: FragmentTagFilterBinding
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var itemClickListener: ItemClickListener
    private lateinit var todos: List<Todo>
    private val viewModel:MainViewModel by viewModels()
    private lateinit var presentTodo: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTagFilterBinding.bind(view)
        itemClickListener = this
        setUpRecyclerView()
        presentTodo = Common.tag

        viewModel.getTodoByTAG(presentTodo).observe(viewLifecycleOwner, {
            todos = it
            todoAdapter.submitList(it)
        })

        binding.remove.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpRecyclerView() = binding.RViewFilter.apply {
        todoAdapter = TodoAdapter(itemClickListener, requireContext())
        adapter = todoAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onItemClick(int: Int, sender: String, viewId: Int) {
        Common.reqId = todos[int].id!!
        Common.reqTimeStamp = todos[int].timestamp
        findNavController().navigate(R.id.action_tagFilterFragment_to_addTodoFragment)
    }
}