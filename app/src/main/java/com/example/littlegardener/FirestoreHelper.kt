package com.example.littlegardener

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
                    "role" to "User",
                    "chatList" to listOf<String>(),
                    "ordersList" to listOf<String>()
                )
                db.collection("users").document(user.uid).set(data)
            }
        }

        fun getAccountName(id: String, listener: (String) -> Unit) {
            val db = getDatabase()
            db.collection("users").document(id).get().addOnSuccessListener {
                listener.invoke(it.get("name").toString())
            }
        }

        fun addProduct(product: Product) {
            val data: MutableMap<String, Any> = mutableMapOf()
            val db = getDatabase()
            data["name"] = product.name
            data["description"] = product.description
            data["price"] = product.price
            data["category"] = product.category
            data["images"] = product.images
            data["seller"] = product.seller
            getRole {
                if (it == "Admin") {
                    db.collection("products").add(data).addOnSuccessListener { ref ->
                        updateToCategories(ref.id, product.category)
                    }
                }
            }
        }

        fun updateProduct(product: Product) {
            println("updateProduct ${product.id}")
            val data: MutableMap<String, Any> = mutableMapOf()
            val db = getDatabase()
            data["name"] = product.name
            data["description"] = product.description
            data["price"] = product.price
            data["category"] = product.category
            data["images"] = product.images
            data["seller"] = product.seller
            getRole {
                if (it == "Admin") {
                    db.collection("products").document(product.id).set(data)
                }
            }
        }

        fun deleteProduct(id: String, category: String) {
            val db = getDatabase()
            getRole {
                if (it == "Admin") {
                    db.collection("products").document(id).delete()
                    removeFromCategories(id, category)
                }
            }
        }

        fun addNotification(id: String, notification: Notification) {
            val db = getDatabase()
            db.collection("notifications").document(id).get().addOnSuccessListener {
                if (it.exists()) {
                    db.collection("notifications").document(id).update("notifications", FieldValue.arrayUnion(notification))
                } else {
                    val data = hashMapOf(
                        "notifications" to listOf(notification)
                    )
                    db.collection("notifications").document(id).set(data)
                }
            }
        }

        private fun updateToCategories(id: String, category: String) {
            val db = getDatabase()
            db.collection("categories").document(category).get().addOnSuccessListener {
                if (it.exists()) {
                    db.collection("categories").document(category).update("items", FieldValue.arrayUnion(id))
                } else {
                    val data = hashMapOf(
                        "items" to listOf(id)
                    )
                    db.collection("categories").document(category).set(data)
                }
            }
        }

        private fun removeFromCategories(id: String, category: String) {
            val db = getDatabase()
            db.collection("categories").document(category).update("items", FieldValue.arrayRemove(id))
            db.collection("categories").document(category).get().addOnSuccessListener {
                if (it.get("items") as List<*> == listOf<Any>()) {
                    db.collection("categories").document(category).delete()
                }
            }

        }

        fun getProductCollection(): CollectionReference {
            return getDatabase().collection("products")
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

        fun getTimeInTimeZone(context: Context, time: String): String {
            val formatted = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            return ZonedDateTime
                .of(formatted, ZoneOffset.UTC)
                .withZoneSameInstant(ZoneOffset.of(context.resources.getString(R.string.time_zone)))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }
    }
}