package com.example.littlegardener

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val chatList: List<ChatItem>): RecyclerView.Adapter<ChatAdapter.ViewChat>() {
    interface ChatListener {
        fun onMessageClicked(chatId: String, chatName: String)
    }

    private lateinit var callback: ChatListener
    private lateinit var viewContext: Context
    private lateinit var viewActivity: Activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewChat {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        viewContext = parent.context
        viewActivity = viewContext as Activity
        callback = viewActivity as ChatListener
        return ViewChat(view)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewChat, position: Int) {
        val chatItem = chatList[position]
        holder.itemView.setOnClickListener {
            callback.onMessageClicked(chatItem.id!!, chatItem.name)
        }
        holder.bind(chatItem)
    }

    class ViewChat(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(chatItem: ChatItem) {
            itemView.findViewById<TextView>(R.id.user_name).text = chatItem.name
        }
    }
}