package com.example.theproductivityapp.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.theproductivityapp.R
import com.example.theproductivityapp.databinding.TodoItemBinding
import com.example.theproductivityapp.db.Todo
import com.example.theproductivityapp.db.Utils
import com.example.theproductivityapp.ui.UIHelper.ItemClickListener
import timber.log.Timber

class TodoAdapter(val itemClickListener: ItemClickListener,
    var context: Context
    ): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){

    inner class TodoViewHolder(var binding: TodoItemBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.ncard.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, Utils.TODO, 0)
            }
        }
    }

    val diffCallback = object : DiffUtil.ItemCallback<Todo>(){
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Todo, newItem: Todo) = oldItem.hashCode() == newItem.hashCode()
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(todos: List<Todo>) {
        differ.submitList(todos)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        var todo = differ.currentList[position]

        if(todo.priority == ""){
            holder.binding.ncard.setShadowColorLight(ContextCompat.getColor(context, R.color.ui_light2))
            holder.binding.ncard.setShadowColorDark(ContextCompat.getColor(context, R.color.ui_light2))
            holder.binding.itemTitle.text = "Extra_added"
            holder.binding.ncard
        } else {
            val todoObject = differ.currentList[position]
            Timber.d("LAPTOP: ${todoObject.emoji} || ")
            if(todoObject.emoji.isNotEmpty()){
                holder.binding.itemTitle.text = " ${todoObject.emoji}   ${differ.currentList[position].title}"
             } else {
                holder.binding.itemTitle.text = "✅   ".plus(todoObject.title)
            }
            holder.binding.ncard.setShadowColorLight(Color.parseColor("#fdfefe"))
            holder.binding.ncard.setShadowColorDark(Color.parseColor("#ADBACB"))
        }
    }

    override fun getItemCount() = differ.currentList.size

}