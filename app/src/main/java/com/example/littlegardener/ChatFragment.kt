package com.example.littlegardener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatFragment : Fragment() {
    private lateinit var chatRecyclerList: List<ChatItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chatRecyclerList = it.get("chatList") as List<ChatItem>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MessageAdapter(chatRecyclerList)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: List<ChatItem>) = ChatFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList("chatList", param1 as ArrayList<ChatItem>)
            }
        }
    }
}