package com.example.littlegardener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val orderItems: List<Pair<String, Pair<Pair<String, String>, HashMap<String, Int>>>>, private val viewType: String): RecyclerView.Adapter<OrderAdapter.ViewOrder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewOrder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return ViewOrder(view)
    }

    override fun onBindViewHolder(holder: ViewOrder, position: Int) {
        val orderItem = orderItems[position]
        if (viewType == "view_orders") {
            holder.itemView.setOnLongClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Cancel order?")
                    .setMessage("Are you sure you want to cancel this order?")
                    .setPositiveButton("Yes") { _, _ ->
                        FirestoreHelper.updateOrderStatus(
                            orderItem.second.first.first,
                            orderItem.first,
                            "Cancelled"
                        )
                    }
                    .setNegativeButton("No") { _, _ -> }
                    .show()
                true
            }
        }
        holder.bind(orderItem, viewType)
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }

    class ViewOrder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private lateinit var cartItemAdapter: CartItemAdapter
        private lateinit var cartRecyclerView: RecyclerView
        private var cartProductItems: MutableList<Pair<String, Int>> = mutableListOf()
        private var checkOutItems: HashMap<Product, Int> = hashMapOf()
        private lateinit var checkOutButton: Button
        private lateinit var orderType: String
        private lateinit var orderId: String
        private lateinit var buyerId: String
        private lateinit var orderViewType: String

        fun bind(orderItem:  Pair<String, Pair<Pair<String, String>, HashMap<String, Int>>>, viewType: String) {
            orderViewType = viewType
            if (viewType != "view_orders") {
                FirestoreHelper.getAccountInfo(orderItem.second.first.first) { name, _ ->
                    itemView.findViewById<TextView>(R.id.cart_seller_name).text = name
                }
            } else {
                FirestoreHelper.getAccountInfo(orderItem.second.first.second) { name, _ ->
                    itemView.findViewById<TextView>(R.id.cart_seller_name).text = name
                }
            }
            orderId = orderItem.first
            buyerId = orderItem.second.first.first
            var totalPrice = 0.0
            cartRecyclerView = itemView.findViewById(R.id.cart_item_recycler_view)
            cartRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            cartItemAdapter = CartItemAdapter(cartProductItems, orderId, "order")
            cartRecyclerView.adapter = cartItemAdapter
            initOrderItemListener(orderId)
            for (key in orderItem.second.second.keys) {
                FirestoreHelper.getOrderProduct(orderId, key) { product ->
                    if (product.id != "") {
                        totalPrice += (product.price * orderItem.second.second[key]!!)
                        val total = "Total Price: RM %.2f".format(totalPrice)
                        itemView.findViewById<TextView>(R.id.cart_total_price).text = total
                        checkOutItems[product] = orderItem.second.second[key]!!
                    }
                }
            }
            checkOutButton = itemView.findViewById(R.id.checkout_button)
        }

        private fun prepareOrder() {
            checkOutButton.visibility = View.VISIBLE
            checkOutButton.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Checkout?")
                    .setMessage("Are you sure you want to prepare?")
                    .setPositiveButton("Yes") { _, _ ->
                        FirestoreHelper.updateOrderStatus(buyerId, orderId, "Preparing")
                    }
                    .setNegativeButton("No") { _, _ -> }
                    .show()
            }
        }

        private fun readyOrder() {
            checkOutButton.visibility = View.VISIBLE
            checkOutButton.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Checkout?")
                    .setMessage("Are you sure you want to ready?")
                    .setPositiveButton("Yes") { _, _ ->
                        FirestoreHelper.updateOrderStatus(buyerId, orderId, "Ready For Pickup")
                    }
                    .setNegativeButton("No") { _, _ -> }
                    .show()
            }
        }

        private fun completeOrder() {
            checkOutButton.visibility = View.VISIBLE
            checkOutButton.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Checkout?")
                    .setMessage("Are you sure you want to complete?")
                    .setPositiveButton("Yes") { _, _ ->
                        FirestoreHelper.updateOrderStatus(buyerId, orderId, "Completed")
                    }
                    .setNegativeButton("No") { _, _ -> }
                    .show()
            }
        }

        private fun updateCheckoutButton() {
            if (orderViewType != "view_orders") {
                val filters = itemView.context.resources.getStringArray(R.array.order_filter)
                checkOutButton.text = filters[(filters.indexOf(orderType) + 1) % filters.size]
                when (orderType) {
                    "Pending" -> { prepareOrder() }
                    "Preparing" -> { readyOrder() }
                    "Ready For Pickup" -> { completeOrder() }
                    else -> { checkOutButton.visibility = View.GONE }
                }
            } else {
                checkOutButton.visibility = View.GONE
            }
        }

        private fun initOrderItemListener(order: String) {
            val db = FirestoreHelper.getOrdersCollection().document(order)
            db.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    cartProductItems.clear()
                    val orderItems = snapshot.data!! as HashMap<String, Any>
                    orderType = orderItems["status"] as String
                    updateCheckoutButton()
                    for (product in orderItems["products"] as HashMap<String, Any>) {
                        var itemId = ""
                        var quantity: Long = 0
                        for (info in product.value as HashMap<String, Any>) {
                            if (info.key == "id") {
                                itemId = info.value as String
                            } else if (info.key == "quantity") {
                                quantity = info.value as Long
                            }
                        }
                        cartProductItems.add(Pair(itemId, quantity.toInt()))
                    }
                    cartItemAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}