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

class OrderAdapter(private val orderItems: List<Pair<String, Pair<String, HashMap<String, Int>>>>): RecyclerView.Adapter<OrderAdapter.ViewOrder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewOrder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return ViewOrder(view)
    }

    override fun onBindViewHolder(holder: ViewOrder, position: Int) {
        val orderItem = orderItems[position]
        holder.bind(orderItem)
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }

    class ViewOrder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private lateinit var cartItemAdapter: CartItemAdapter
        private lateinit var cartRecyclerView: RecyclerView
        private var cartProductItems: MutableList<Pair<String, Int>> = mutableListOf()
        private var checkOutItems: HashMap<Product, Int> = hashMapOf()

        fun bind(orderItem: Pair<String, Pair<String, HashMap<String, Int>>>) {
            FirestoreHelper.getAccountInfo(orderItem.second.first) { name, _ ->
                itemView.findViewById<TextView>(R.id.cart_seller_name).text = name
            }
            var totalPrice = 0.0
            cartRecyclerView = itemView.findViewById(R.id.cart_item_recycler_view)
            cartRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            cartItemAdapter = CartItemAdapter(cartProductItems, orderItem.second.first)
            cartRecyclerView.adapter = cartItemAdapter
            initOrderItemListener(orderItem.first)
            for (key in orderItem.second.second.keys) {
                FirestoreHelper.getProduct(key) { product ->
                    totalPrice += (product.price * orderItem.second.second[key]!!)
                    val total = "Total Price: RM %.2f".format(totalPrice)
                    itemView.findViewById<TextView>(R.id.cart_total_price).text = total
                    checkOutItems[product] = orderItem.second.second[key]!!
                }
            }
            itemView.findViewById<Button>(R.id.checkout_button).setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Checkout?")
                    .setMessage("Are you sure you want to checkout?")
                    .setPositiveButton("Yes") { _, _ ->
                        println("Test")
                    }
                    .setNegativeButton("No") { _, _ -> }
                    .show()
            }
        }

        private fun initOrderItemListener(order: String) {
            val db = FirestoreHelper.getOrdersCollection().document("order")
            db.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(itemView.context, "Failed to load order", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val orderItems = snapshot.data!![order] as HashMap<String, HashMap<String, Int>>
                    cartProductItems.clear()
                    for (key in orderItems.keys) {
                        cartProductItems.add(Pair(key, orderItems[key]!!["quantity"]!!))
                    }
                    cartItemAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}