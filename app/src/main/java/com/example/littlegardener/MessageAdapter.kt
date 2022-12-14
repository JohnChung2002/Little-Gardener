package com.example.littlegardener

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MessageAdapter(private val chatList: List<ChatItem>): RecyclerView.Adapter<MessageAdapter.ViewMessage>() {
    interface MessageListener {
        fun onMessageClicked(chatItem: ChatItem)
    }

    private lateinit var callback: MessageListener
    private lateinit var viewContext: Context
    private lateinit var viewActivity: Activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewMessage {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        viewContext = parent.context
        viewActivity = viewContext as Activity
        callback = viewActivity as MessageListener
        return ViewMessage(view)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewMessage, position: Int) {
        val chatItem = chatList[position]
        holder.itemView.setOnClickListener {
            callback.onMessageClicked(chatItem)
        }
        holder.bind(chatItem)
    }

    class ViewMessage(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(chatItem: ChatItem) {
            itemView.findViewById<TextView>(R.id.user_name).text = chatItem.name
            if (chatItem.image != "") {
                Glide.with(itemView).load(chatItem.image).into(itemView.findViewById(R.id.user_icon))
            } else {
                itemView.findViewById<ImageView>(R.id.user_icon).setImageResource(R.drawable.user_icon)
            }
            if (chatItem.status == "Read") {
                itemView.findViewById<ImageView>(R.id.unread_indicator).visibility = View.GONE
            } else {
                itemView.findViewById<ImageView>(R.id.unread_indicator).visibility = View.VISIBLE
            }
        }
    }
}