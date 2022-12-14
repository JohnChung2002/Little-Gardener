package com.example.littlegardener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.Auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage

class ChatFragment : Fragment() {
    private lateinit var messageAdapter: MessageAdapter
    private var chatRecyclerList: MutableList<ChatItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realtimeDBListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        messageAdapter = MessageAdapter(chatRecyclerList)
        recyclerView.adapter = messageAdapter
        return view
    }

    private fun realtimeDBListener() {
        // Retrieve the current user's data from the database
        val db = FirestoreHelper.getCurrUserDocument()
        // Add a listener to the user document
        db.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            // Retrieve the user's chat list
            val chats = snapshot?.data?.get("chatList") as ArrayList<String>
            // Clear the recycler view chat list
            chatRecyclerList.clear()
            // Loop through the user's chat list
            for (chatId in chats) {
                // Retrieve the chat information from the database
                RealtimeDBHelper.getChatInfo(chatId) { chatItem ->
                    // Add the chat data to the recycler view chat list
                    chatRecyclerList.add(chatItem)
                    // Check if the chat status is unread
                    if (chatItem.status == "Unread") {
                        //if yes, trigger a notification to the user
                        val notification = Notification(title = "New message", description = "You have a new message from ${chatItem.name}")
                        FirestoreHelper.triggerNotification(this.requireContext(), notification)
                    }
                    // Notify the adapter that the data has changed
                    messageAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChatFragment()
    }
}