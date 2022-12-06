package com.example.littlegardener

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private val chatsHashmap: MutableMap<String, Message>): RecyclerView.Adapter<MessageAdapter.ViewMessage>() {
    private lateinit var viewContext: Context
    private lateinit var viewActivity: Activity

    val SENT = 1
    val RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewMessage {
        if (viewType == SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.sent_message, parent, false)
            viewContext = parent.context
            viewActivity = viewContext as Activity
            return ViewMessage(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.received_message, parent, false)
            viewContext = parent.context
            viewActivity = viewContext as Activity
            return ViewMessage(view)
        }
    }

    override fun getItemCount(): Int {
        return chatsHashmap.size
    }

    override fun onBindViewHolder(holder: ViewMessage, position: Int) {
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

    class ViewMessage(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message) {
            itemView.findViewById<TextView>(R.id.user_name).text = message.message
        }
    }
}