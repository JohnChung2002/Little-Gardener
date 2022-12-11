package com.example.littlegardener

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

private const val SENT_MESSAGE = 1
private const val RECEIVED_MESSAGE = 2
private const val SENT_IMAGE = 3
private const val RECEIVED_IMAGE = 4

class ChatAdapter(private val chatsHashmap: MutableMap<String, Message>): RecyclerView.Adapter<ChatAdapter.ViewChat>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewChat {
        val view = when (viewType) {
            SENT_MESSAGE -> {
                LayoutInflater.from(parent.context).inflate(R.layout.sent_message, parent, false)
            }
            RECEIVED_MESSAGE -> {
                LayoutInflater.from(parent.context).inflate(R.layout.received_message, parent, false)
            }
            SENT_IMAGE -> {
                LayoutInflater.from(parent.context).inflate(R.layout.sent_image, parent, false)
            }
            else -> {
                LayoutInflater.from(parent.context).inflate(R.layout.received_image, parent, false)
            }
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
        return if (chatItem.sender == AuthenticationHelper.getAuth().currentUser?.uid) {
            if (chatItem.type == "message") {
                SENT_MESSAGE
            } else {
                SENT_IMAGE
            }
        } else {
            if (chatItem.type == "message") {
                RECEIVED_MESSAGE
            } else {
                RECEIVED_IMAGE
            }
        }
    }

    class ViewChat(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message) {
            if (message.type == "message") {
                itemView.findViewById<TextView>(R.id.message_text_view).text = message.message
            } else {
                val imageView = itemView.findViewById<ImageView>(R.id.message_image_view)
                Glide
                    .with(itemView.context)
                    .load(message.message)
                    .into(imageView)
                imageView.setOnClickListener {
                    val intent = Intent(itemView.context, FullScreenImageActivity::class.java)
                    intent.putExtra("image", message.message)
                    itemView.context.startActivity(intent)
                }
            }
            itemView.findViewById<TextView>(R.id.message_timestamp).text = FirestoreHelper.getTimeInTimeZone(itemView.context, message.timestamp)
        }
    }
}