package com.prayatnApp.theproductivityapp.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.prayatnApp.theproductivityapp.databinding.BotMessageItemBinding
import com.prayatnApp.theproductivityapp.databinding.UserMessageItemBinding
import com.prayatnApp.theproductivityapp.db.tables.ChatMessage
import com.prayatnApp.theproductivityapp.db.tables.Sender

class ChatAdapter: RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    var items = listOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return if (viewType == 0) ChatViewHolder.BotMessageViewHolder(BotMessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else ChatViewHolder.UserMessageViewHolder(UserMessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position].sender){
            Sender.BOT -> 0
            Sender.USER -> 1
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    sealed class ChatViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        class UserMessageViewHolder(private val userMessageItemBinding: UserMessageItemBinding): ChatViewHolder(userMessageItemBinding){
            override fun bind(chatMessage: ChatMessage){
                userMessageItemBinding.message.text = chatMessage.description
                userMessageItemBinding.date.text = convertDate(chatMessage.timeStamp)
            }
        }
        class BotMessageViewHolder(private val botMessageItemBinding: BotMessageItemBinding): ChatViewHolder(botMessageItemBinding){
            override fun bind(chatMessage: ChatMessage){
                botMessageItemBinding.message.text = chatMessage.description
            }
        }
        abstract fun bind(chatMessage: ChatMessage)
        companion object{
            private fun convertDate(timeInMillis: Long): String =
                DateFormat.format("dd/MM hh:mm", timeInMillis).toString()
        }
    }
}
