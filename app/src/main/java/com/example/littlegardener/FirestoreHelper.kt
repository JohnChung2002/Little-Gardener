package com.example.littlegardener

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreHelper {
    companion object {
        fun getDatabase(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }

        fun getRole(listener: (String) -> Unit) {
            val db = getDatabase()
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                db.collection("users").document(user.uid).get().addOnSuccessListener {
                    listener(it.get("role").toString())
                }
            }
        }

        fun createUserEntry(name: String) {
            val db = getDatabase()
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val data = hashMapOf(
                    "name" to name,
                    "role" to "User"
                )
                db.collection("users").document(user.uid).set(data)
            }
        }

        fun getAccountName(id: String, listener:(String)->Unit) {
            val db = getDatabase()
            db.collection("users").document(id).get().addOnSuccessListener {
                listener.invoke(it.get("name").toString())
            }
        }

        fun getAccountInformation(listener:(String)->Unit) {
            val db = getDatabase()
            val auth = AuthenticationHelper.getAuth()
            db.collection("users").document(auth.currentUser?.uid?:"").get().addOnSuccessListener { documentSnapshot ->
                val email = auth.currentUser?.email
                val name = documentSnapshot.getString("name")
                val role = documentSnapshot.getString("role")
                listener.invoke(email + "\n" + name + "\n" + role)
            }
        }

        fun getChatList(listener: (List<String>) -> Unit) {
            val db = getDatabase()
            val auth = AuthenticationHelper.getAuth()
            db.collection("users").document(auth.currentUser?.uid?:"").get().addOnSuccessListener { documentSnapshot ->
                val chatList = documentSnapshot.get("chatList")
                if (chatList != null) {
                    listener.invoke(chatList as List<String>)
                } else {
                    listener.invoke(listOf())
                }
            }
        }
    }
}