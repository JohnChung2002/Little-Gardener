package com.example.littlegardener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationFragment : Fragment() {
    private lateinit var notificationAdapter: NotificationAdapter
    private var notificationRecyclerList: MutableList<Notification> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNotificationListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.notification_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        notificationAdapter = NotificationAdapter(notificationRecyclerList)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = notificationAdapter
        return view
    }

    private fun initNotificationListener() {
        FirestoreHelper.initNotification(AuthenticationHelper.getCurrentUserUid()) {
            val db = FirestoreHelper.getNotificationCollection().document(AuthenticationHelper.getCurrentUserUid())
            db.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val notifications = snapshot?.data?.get("notifications") as ArrayList<HashMap<String, String>>
                notificationRecyclerList.clear()
                for (i in notifications.indices) {
                    notifications[i].let {
                        notificationRecyclerList.add(Notification(it["title"]!!, it["description"]!!, it["timestamp"]!!))
                    }
                    notificationRecyclerList.sortByDescending { it.timestamp }
                    notificationAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationFragment()
    }
}