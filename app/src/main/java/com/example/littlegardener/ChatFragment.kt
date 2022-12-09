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
        FirestoreHelper.getCurrUserDocument().addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            val chats = snapshot?.data?.get("chatList") as ArrayList<String>
            for (chatId in chats) {
                val ref = RealtimeDBHelper.getChatGroupReferences(chatId)
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatRecyclerList.clear()
                        for (party in snapshot.children) {
                            if (party.key != AuthenticationHelper.getCurrentUserUid()) {
                                FirestoreHelper.getAccountInfo(party.key.toString()) { name, image ->
                                    chatRecyclerList.add(ChatItem(chatId, name, party.key.toString(), image))
                                    messageAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChatFragment()
    }
}