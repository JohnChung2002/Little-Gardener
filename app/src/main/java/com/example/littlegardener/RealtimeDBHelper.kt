package com.example.littlegardener

import android.util.Log
import com.google.firebase.database.*

class RealtimeDBHelper {
    companion object {
        private fun getDatabase(): FirebaseDatabase {
            return FirebaseDatabase.getInstance()
        }

        private fun getChatGroupReferences(id: String): DatabaseReference {
            return getDatabase().getReference("chats").child(id).child("parties")
        }

        fun getChatReference(chatId: String):DatabaseReference {
            return getDatabase().getReference("chats").child(chatId).child("messages")
        }

        fun getChatInfo(chatId: String, listener: (ChatItem) -> Unit) {
            retrieveChatInfo(chatId) { chatItem ->
                FirestoreHelper.getAccountInfo(chatItem.receiver) { name, image ->
                    chatItem.name = name
                    chatItem.image = image
                    listener.invoke(chatItem)
                }
            }
        }

        private fun retrieveChatInfo(chatId: String, listener: (ChatItem) -> Unit) {
            val db = getChatGroupReferences(chatId)
            db.get().addOnSuccessListener {
                val chatItem = ChatItem(chatId, "", "", "", "")
                for (party in it.children) {
                    if (party.key == AuthenticationHelper.getCurrentUserUid()) {
                        chatItem.status = party.value.toString()
                    } else {
                        chatItem.receiver = party.key.toString()
                    }
                }
                listener.invoke(chatItem)
            }
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

        fun setMessageStatus(chatId: String, party: String, status: String) {
            getChatGroupReferences(chatId).child(party).setValue(status)
            FirestoreHelper.updateChatStatus(party)
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