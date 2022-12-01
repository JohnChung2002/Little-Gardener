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

        fun createUserEntry() {
            val db = getDatabase()
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val data = hashMapOf(
                    "name" to user.displayName,
                    "role" to "User"
                )
                db.collection("users").document(user.uid).set(data)
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
    }
}