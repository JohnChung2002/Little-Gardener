package com.example.littlegardener

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private const val SENT = 1
private const val RECEIVED = 2

class ChatAdapter(private val chatsHashmap: MutableMap<String, Message>): RecyclerView.Adapter<ChatAdapter.ViewChat>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewChat {
        val view = if (viewType == SENT) {
            LayoutInflater.from(parent.context).inflate(R.layout.sent_message, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.received_message, parent, false)
        }
        return ViewChat(view)
    }

    override fun getItemCount(): Int {
        return chatsHashmap.size
    }

    override fun onBindViewHolder(holder: ViewChat, position: Int) {
        val chatItem = chatsHashmap.values.elementAt(position)
        holder.bind(chatItem)
    }

    override fun getItemViewType(position: Int): Int {
        val chatItem = chatsHashmap.values.elementAt(position)
        if (chatItem.sender == AuthenticationHelper.getAuth().currentUser?.uid) {
            return SENT
        }
        return RECEIVED
    }

    class ViewChat(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message) {
            itemView.findViewById<TextView>(R.id.messageTextView).text = message.message
        }
    }
}