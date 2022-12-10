package com.example.littlegardener

import android.util.Log
import com.google.firebase.database.*

class RealtimeDBHelper {
    companion object {
        private fun getDatabase(): FirebaseDatabase {
            return FirebaseDatabase.getInstance()
        }

        fun getChatGroupReferences(id: String): DatabaseReference {
            return getDatabase().getReference("chats").child(id).child("parties")
        }

        fun getChatReference(chatId: String):DatabaseReference {
            return getDatabase().getReference("chats").child(chatId).child("messages")
        }

        fun createChat(receiver: String, listener: (String) -> Unit) {
            val sender = AuthenticationHelper.getCurrentUserUid()
            val ref = getDatabase().getReference("chats")
            val chatId = ref.push().key
            val chatGroup = ChatGroup(id = chatId!!, parties = hashMapOf(sender to "Read", receiver to "Read"))
            FirestoreHelper.addChatIdToUsers(chatId, receiver)
            ref.child(chatId).setValue(
                hashMapOf(
                    "parties" to chatGroup.parties,
                    "messages" to hashMapOf<String, Message>()
                )
            ).addOnSuccessListener {
                listener.invoke(chatId)
            }
        }

        private fun setMessageStatus(chatId: String, party: String, status: String) {
            getChatGroupReferences(chatId).child(party).setValue(status)
        }

        fun pushMessage(chatId: String, party: String, message: Message) {
            val ref = getChatReference(chatId)
            val key = ref.push().key
            if (key != null) {
                ref.child(key).setValue(message)
            }
            setMessageStatus(chatId, party, "Unread")
        }
    }
}