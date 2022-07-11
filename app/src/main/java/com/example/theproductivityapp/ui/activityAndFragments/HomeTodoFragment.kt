package com.example.theproductivityapp.ui.activityAndFragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.theproductivityapp.utils.Common
import com.example.theproductivityapp.Adapter.TagAdapter
import com.example.theproductivityapp.Adapter.TodoAdapter
import com.example.theproductivityapp.R
import com.example.theproductivityapp.service.ReminderService
import com.example.theproductivityapp.databinding.FragmentHomeTodoBinding
import com.example.theproductivityapp.db.tables.GraphTodo
import com.example.theproductivityapp.db.tables.Todo
import com.example.theproductivityapp.db.Utils
import com.example.theproductivityapp.utils.ItemClickListener
import com.example.theproductivityapp.ui.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@AndroidEntryPoint
class HomeTodoFragment : Fragment(R.layout.fragment_home_todo), ItemClickListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentHomeTodoBinding
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var tagAdapter: TagAdapter
    private lateinit var list: List<Todo>
    private lateinit var listToTag: List<Todo>
    private lateinit var itemClickListener: ItemClickListener
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var view_: View
    lateinit var paint: Paint
    lateinit var background: Drawable
    lateinit var graphTodo: GraphTodo
    private lateinit var reminderService: ReminderService

    override fun onResume() {
        super.onResume()
        binding.rView.smoothScrollToPosition(0)
        binding.tagRview.smoothScrollToPosition(0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view_ = view
        itemTouchHelper = ItemTouchHelper(callBack)
        binding = FragmentHomeTodoBinding.bind(view)
        itemClickListener = this
        setUpTagRecyclerView()
        if(requireActivity().intent.getStringExtra("TYPE") != null && !requireActivity().intent.getBooleanExtra("NavigateStatus", true)){
            val source = requireActivity().intent.getStringExtra("TYPE")
            if(source == "Standup"){
                requireActivity().intent.putExtra("NavigateStatus", true)
                findNavController().navigate(R.id.action_homeTodo_to_dailyStandupFragment)
            } else {
                Common.reqTimeStamp = requireActivity().intent.getLongExtra("timeStamp", 0L)
                requireActivity().intent.putExtra("NavigateStatus", true)
                findNavController().navigate(R.id.action_homeTodo_to_addTodoFragment)
            }
        }
        reminderService = ReminderService(requireContext())
        viewModel.reminders.observe(viewLifecycleOwner) {it->
            for (i in it) {
                if (i.remindTimeInMillis < System.currentTimeMillis()) {
                    reminderService.cancelReminder(i.timeStampOfTodo, i.remindTimeInMillis)
                    viewModel.deleteReminder(i)
                }
            }
        }
        viewModel.graphTodos.observe(viewLifecycleOwner, {it->
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

            for(graphTodo_ in it){
                if(graphTodo_.month == month && graphTodo_.date == date){
                    graphTodo = graphTodo_
                    break
                }
            }
        })

        viewModel.todos.observe(viewLifecycleOwner, Observer{
            var order: Int = 0
            for(i in it){
                order = Math.max(order, i.displayOrder)
            }
            val listToTag_ = mutableListOf<Todo>()
            val set = mutableSetOf<String>()
            for(i in it){
                if(set.contains(i.tag) == false){
                    listToTag_.add(i)
                    set.add(i.tag)
                }
            }
            listToTag = listToTag_
            list = it

            Common.todos_size = order+1
            tagAdapter.submitList(listToTag)
            todoAdapter.submitList(it)
            if(binding.progress.visibility == View.VISIBLE){
                binding.progress.visibility = View.GONE
            }
        })
        binding.setting.setOnClickListener{
            changeLayoutManager()
        }
    }

    private var callBack = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT)){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {

            val start = viewHolder.adapterPosition
            val end = target.adapterPosition
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

            binding.rView.adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            todoAdapter.submitList(list)
            return true
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {

            val itemView = viewHolder.itemView
            paint = Paint()
            background = ColorDrawable()
            val leftBG: Int = Color.parseColor("#f53b02")
            val leftLabel: String = "Delete!"
            val leftIcon: Drawable? = AppCompatResources.
            getDrawable(requireContext(), R.drawable.ic_trash)

            val rightBG: Int = Color.parseColor("#25cc04")
            val rightLabel: String = "Done!"
            val rightIcon: Drawable? = AppCompatResources.
            getDrawable(requireContext(), R.drawable.tick)
            paint.color = Color.WHITE
            paint.textSize = 48f
            paint.textAlign = Paint.Align.CENTER
            background = ColorDrawable();

            if (dX != 0.0f) {

                if (dX > 0) {
                    //right swipe
                    val intrinsicHeight = (rightIcon?.intrinsicWidth ?: 0)
                    val xMarkTop = itemView.top + ((itemView.bottom - itemView.top) - intrinsicHeight) / 2
                    val xMarkBottom = xMarkTop + intrinsicHeight

                    colorCanvas(c, rightBG, itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
                    drawTextOnCanvas(c, rightLabel, (itemView.left + 200).toFloat(), (xMarkTop + 10).toFloat())
                    drawIconOnCanVas(
                        c, rightIcon, itemView.left + (rightIcon?.intrinsicWidth ?: 0) + 50,
                        xMarkTop + 20,
                        itemView.left + 2 * (rightIcon?.intrinsicWidth ?: 0) + 50,
                        xMarkBottom + 20
                    )

                } else {
                    //left swipe
                    val intrinsicHeight = (leftIcon?.intrinsicWidth ?: 0)
                    val xMarkTop = itemView.top + ((itemView.bottom - itemView.top) - intrinsicHeight) / 2
                    val xMarkBottom = xMarkTop + intrinsicHeight

                    colorCanvas(
                        c,
                        leftBG,
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    drawTextOnCanvas(c, leftLabel, (itemView.right - 200).toFloat(), (xMarkTop + 10).toFloat())
                    drawIconOnCanVas(
                        c, leftIcon, itemView.right - 2 * (leftIcon?.intrinsicWidth ?: 0) - 70,
                        xMarkTop + 20,
                        itemView.right - (leftIcon?.intrinsicWidth ?: 0) - 70,
                        xMarkBottom + 20
                    )
                }
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val toDelete = list[viewHolder.adapterPosition]
            var message = "Deleted"
            if(direction == ItemTouchHelper.RIGHT){
                //Add to 2nd DATABASE
                graphTodo.done_count++
                viewModel.updateGraph(graphTodo)
                message = "Marked as done"
            }
            viewModel.delete(toDelete)

            Snackbar.make(binding.myCoordinatorLayout, message, Snackbar.LENGTH_SHORT).setAction("Undo", View.OnClickListener {
                viewModel.insert(toDelete)
                if(direction == ItemTouchHelper.RIGHT){
                    graphTodo.done_count--
                }
                viewModel.updateGraph(graphTodo)
            }).show()
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            val color = ContextCompat.getColor(
                requireContext(),
                R.color.primary_light_v1
            )
            viewHolder.itemView.setBackgroundColor(color)
            for(i in list){
                viewModel.update(i)
            }
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (viewHolder != null) {
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    private fun setUpRecyclerView() = binding.rView.apply {
        todoAdapter = TodoAdapter(itemClickListener, requireContext())
        adapter = todoAdapter
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        layoutManager = if(readSharedPref()){
            LinearLayoutManager(requireContext())
        } else {
            staggeredGridLayoutManager
        }
        itemTouchHelper.attachToRecyclerView(this)

    }

    private fun setUpTagRecyclerView() = binding.tagRview.apply {
        tagAdapter = TagAdapter(itemClickListener)
        adapter = tagAdapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager = linearLayoutManager
        val list = listOf(Todo("NULL", "NULL", "", System.currentTimeMillis(), true, "Sample", 0, ""))
        tagAdapter.submitList(list)
        setUpRecyclerView()
    }

    private fun changeLayoutManager(){
        if(!readSharedPref()){
            // sta -> linear
            binding.rView.layoutManager = LinearLayoutManager(requireContext())
            writeSharedPref(true)
        } else {
            writeSharedPref(false)
            binding.rView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        }
    }

    private fun writeSharedPref(isLinear: Boolean){
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(Utils.recyclerViewStatus, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(Utils.recyclerViewStatus, isLinear)
        editor.apply()
    }

    private fun readSharedPref(): Boolean{
        var val_ = ""
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(Utils.recyclerViewStatus, Context.MODE_PRIVATE)
        val_ = if(sharedPref.getBoolean(Utils.recyclerViewStatus, true)){
            "LINEAR"
        } else {
            "STA"
        }
        return sharedPref.getBoolean(Utils.recyclerViewStatus, true)
    }

    override fun onItemClick(int: Int, sender: String, viewId: Int) {
        val tag = list[int].tag
        if(sender == Utils.TAG){
            Common.tag = listToTag[int].tag
            findNavController().navigate(R.id.action_homeTodo_to_tagFilterFragment)
        } else {
            Common.reqId = list[int].id!!
            Common.reqTimeStamp = list[int].timestamp!!
            findNavController().navigate(R.id.action_homeTodo_to_addTodoFragment)
        }
    }

    fun colorCanvas(canvas: Canvas, canvasColor: Int, left: Int, top: Int, right: Int, bottom: Int): Unit {
        (background as ColorDrawable).color = canvasColor
        background.setBounds(left, top, right, bottom)
        background.draw(canvas)
    }

    fun drawTextOnCanvas(canvas: Canvas, label: String, x: Float, y: Float) {
        canvas.drawText(label, x, y, paint)
    }

    fun drawIconOnCanVas(
        canvas: Canvas, icon: Drawable?, left: Int, top: Int, right: Int, bottom: Int
    ): Unit {
        icon?.setBounds(left, top, right, bottom)
        icon?.draw(canvas)
    }
}