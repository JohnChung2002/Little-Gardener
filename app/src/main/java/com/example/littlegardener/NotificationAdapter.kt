package com.example.littlegardener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(): RecyclerView.Adapter<NotificationAdapter.ViewNotification>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewNotification {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewNotification(view)
    }

    override fun getItemCount(): Int {
        return 0
    }

    override fun onBindViewHolder(holder: ViewNotification, position: Int) {

    }

    class ViewNotification(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(notification: Notification) {

        }
    }
}