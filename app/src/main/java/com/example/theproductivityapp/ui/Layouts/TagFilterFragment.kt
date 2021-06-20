package com.example.theproductivityapp.ui.Layouts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theproductivityapp.ui.UIHelper.Common
import com.example.theproductivityapp.Adapter.TodoAdapter
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.FragmentTagFilterBinding
import com.example.theproductivityapp.db.Todo
import com.example.theproductivityapp.ui.UIHelper.ItemClickListener
import timber.log.Timber

class TagFilterFragment : Fragment(R.layout.fragment_tag_filter), ItemClickListener {

    private lateinit var binding: FragmentTagFilterBinding
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var itemClickListener: ItemClickListener
    private lateinit var todos: List<Todo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("KKKKKKKKKKKK: Fragment ONCREATE!")
    }

    init{
        Timber.d("KKKKKKKKKKKK: Fragment!")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTagFilterBinding.bind(view)

        todos = Common.todos
        Timber.d("KKKKKKKKKKKK: Fragment2!")
        for(i in todos){
            Timber.d("KKKKKKKKKKKK11: ${i}")
        }
        Timber.d("KKKKKKKKKKKK: ${todos.size}")
        itemClickListener = this
        setUpRecyclerView()
        binding.remove.setOnClickListener {
//            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
            findNavController().popBackStack()
//            findNavController().navigate(R.id.action_tagFilterFragment_to_homeTodo)
        }
    }

    private fun setUpRecyclerView() = binding.RViewFilter.apply {
        Timber.d("INCLUDE: Todo setup")
        todoAdapter = TodoAdapter(itemClickListener, requireContext())
        adapter = todoAdapter
        layoutManager = LinearLayoutManager(requireContext())
        todoAdapter.submitList(todos)
    }

    override fun onItemClick(int: Int, sender: String) {
        Toast.makeText(requireContext(), "Congrats! ${todos[int].title}", Toast.LENGTH_SHORT).show()
    }
}