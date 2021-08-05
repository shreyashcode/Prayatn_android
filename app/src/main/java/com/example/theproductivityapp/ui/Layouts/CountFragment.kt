package com.example.theproductivityapp.ui.Layouts

import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theproductivityapp.Adapter.ReminderAdapter
import com.example.theproductivityapp.R
import com.example.theproductivityapp.Service.ReminderService
import com.example.theproductivityapp.databinding.FragmentCountBinding
import com.example.theproductivityapp.db.Reminder
import com.example.theproductivityapp.ui.UIHelper.ItemClickListener
import com.example.theproductivityapp.ui.ViewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CountFragment : Fragment(R.layout.fragment_count), ItemClickListener{

    private val viewModel:MainViewModel by viewModels()
    private lateinit var binding: FragmentCountBinding
    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var currentReminderList: List<Reminder>
    private lateinit var reminderService: ReminderService
    private lateinit var snackbarView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCountBinding.bind(view)
        setUpRecyclerView(this)
        snackbarView = view
        reminderService = ReminderService(requireContext(), 0L, "NA")

        viewModel.reminders.observe(viewLifecycleOwner, {
            reminderAdapter.submitList(it)
            currentReminderList = it
        })

    }

    private fun setUpRecyclerView(itemClickListener: ItemClickListener){
        binding.reminderRview.apply {
            reminderAdapter = ReminderAdapter(itemClickListener)
            adapter = reminderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onItemClick(int: Int, sender: String, viewId: Int) {
        if(viewId == R.id.deleteReminder){
            reminderService.cancelReminder(currentReminderList[int].timeStampOfTodo,
                                            currentReminderList[int].remindTimeInMillis)
            viewModel.deleteReminder(currentReminderList[int])
        } else {
            Snackbar.make(snackbarView, "Press delete to remove alarm!", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM hh:mm", timeInMillis).toString()

}