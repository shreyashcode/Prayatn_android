package com.example.theproductivityapp.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.TypedArrayUtils.getString
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
                itemClickListener.onItemClick(adapterPosition, Utils.TODO)
            }
        }
    }

    val diffCallback = object : DiffUtil.ItemCallback<Todo>(){
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Todo, newItem: Todo) = oldItem.hashCode() == newItem.hashCode()
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(todos: List<Todo>) {
        Timber.d("INCLUDE: Inside submit Todo")
        differ.submitList(todos)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        var todo = differ.currentList[position]
        Timber.d("ICE: OnBind: TodoAdapter Postion: ${position}  ${todo}")
//        if(position >= differ.currentList.size-2){
//            Timber.d("Changed***********************")
//            holder.binding.ncard.setShadowColorDark(Color.parseColor("#1a1a1a"))
//            holder.binding.ncard.setShadowColorLight(Color.parseColor("#1a1a1a"))
//            holder.binding.itemPriority.text = ""
//            holder.binding.itemTitle.text = ""
//        } else {
//            holder.binding.ncard.setShadowColorDark(Color.parseColor("#15151a"))
//            holder.binding.ncard.setShadowColorLight(Color.parseColor("#242426"))
            Timber.d("INSIDE: ${position} ")

        if(todo.priority == ""){
            holder.binding.ncard.setShadowColorLight(ContextCompat.getColor(context, R.color.ui_light2))
            holder.binding.ncard.setShadowColorDark(ContextCompat.getColor(context, R.color.ui_light2))
            holder.binding.itemTitle.text = "Extra_added"
            holder.binding.ncard
        } else {
            holder.binding.itemTitle.text = differ.currentList[position].title
            holder.binding.ncard.setShadowColorLight(Color.parseColor("#fdfefe"))
            holder.binding.ncard.setShadowColorDark(Color.parseColor("#ADBACB"))
        }
//        }
    }

    override fun getItemCount() = differ.currentList.size

}