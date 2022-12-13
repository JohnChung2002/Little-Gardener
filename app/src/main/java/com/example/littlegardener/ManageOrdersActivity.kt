package com.example.littlegardener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration

class ManageOrdersActivity : AppCompatActivity() {
    private lateinit var type: String
    private lateinit var filter: String
    private lateinit var snapshotListener: ListenerRegistration
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var filterSpinner: Spinner
    private var orderList: MutableList<Pair<String, Pair<Pair<String, String>, HashMap<String, Int>>>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_orders)
        type = intent.getStringExtra("type")!!
        initUI()
    }

    private fun initUI() {
        filterSpinner = findViewById(R.id.order_filter_spinner)
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadListeners()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        val cartRecyclerView = findViewById<RecyclerView>(R.id.orders_recycler_view)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        filter = filterSpinner.selectedItem.toString()
        orderAdapter = OrderAdapter(orderList, type)
        cartRecyclerView.adapter = orderAdapter
    }

    private fun loadListeners() {
        filter = filterSpinner.selectedItem.toString()
        when (type) {
            "manage_orders" -> {
                loadManageAllOrders()
            }
            else -> {
                loadUserOrders()
            }
        }
    }

    private fun loadUserOrders() {
        findViewById<Toolbar>(R.id.toolbar).title = getString(R.string.my_orders)
        val db = FirestoreHelper.getOrdersCollection().whereEqualTo("buyer", AuthenticationHelper.getCurrentUserUid()).whereEqualTo("status", filter)
        if (this::snapshotListener.isInitialized) {
            snapshotListener.remove()
        }
        snapshotListener = db.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                orderList.clear()
                for (document in snapshot.documents) {
                    val buyer = document.data!!["buyer"] as String
                    val seller = document.data!!["seller"] as String
                    val items: HashMap<String, Int> = hashMapOf()
                    for (product in document.data!!["products"] as HashMap<String, Any>) {
                        val prod = product.value as HashMap<String, Any>
                        items[prod["id"] as String] = (prod["quantity"] as Long).toInt()
                    }
                    orderList.add(Pair(document.id, Pair(Pair(buyer, seller), items)))
                }
            }
            orderAdapter.notifyDataSetChanged()
        }
    }

    private fun loadManageAllOrders() {
        findViewById<Toolbar>(R.id.toolbar).title = getString(R.string.manage_all_orders)
        if (this::snapshotListener.isInitialized) {
            snapshotListener.remove()
        }
        snapshotListener = FirestoreHelper.getOrdersCollection().whereEqualTo("status", filter).addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                orderList.clear()
                for (document in snapshot.documents) {
                    val buyer = document.data!!["buyer"] as String
                    val seller = document.data!!["seller"] as String
                    val items: HashMap<String, Int> = hashMapOf()
                    for (product in document.data!!["products"] as HashMap<String, Any>) {
                        val prod = product.value as HashMap<String, Any>
                        items[prod["id"] as String] = (prod["quantity"] as Long).toInt()
                    }
                    orderList.add(Pair(document.id, Pair(Pair(buyer, seller), items)))
                }
            }
            orderAdapter.notifyDataSetChanged()
        }
    }
}