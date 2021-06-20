package com.example.theproductivityapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.theproductivityapp.Model.Tags
import com.example.theproductivityapp.databinding.TagItemBinding
import com.example.theproductivityapp.db.Todo
import com.example.theproductivityapp.db.Utils
import com.example.theproductivityapp.ui.UIHelper.ItemClickListener
import timber.log.Timber

class TagAdapter(var itemClickListener: ItemClickListener) : RecyclerView.Adapter<TagAdapter.TagViewHolder>(){

    inner class TagViewHolder(var binding: TagItemBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.tagCard.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, Utils.TAG)
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
        for(i in list){
            Timber.d("CSE_DDDDDDDDDDDD: ${i.title}, ${i.tag}, ${i.displayOrder}")
        }
        diff.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagAdapter.TagViewHolder {
        Timber.d("INCLUDE : ONCCRESTE")
        return TagViewHolder(TagItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TagAdapter.TagViewHolder, position: Int) {
        Timber.d("INCLUDE: ONBIND")
        holder.binding.tagId.text = diff.currentList[position].tag
    }

    override fun getItemCount(): Int {
        Timber.d("INCLUDE: SIZE: ${diff.currentList.size}")
        return diff.currentList.size
    }
}