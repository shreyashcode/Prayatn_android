package com.prayatnApp.theproductivityapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.prayatnApp.theproductivityapp.databinding.TodoItemBinding
import com.prayatnApp.theproductivityapp.db.tables.Todo
import com.prayatnApp.theproductivityapp.db.Utils
import com.prayatnApp.theproductivityapp.utils.ItemClickListener


class TodoAdapter(val itemClickListener: ItemClickListener,
                  var context: Context
    ): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){

    inner class TodoViewHolder(var binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ncard.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, Utils.TODO, 0)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Todo>(){
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Todo, newItem: Todo) = oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(todos: List<Todo>) {
        differ.submitList(todos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todoObject = differ.currentList[position]
        if(todoObject.emoji.isNotEmpty()){
            holder.binding.itemTitle.text = " ${todoObject.emoji}   ${differ.currentList[position].title}"
         } else {
            holder.binding.itemTitle.text = "✅   ".plus(todoObject.title)
        }
    }

    override fun getItemCount() = differ.currentList.size
}