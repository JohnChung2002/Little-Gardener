package com.example.littlegardener

import android.content.Context
import com.google.firebase.firestore.*
import java.time.Instant
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
                "image" to ""
            )
            db.set(data)
        }

        fun getAccountInfo(id: String, listener: (String, String) -> Unit) {
            val db = getDatabase()
            db.collection("users").document(id).get().addOnSuccessListener {
                listener.invoke(it.get("name").toString(), it.get("image").toString())
            }
        }

        fun updateProfileName(name: String) {
            val db = getCurrUserDocument()
            db.update("name", name)
        }

        fun updateProfileImage(image: String) {
            val db = getCurrUserDocument()
            db.update("image", image)
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
                    db.add(data)
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
                }
            }
        }

        fun getOrderProduct(orderId: String, id: String, listener: (Product) -> Unit) {
            val db = getOrdersCollection().document(orderId)
            db.get().addOnSuccessListener {
                try {
                    val info = it.data as HashMap<String, Any>
                    val productMap = info["products"] as HashMap<String, Any>
                    val product = productMap[id] as HashMap<String, Any>
                    val returnProduct = Product(
                        id,
                        product["name"].toString(),
                        product["price"].toString().toDouble(),
                        product["description"].toString(),
                        product["category"].toString(),
                        product["images"] as List<String>,
                        product["seller"].toString(),
                        product["quantity"].toString().toInt()
                    )
                    listener.invoke(returnProduct)
                } catch(e: Exception) {
                    listener.invoke(Product())
                }

            }
        }

        fun getCartProduct(seller: String, id: String, listener: (Product) -> Unit) {
            val db = getProductCollection()
            db.document(id).get().addOnSuccessListener {
                if (it.data != null) {
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
                } else {
                    removeProductFromCart(Product(id = id, seller = seller))
                    listener.invoke(Product())
                }
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
                db.set(mapOf(product.seller to mapOf(product.id to 1)), SetOptions.merge())
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

        fun immediateOrder(product: Product, listener: (Boolean) -> Unit) {
            val db = getOrdersCollection()
            val data: MutableMap<String, Any> = mutableMapOf()
            data["products"] = mapOf(product.id to mapOf("id" to product.id, "name" to product.name, "price" to product.price, "description" to product.description, "category" to product.category, "images" to product.images, "seller" to product.seller, "quantity" to 1))
            data["buyer"] = AuthenticationHelper.getCurrentUserUid()
            data["seller"] = product.seller
            data["status"] = "Pending"
            data["timestamp"] = getCurrTimestamp()
            db.add(data).addOnSuccessListener {
                addNotification(product.seller, Notification(title = "New Order", description = "You have a new order. Please check the manage product list", timestamp = getCurrTimestamp()))
                listener.invoke(true)
            }.addOnFailureListener {
                listener.invoke(false)
            }
        }

        fun completeOrder(seller: String, listener: (Boolean) -> Unit) {
            var itemCount: Int
            var count = 0
            var totalPrice = 0.0
            val data: MutableMap<String, Any> = mutableMapOf()
            val productsData: MutableMap<String, Any> = mutableMapOf()
            data["timestamp"] = getCurrTimestamp()
            data["seller"] = seller
            data["buyer"] = AuthenticationHelper.getCurrentUserUid()
            data["status"] = "Pending"
            getCurrCartDocument().get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    if (snapshot.get(seller) != null) {
                        val snapshotData = snapshot.get(seller) as Map<String, Int>
                        itemCount = snapshotData.size
                        for (product in snapshotData) {
                            getCartProduct(seller, product.key) { cartProduct ->
                                if (cartProduct.id != "") {
                                    totalPrice += cartProduct.price * product.value
                                    cartProduct.quantity = product.value
                                    productsData[product.key] = cartProduct
                                    count++
                                } else {
                                    itemCount--
                                }
                                if (itemCount == count) {
                                    data["products"] = productsData
                                    data["price"] = totalPrice
                                    getCurrCartDocument().update(seller, FieldValue.delete())
                                    getOrdersCollection().add(data).addOnSuccessListener {
                                        addNotification(seller, Notification(title = "New Order", description = "You have a new order. Please check the manage product list", timestamp = getCurrTimestamp()))
                                        listener.invoke(true)
                                    }.addOnFailureListener {
                                        listener.invoke(false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        fun getOrdersCollection(): CollectionReference {
            return getDatabase().collection("orders")
        }

        fun updateOrderStatus(buyerId: String, orderId: String, status: String) {
            getOrdersCollection().document(orderId).update("status", status)
            addNotification(buyerId, Notification(title = "Order is $status", description = "Your order status has been changed to $status. Please check your order list.", timestamp = getCurrTimestamp()))
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

        private fun addNotification(id: String, notification: Notification) {
            val db = getNotificationCollection().document(id)
            db.get().addOnSuccessListener {
                if (it.exists()) {
                    db.update("notifications", FieldValue.arrayUnion(notification))
                } else {
                    val data = hashMapOf(
                        "notifications" to listOf(hashMapOf(
                            "title" to notification.title,
                            "description" to notification.description,
                            "timestamp" to notification.timestamp
                        ))
                    )
                    db.set(data)
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

        fun updateChatStatus(receiver: String) {
            val db = getDatabase().collection("users").document(receiver)
            db.get().addOnSuccessListener {
                if (it.exists()) {
                    if (it.get("chat") != null) {
                        db.update("chat", FieldValue.delete())
                    } else {
                        db.update("chat", "ping")
                    }
                }
            }
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

        fun getCurrTimestamp(): String {
            return DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now())
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