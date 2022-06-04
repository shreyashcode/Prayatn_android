package com.example.theproductivityapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.theproductivityapp.databinding.TagItemBinding
import com.example.theproductivityapp.db.tables.Todo
import com.example.theproductivityapp.db.Utils
import com.example.theproductivityapp.ui.UIHelper.ItemClickListener

class TagAdapter(var itemClickListener: ItemClickListener) : RecyclerView.Adapter<TagAdapter.TagViewHolder>(){

    inner class TagViewHolder(var binding: TagItemBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.tagCard.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, Utils.TAG, 0)
            }
        }
    }

    var diffCallback = object : DiffUtil.ItemCallback<Todo>(){
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    var diff = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Todo>) {
        diff.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagAdapter.TagViewHolder {
        return TagViewHolder(TagItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TagAdapter.TagViewHolder, position: Int) {
        holder.binding.tagId.text = diff.currentList[position].tag
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }
}