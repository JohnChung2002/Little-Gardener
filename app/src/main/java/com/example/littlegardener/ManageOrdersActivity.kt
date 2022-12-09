package com.example.littlegardener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration

class ManageOrdersActivity : AppCompatActivity() {
    private lateinit var type: String
    private lateinit var snapshotListener: ListenerRegistration
    private lateinit var orderAdapter: OrderAdapter
    private var orderList: MutableList<Pair<String, Pair<String, HashMap<String, Int>>>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_orders)
        type = intent.getStringExtra("type")!!
        initUI()
        when (type) {
            "self_manage_orders" -> {
                loadManageSelfOrders()
            }
            "all_manage_orders" -> {
                loadManageAllOrders()
            }
            else -> {
                loadUserOrders()
            }
        }
    }

    private fun initUI() {
        val cartRecyclerView = findViewById<RecyclerView>(R.id.orders_recycler_view)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(orderList)
        cartRecyclerView.adapter = orderAdapter
    }

    private fun loadUserOrders() {
        val db = FirestoreHelper.getOrdersCollection().whereEqualTo("buyer", AuthenticationHelper.getCurrentUserUid())
        snapshotListener = db.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("error ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                orderList.clear()
                for (document in snapshot.documents) {
                    println("id: ${document.id}")
                    println("data: ${document.data}")
                    println("product data: ${document.data?.get("products")!!}")
                    var seller = ""
                    val items: HashMap<String, Int> = hashMapOf()
                    for (product in document.data!!["products"] as HashMap<String, Any>) {
                        val prod = product.value as HashMap<String, Any>
                        items[prod["id"] as String] = (prod["quantity"] as Long).toInt()
                        seller = prod["seller"] as String
                    }
                    orderList.add(Pair(document.id, Pair(seller, items)))
                }
                println("Completed: $orderList")
                orderAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun loadManageSelfOrders() {
        val db = FirestoreHelper.getOrdersCollection().whereEqualTo("seller", AuthenticationHelper.getCurrentUserUid())
        snapshotListener = db.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {

            }
        }
    }

    private fun loadManageAllOrders() {
        snapshotListener = FirestoreHelper.getOrdersCollection().addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {

            }
        }
    }
}