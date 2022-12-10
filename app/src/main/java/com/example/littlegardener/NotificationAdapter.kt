package com.example.littlegardener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(private val notificationList: List<Notification>): RecyclerView.Adapter<NotificationAdapter.ViewNotification>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewNotification {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewNotification(view)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: ViewNotification, position: Int) {
        val notification = notificationList[position]
        holder.bind(notification)
    }

    class ViewNotification(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(notification: Notification) {
            itemView.findViewById<TextView>(R.id.notification_title).text = notification.title
            itemView.findViewById<TextView>(R.id.notification_description).text = notification.description
            itemView.findViewById<TextView>(R.id.notification_timestamp).text = FirestoreHelper.getTimeInTimeZone(itemView.context, notification.timestamp)
        }
    }
}