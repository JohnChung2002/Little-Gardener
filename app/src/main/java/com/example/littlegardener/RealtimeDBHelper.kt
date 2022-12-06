package com.example.littlegardener

import android.util.Log
import com.google.firebase.database.*

class RealtimeDBHelper {
    companion object {
        fun getDatabase(): FirebaseDatabase {
            return FirebaseDatabase.getInstance()
        }

        fun getChatGroupReferences(id: String): DatabaseReference {
            return getDatabase().getReference("chats").child(id).child("parties")
        }

        fun getChatReference(chatId: String):DatabaseReference {
            return getDatabase().getReference("chats").child(chatId).child("messages")
        }

        fun loadUserNameId(id: String, listener: (String, String) -> Unit) {
            val sender = AuthenticationHelper.getAuth().currentUser?.uid
            val db = getDatabase()
            db.getReference("chats").child(id).child("parties").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val parties = snapshot.children
                    parties.forEach {
                        if (it.key != sender) {
                            FirestoreHelper.getAccountName(it.key.toString()) { name ->
                                listener.invoke(name, it.key.toString())
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        fun pushMessage(chatId: String, message: Message) {
            val ref = getChatReference(chatId)
            val key = ref.push().key
            if (key != null) {
                ref.child(key).setValue(message)
            }
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