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

class CartAdapter(private val cartItems: List<Pair<String, HashMap<String, Int>>>): RecyclerView.Adapter<CartAdapter.ViewCart>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewCart {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return ViewCart(view)
    }

    override fun onBindViewHolder(holder: ViewCart, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    class ViewCart(itemView: View): RecyclerView.ViewHolder(itemView) {
        private lateinit var cartItemAdapter: CartItemAdapter
        private lateinit var cartRecyclerView: RecyclerView
        private var cartProductItems: MutableList<Pair<String, Int>> = mutableListOf()
        private var checkOutItems: HashMap<Product, Int> = hashMapOf()
        private lateinit var checkOutButton: Button

        fun bind(cartItem: Pair<String, HashMap<String, Int>>) {
            FirestoreHelper.getAccountInfo(cartItem.first) { name, _ ->
                itemView.findViewById<TextView>(R.id.cart_seller_name).text = name
            }
            var totalPrice = 0.0
            cartRecyclerView = itemView.findViewById(R.id.cart_item_recycler_view)
            cartRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            cartItemAdapter = CartItemAdapter(cartProductItems, cartItem.first)
            cartRecyclerView.adapter = cartItemAdapter
            initCartItemListener(cartItem.first)
            for (key in cartItem.second.keys) {
                FirestoreHelper.getProduct(key) { product ->
                    totalPrice += (product.price * cartItem.second[key]!!)
                    val total = "Total Price: RM %.2f".format(totalPrice)
                    itemView.findViewById<TextView>(R.id.cart_total_price).text = total
                    checkOutItems[product] = cartItem.second[key]!!
                }
            }
            itemView.findViewById<Button>(R.id.checkout_button).setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Checkout?")
                    .setMessage("Are you sure you want to checkout?")
                    .setPositiveButton("Yes") { _, _ ->
                        FirestoreHelper.completeOrder(cartItem.first, checkOutItems, totalPrice) {
                            if (it) {
                                Toast.makeText(
                                    itemView.context,
                                    "Order completed!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .setNegativeButton("No") { _, _ -> }
                    .show()
            }
        }

        private fun initCartItemListener(seller: String) {
            FirestoreHelper.initCart {
                val db = FirestoreHelper.getCurrCartDocument()
                db.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        if (snapshot.data!![seller] != null) {
                            cartProductItems.clear()
                            val item = snapshot.data!![seller] as HashMap<String, Int>
                            for (key in item.keys.toList()) {
                                cartProductItems.add(Pair(key, item[key]!!))
                            }
                        }
                    }
                    cartItemAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}