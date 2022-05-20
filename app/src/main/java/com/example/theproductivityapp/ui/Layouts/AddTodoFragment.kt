package com.example.theproductivityapp.ui.Layouts

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.FragmentAddTodoBinding
import com.example.theproductivityapp.db.GraphTodo
import com.example.theproductivityapp.db.Todo
import com.example.theproductivityapp.db.Utils
import com.example.theproductivityapp.Utils.Common
import com.example.theproductivityapp.ui.ViewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import android.text.format.DateFormat
import com.example.theproductivityapp.Service.ReminderService
import com.example.theproductivityapp.db.Reminder


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
    private var globalTimeInMillis = 0L
    private lateinit var gTodo: Todo
    private lateinit var graphTodo: GraphTodo
    private var isNew = true
    private var isReminderApplied = false
    private var addReminder = false
    private var showReminderOption = false
    private var reminderTime: Long = 0L

    override fun onResume() {
        super.onResume()
        prepareUI(Common.reqTimeStamp)
        importance = resources.getStringArray(R.array.Importance)
        priorities = resources.getStringArray(R.array.Priority)
        binding.dropDownImp.setAdapter(ArrayAdapter(requireContext(), R.layout.custom_spinner, importance))
        binding.dropDown.setAdapter(ArrayAdapter(requireContext(), R.layout.custom_spinner, priorities))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddTodoBinding.bind(view)

        Toast.makeText(requireContext(), "Tap edit to start!", Toast.LENGTH_SHORT).show()

        importance = resources.getStringArray(R.array.Importance)
        priorities = resources.getStringArray(R.array.Priority)
        priority = priorities[0]
        imp = importance[0]
        binding.dropDownImp.isClickable = false

        disableEditingMode()
        viewModel.reminderByTimestamp(Common.reqTimeStamp).observe(viewLifecycleOwner, {
            if(it.isNotEmpty()){
                binding.reminderSwitch.visibility = View.INVISIBLE
                binding.showAlarm.text = convertDate(it[0].remindTimeInMillis)
            }
        })
        prepareUI(Common.reqTimeStamp)

        viewModel.graphTodos.observe(viewLifecycleOwner, {
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

        binding.toggleButton.bind(binding.title, binding.description, binding.tag, binding.emoji)

        binding.toggleButton.setOnClickListener {
            if(binding.title.getEditing()){
                disableEditingMode()
                if(validate(view)){
                    binding.toggleButton.visibility = View.INVISIBLE

                    // ID problem
                    var notify: String? = null
                    if(isNew == false){
                        notify = "Todo updated!"
                        updateTodo(globalTimeInMillis)
                    } else {
                        notify = "Todo Saved!"
                        insertTodo(globalTimeInMillis)
                    }
                    Snackbar.make(view, notify!!, Snackbar.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            } else {
                editingMode()
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

        binding.reminderSwitch.setOnCheckedChangeListener{_, checked->
            addReminder = checked
            if(checked == true){
                getTimeInMillis{
                    setTime(it)
                    binding.showAlarm.text = convertDate(it)
                }
            } else {
                binding.showAlarm.text = "Reminder?"
            }
        }
    }

    private fun setTime(timeInMillis: Long) {
        globalTimeInMillis = timeInMillis
        isReminderApplied = true
        binding.showAlarm.text = convertDate(timeInMillis)
    }

    private fun getTimeInMillis(callback: (Long) -> Unit): Long{
        var timeInMillis: Long = 0L
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            DatePickerDialog(
                requireContext(),
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    TimePickerDialog(
                        requireContext(),
                        0,
                        { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            timeInMillis = this.timeInMillis
                            callback(this.timeInMillis)
//                            binding.showAlarm.text = convertDate(timeInMillis)
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()
                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        Toast.makeText(requireContext(), "CLOCK: ${convertDate(timeInMillis)}", Toast.LENGTH_SHORT).show()
        return timeInMillis
    }

    private fun prepareUI(timeStamp: Long){
        viewModel.getTodoByTimeStamp(timeStamp).observe(viewLifecycleOwner, Observer {list_->
            if(list_.isNotEmpty() == true){
                isNew = false
                val it = list_[0]
                gTodo = it
                binding.emoji.textView.text = it.emoji
                // Check?
                binding.emoji.editText.setText(it.emoji)
                binding.description.textView.text = it.description
                binding.description.editText.setText(it.description)
                binding.title.textView.text = it.title
                binding.title.editText.setText(it.title)
                binding.priorityText.text = it.priority
                binding.importanceText.text = it.importance
                binding.tag.textView.text = it.tag
                binding.tag.editText.setText(it.tag)
                binding.dropDown.setText(it.priority, false)
                binding.dropDownImp.setText(it.importance, false)
            }
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

    private fun insertTodo(timeInMillis: Long){
        val todo = buildTodo()
        if(addReminder == true){
            setReminder(timeInMillis, todo.timestamp, todo.title)
            todo.isReminderSet = true
        }
        todo.emoji = binding.emoji.editText.text.toString()
        Toast.makeText(requireContext(), "$ ${convertDate(globalTimeInMillis)}", Toast.LENGTH_SHORT).show()
        todo.displayOrder = Common.todos_size
        Common.todos_size++
        writeSharedPref(todo.priority, todo.importance)
        viewModel.insert(todo)
        graphTodo.added_count++
        viewModel.updateGraph(graphTodo)
    }

    private fun setReminder(remindTime: Long, todo_timestamp: Long, title: String){
        viewModel.insertReminder(Reminder(remindTime, title, todo_timestamp))
        Toast.makeText(requireContext(), "You would be reminded at ${convertDate(remindTime)}!", Toast.LENGTH_SHORT).show()
        val reminderService = ReminderService(requireContext(), todo_timestamp, title)
        reminderService.setExactAlarm(remindTime)
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

    private fun updateTodo(remindTime: Long){
        val todo = buildTodo()
        if(addReminder == true){
            setReminder(remindTime, todo.timestamp, todo.title)
            todo.isReminderSet = true
        }
        todo.emoji = binding.emoji.editText.text.toString()
        todo.id = gTodo.id
        todo.displayOrder = gTodo.displayOrder
        viewModel.update(todo)
    }

    private fun editingMode(){
        binding.save.text = "Save"
        binding.reminderSwitch.visibility = View.VISIBLE
        binding.customDropDown.visibility = View.VISIBLE
        binding.customDropDownImp.visibility = View.VISIBLE
        binding.importanceText.visibility = View.INVISIBLE
        binding.priorityText.visibility = View.INVISIBLE
    }

    private fun disableEditingMode(){
        binding.save.text = "Edit"
        binding.reminderSwitch.visibility = View.INVISIBLE
        binding.customDropDownImp.visibility = View.INVISIBLE
        binding.customDropDown.visibility = View.INVISIBLE
        binding.importanceText.visibility = View.VISIBLE
        binding.priorityText.visibility = View.VISIBLE
    }

    private fun writeSharedPref(priority: String, imp: String){
        var key: String = imp+priority;
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(Utils.QuadrantSharedPrefs, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val getValue: Float = readSharedPref(key)
        editor.putFloat(key, getValue.plus(1f))
        editor.apply()
    }

    private fun readSharedPref(key: String): Float{
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(Utils.QuadrantSharedPrefs, Context.MODE_PRIVATE)
        return sharedPref.getFloat(key, 0f)
    }

    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM hh:mm", timeInMillis).toString()
}