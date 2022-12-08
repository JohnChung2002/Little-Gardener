package com.example.littlegardener

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.merge
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FirestoreHelper {
    companion object {
        private fun getDatabase(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }

        fun getRole(listener: (String) -> Unit) {
            val db = getCurrUserDocument()
            db.get().addOnSuccessListener {
                listener(it.get("role").toString())
            }
        }

        fun createUserEntry(name: String) {
            val db = getCurrUserDocument()
            val data = hashMapOf(
                "name" to name,
                "role" to "User",
                "chatList" to listOf<String>(),
                "ordersList" to listOf<String>()
            )
            db.set(data)
        }

        fun getAccountName(id: String, listener: (String) -> Unit) {
            val db = getDatabase()
            db.collection("users").document(id).get().addOnSuccessListener {
                listener.invoke(it.get("name").toString())
            }
        }

        fun addProduct(product: Product) {
            val data: MutableMap<String, Any> = mutableMapOf()
            val db = getProductCollection()
            data["name"] = product.name
            data["description"] = product.description
            data["price"] = product.price
            data["category"] = product.category
            data["images"] = product.images
            data["seller"] = product.seller
            getRole {
                if (it == "Admin") {
                    db.add(data).addOnSuccessListener { ref ->
                        updateToCategories(ref.id, product.category)
                    }
                }
            }
        }

        fun updateProduct(product: Product) {
            val data: MutableMap<String, Any> = mutableMapOf()
            val db = getProductCollection()
            data["name"] = product.name
            data["description"] = product.description
            data["price"] = product.price
            data["category"] = product.category
            data["images"] = product.images
            data["seller"] = product.seller
            getRole {
                if (it == "Admin") {
                    db.document(product.id).set(data)
                }
            }
        }

        fun deleteProduct(id: String, category: String) {
            val db = getProductCollection()
            getRole {
                if (it == "Admin") {
                    db.document(id).delete()
                    removeFromCategories(id, category)
                }
            }
        }

        fun getProduct(id: String, listener: (Product) -> Unit) {
            val db = getProductCollection()
            db.document(id).get().addOnSuccessListener {
                val product = Product(
                    id = it.id,
                    name = it.get("name").toString(),
                    description = it.get("description").toString(),
                    price = it.get("price").toString().toDouble(),
                    category = it.get("category").toString(),
                    images = it.get("images") as List<String>,
                    seller = it.get("seller").toString()
                )
                listener.invoke(product)
            }.addOnFailureListener {
                listener.invoke(Product())
            }
        }

        fun getProductPrice(id: String, listener: (Double) -> Unit) {
            val db = getProductCollection()
            db.document(id).get().addOnSuccessListener {
                listener.invoke(it.get("price").toString().toDouble())
            }
        }

        fun checkIfChatExists(receiver: String, listener: (String) -> Unit) {
            val sender = AuthenticationHelper.getCurrentUserUid()
            val db = getDatabase().collection("users")
            db.document(sender).get().addOnSuccessListener { doc1 ->
                val chatList1 = doc1.get("chatList") as List<String>
                db.document(receiver).get().addOnSuccessListener { doc2 ->
                    val chatList2 = doc2.get("chatList") as List<String>
                    val chats = chatList1.intersect(chatList2.toSet())
                    if (chats.isEmpty()) {
                        listener.invoke("")
                    } else {
                        listener.invoke(chats.first())
                    }
                }
            }
        }

        fun addChatIdToUsers(chatId: String, receiver: String) {
            val sender = AuthenticationHelper.getCurrentUserUid()
            val db = getDatabase().collection("users")
            db.document(sender).update("chatList", FieldValue.arrayUnion(chatId))
            db.document(receiver).update("chatList", FieldValue.arrayUnion(chatId))
        }

        fun addProductToCart(product: Product) {
            val db = getCurrCartDocument()
            db.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    if (snapshot.get(product.seller) != null) {
                        val data = snapshot.get(product.seller) as Map<String, Map<String, Int>>
                        if (data.containsKey(product.id)) {
                            db.update("${product.seller}.${product.id}", FieldValue.increment(1))
                        } else {
                            db.update("${product.seller}.${product.id}", 1)
                        }
                        return@addOnSuccessListener
                    }
                }
                db.set(mapOf(product.seller to mapOf(product.id to 1)))
            }
        }

        fun removeProductFromCart(product: Product) {
            val db = getCurrCartDocument()
            db.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    if (snapshot.get(product.seller) != null) {
                        val data = snapshot.get(product.seller) as Map<String, Map<String, Int>>
                        if (data.containsKey(product.id)) {
                            db.update("${product.seller}.${product.id}", FieldValue.delete())
                            clearCartField(product.seller)
                        }
                    }
                }
            }
        }

        private fun clearCartField(seller: String) {
            val db = getCurrCartDocument()
            db.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    if (snapshot.get(seller) != null) {
                        val data = snapshot.get(seller) as Map<String, Map<String, Int>>
                        if (data.isEmpty()) {
                            db.update(seller, FieldValue.delete())
                        }
                    }
                }
            }
        }

        fun initNotification(id: String, listener: (Boolean) -> Unit) {
            val db = getNotificationCollection().document(id)
            db.get().addOnSuccessListener {
                if (!it.exists()) {
                    db.set(mapOf("notifications" to listOf<HashMap<String, String>>())).addOnSuccessListener {
                        listener.invoke(true)
                    }
                } else {
                    listener.invoke(true)
                }
            }
        }

        fun getNotificationCollection(): CollectionReference {
            return getDatabase().collection("notifications")
        }

        fun addNotification(id: String, notification: Notification) {
            val db = getNotificationCollection()
            db.document(id).get().addOnSuccessListener {
                if (it.exists()) {
                    db.document(id).update("notifications", FieldValue.arrayUnion(notification))
                } else {
                    val data = hashMapOf(
                        "notifications" to listOf(notification)
                    )
                    db.document(id).set(data)
                }
            }
        }

        fun getCategoriesCollection(): CollectionReference {
            return getDatabase().collection("categories")
        }

        private fun updateToCategories(id: String, category: String) {
            val db = getCategoriesCollection()
            db.document(category).get().addOnSuccessListener {
                if (it.exists()) {
                    db.document(category).update("items", FieldValue.arrayUnion(id))
                } else {
                    val data = hashMapOf(
                        "items" to listOf(id)
                    )
                    db.document(category).set(data)
                }
            }
        }

        private fun removeFromCategories(id: String, category: String) {
            val db = getCategoriesCollection()
            db.document(category).update("items", FieldValue.arrayRemove(id))
            db.document(category).get().addOnSuccessListener {
                if (it.get("items") as List<*> == listOf<Any>()) {
                    db.document(category).delete()
                }
            }

        }

        fun getProductCollection(): CollectionReference {
            return getDatabase().collection("products")
        }

        fun getCurrUserDocument(): DocumentReference {
            return getDatabase().collection("users").document(AuthenticationHelper.getCurrentUserUid())
        }

        fun getCurrCartDocument(): DocumentReference {
            return getDatabase().collection("carts").document(AuthenticationHelper.getCurrentUserUid())
        }

        fun initCart(listener: (Boolean) -> Unit) {
            val db = getCurrCartDocument()
            db.get().addOnSuccessListener {
                if (!it.exists()) {
                    db.set(HashMap<String, Object>()).addOnSuccessListener {
                        listener.invoke(true)
                    }
                } else {
                    listener.invoke(true)
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