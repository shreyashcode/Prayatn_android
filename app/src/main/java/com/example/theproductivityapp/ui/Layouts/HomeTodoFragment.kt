package com.example.theproductivityapp.ui.Layouts

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.theproductivityapp.ui.UIHelper.Common
import com.example.theproductivityapp.Adapter.TagAdapter
import com.example.theproductivityapp.Adapter.TodoAdapter
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.FragmentHomeTodoBinding
import com.example.theproductivityapp.db.Todo
import com.example.theproductivityapp.db.Utils
import com.example.theproductivityapp.ui.UIHelper.ItemClickListener
import com.example.theproductivityapp.ui.ViewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class HomeTodoFragment : Fragment(R.layout.fragment_home_todo), ItemClickListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentHomeTodoBinding
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var tagAdapter: TagAdapter
    private lateinit var list: List<Todo>
    private lateinit var itemClickListener: ItemClickListener
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var view_: View

    override fun onResume() {
        super.onResume()
        binding.rView.smoothScrollToPosition(0)
        binding.tagRview.smoothScrollToPosition(0)
        Toast.makeText(requireContext(), "CALLED", Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_ = view
        itemTouchHelper = ItemTouchHelper(callBack)
        binding = FragmentHomeTodoBinding.bind(view)
        itemClickListener = this
        setUpTagRecyclerView()

        viewModel.todos.observe(viewLifecycleOwner, Observer{
//            Timber.d("INCLUDE: 1")
            var order: Int = 0
            for(i in it){
                order = Math.max(order, i.displayOrder)
                Timber.d("ONSWIPE : ${i}")
            }
            Timber.d("ONSWIPE: OUTSIDE")
            Timber.d("ONSWIPE: OUTSIDE")
            Timber.d("ONSWIPE: OUTSIDE")
//            if(it.isNotEmpty() && it[0].priority < 0){
//                Collections.reverse(it)
//            }
            val listToTag = mutableListOf<Todo>()
            val set = mutableSetOf<String>()

            for(i in it){
                if(set.contains(i.tag) == false){
                    listToTag.add(i)
                    set.add(i.tag)
                }
            }

            list = it

            Common.todos_size = order+1
            tagAdapter.submitList(listToTag)
            todoAdapter.submitList(it)

//            tagAdapter.notifyDataSetChanged()

            if(binding.progress.visibility == View.VISIBLE){
                binding.progress.visibility = View.GONE
            }
        })
    }

    private var callBack = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT)){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            var start = viewHolder.adapterPosition
            var end = target.adapterPosition
            Timber.d("Shreyash: ${start} ${end}")
            if(start < end){
                for(i in start until end){
                    val order1 = list[i].displayOrder
                    val order2 = list[i+1].displayOrder
                    list[i].displayOrder = order2
                    list[i+1].displayOrder = order1
                    Collections.swap(list, i, i+1)
                }
            } else {
                for(i in start downTo end+1){
                    val order1 = list[i].displayOrder
                    val order2 = list[i-1].displayOrder
                    list[i].displayOrder = order2
                    list[i-1].displayOrder = order1
                    Collections.swap(list, i, i-1)
                }
            }

            for(i in list){
                Timber.d("Shreyash: ${i}")
            }

            todoAdapter.submitList(list)
            binding.rView.adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val toDelete = list[viewHolder.adapterPosition]
            var removed = 0
            Timber.d("ONSWIPE1: ${list[viewHolder.adapterPosition]} | ${viewHolder.adapterPosition}")
//            binding.rView.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
            viewModel.delete(toDelete)
            Snackbar.make(requireView(), "Marked as done!", Snackbar.LENGTH_SHORT).setAction("Undo!", View.OnClickListener {
//                todoAdapter.submitList(list)
                Timber.d("ONSWIPE: OnClick")
                viewModel.insert(toDelete)
//                binding.rView.adapter?.notifyItemInserted(viewHolder.adapterPosition)
                removed++
            }).show()
            if(removed == 0){
                Timber.d("ONSWIPE: REMOVED")
            } else {
                Timber.d("ONSWIPE: INSERTED")
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#D5DCE5"))
//            viewHolder.itemView
//            viewHolder.itemView.ncard.setBackgroundColor(Color.parseColor("#D5DCE5"))
            for(i in list){
                viewModel.update(i)
            }
            Timber.d("Random: Updated!")
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (viewHolder != null) {
//                viewHolder.itemView.ncard.setBackgroundColor(Color.WHITE)
                viewHolder.itemView.setBackgroundColor(Color.WHITE)
            }
        }
    }

    private fun setUpRecyclerView() = binding.rView.apply {
        Timber.d("INCLUDE: Todo setup")
        todoAdapter = TodoAdapter(itemClickListener, requireContext())
        adapter = todoAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemTouchHelper.attachToRecyclerView(this)
    }

    private fun setUpTagRecyclerView() = binding.tagRview.apply {
        Timber.d("INCLUDE: Tag setup")
        tagAdapter = TagAdapter(itemClickListener)
        adapter = tagAdapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager = linearLayoutManager
        val list = listOf(Todo("NULL", "NULL", "", System.currentTimeMillis(), true, "Sample", 0, ""))
        tagAdapter.submitList(list)
        setUpRecyclerView()
    }

    override fun onItemClick(int: Int, sender: String) {
        val tag = list[int].tag
        if(sender == Utils.TAG){
            val filter = mutableListOf<Todo>()
            for(todo in list){
                if(todo.tag == tag){
                    filter.add(todo)
                }
            }
            Common.todos = filter
            Toast.makeText(requireContext(), "TAG: ${list[int].tag}", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_homeTodo_to_tagFilterFragment)
        } else {
            Toast.makeText(requireContext(), "TOD: ${list[int].title}", Toast.LENGTH_SHORT).show()
            Timber.d("XYZC: ${list[int]}")
//            val action = list[int].id?.let {
//                HomeTodoFragmentDirections.actionHomeTodoToAddTodoFragment(
//                    it
//                )
//            }
            Common.reqId = list[int].id!!
            findNavController().navigate(R.id.action_homeTodo_to_addTodoFragment)
        }
    }
}