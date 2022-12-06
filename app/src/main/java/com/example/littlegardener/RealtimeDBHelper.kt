package com.example.littlegardener

import android.util.Log
import com.google.firebase.database.*

class RealtimeDBHelper {
    companion object {
        fun getDatabase(): FirebaseDatabase {
            return FirebaseDatabase.getInstance()
        }

        fun loadUserName(id: String, listener: (String) -> Unit) {
            val sender = AuthenticationHelper.getAuth().currentUser?.uid
            val db = getDatabase()
            db.getReference("chats").child(id).child("parties").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val parties = snapshot.children
                    parties.forEach {
                        if (it.key != sender) {
                            FirestoreHelper.getAccountName(it.key.toString()) { name ->
                                listener.invoke(name)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        fun getChatMessages(chatId: String, listener: (HashMap<String, Message>) -> Unit) {
            val messageHashmap: HashMap<String, Message> = hashMapOf()
            val db = getDatabase()
            db.getReference("chats").child(chatId).child("messages").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = snapshot.children
                    for (message in messages) {
                        val msg = message.getValue(Message::class.java)
                        if (msg != null) {
                            messageHashmap[message.key.toString()] = msg
                        }
                    }
                    listener.invoke(messageHashmap)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        fun loadChatGroups(listener: (List<ChatGroup>) -> Unit) {
            val database = getDatabase()
            FirestoreHelper.getChatList {
                for (chatGroup in it) {
                    val ref = database.getReference("chats/$chatGroup")
                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val chatGroup = snapshot.getValue(ChatGroup::class.java)
                            Log.e("ChatGroup", chatGroup.toString())
                            listener.invoke(listOf(chatGroup!!))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("RealtimeDBHelper", "loadChatGroups: ${error.message}")
                        }
                    })
                }

            }
        }
    }
}