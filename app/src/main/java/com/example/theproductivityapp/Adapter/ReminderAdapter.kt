package com.example.theproductivityapp.Adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.ReminderItemBinding
import com.example.theproductivityapp.db.Reminder
import com.example.theproductivityapp.db.Utils
import com.example.theproductivityapp.ui.UIHelper.ItemClickListener
import timber.log.Timber

class ReminderAdapter(
    var itemClickListener:ItemClickListener
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(var binding: ReminderItemBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.ncard.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, Utils.TAG, R.id.ncard)
            }
            binding.deleteReminder.setOnClickListener{
                itemClickListener.onItemClick(adapterPosition, Utils.TAG, R.id.deleteReminder)
            }
        }
    }

    var diffCallback = object : DiffUtil.ItemCallback<Reminder>(){
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.timeStampOfTodo == newItem.timeStampOfTodo
        }

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    var diff = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Reminder>) {
        diff.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderAdapter.ReminderViewHolder {
        return ReminderViewHolder(ReminderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ReminderAdapter.ReminderViewHolder, position: Int) {
        holder.binding.reminderTitle.text = diff.currentList[position].title
        holder.binding.reminderTime.text = convertDate(diff.currentList[position].remindTimeInMillis)
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM hh:mm", timeInMillis).toString()
}