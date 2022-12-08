package com.example.littlegardener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private val cartItems: List<Pair<String, HashMap<String, Int>>>): RecyclerView.Adapter<CartAdapter.ViewCart>() {

    private lateinit var cartItemAdapter: CartItemAdapter
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private var totalPrice = 0.0
    private var cartProductItems: MutableList<Pair<String, Int>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewCart {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return ViewCart(view)
    }

    override fun onBindViewHolder(holder: ViewCart, position: Int) {
        val cartItem = cartItems[position]
        totalPriceTextView = holder.itemView.findViewById(R.id.cart_total_price)
        cartRecyclerView = holder.itemView.findViewById(R.id.cart_item_recycler_view)
        cartRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        cartItemAdapter = CartItemAdapter(cartProductItems, cartItem.first)
        cartRecyclerView.adapter = cartItemAdapter
        initCartItemListener(position)
        holder.itemView.findViewById<Button>(R.id.checkout_button).setOnClickListener {
            println("seller: ${cartItem.first}, cartItem: ${cartItem.second}")
        }
        holder.bind(cartItem, totalPrice)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    private fun initCartItemListener(position: Int) {
        FirestoreHelper.initCart {
            val db = FirestoreHelper.getCurrCartDocument()
            db.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    totalPrice = 0.0
                    cartProductItems.clear()
                    val item = ArrayList(snapshot.data!!.values) as ArrayList<HashMap<String, Int>>
                    for (info in item[position]) {
                        cartProductItems.add(Pair(info.key, info.value))
                        FirestoreHelper.getProductPrice(info.key) {
                            totalPrice += it * info.value
                            val total = "Total Price: RM %.2f".format(totalPrice)
                            totalPriceTextView.text = total
                        }
                    }
                    cartItemAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    class ViewCart(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(cartItem: Pair<String, HashMap<String, Int>>, totalPrice: Double) {
            FirestoreHelper.getAccountName(cartItem.first) {
                itemView.findViewById<TextView>(R.id.cart_seller_name).text = it
            }
        }
    }
}