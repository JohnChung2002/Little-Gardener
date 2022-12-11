package com.example.littlegardener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartItemAdapter(private val cartItems: List<Pair<String, Int>>, private val id: String, private val type: String): RecyclerView.Adapter<CartItemAdapter.ViewCartItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewCartItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_product_item, parent, false)
        return ViewCartItem(view)
    }

    override fun onBindViewHolder(holder: ViewCartItem, position: Int) {
        val cartItem = cartItems[position]
        if (type == "cart") {
            holder.itemView.setOnLongClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Remove item from cart?")
                    .setMessage("Are you sure you want to remove this item from your cart?")
                    .setPositiveButton("Yes") { _, _ ->
                        FirestoreHelper.removeProductFromCart(Product(id = cartItem.first, seller = id))
                    }
                    .setNegativeButton("No") { _, _ -> }
                    .show()
                true
            }
        }
        holder.bind(cartItem, id, type)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    class ViewCartItem(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(cartItem: Pair<String, Int>, id: String, type: String) {
            if (type == "cart") {
                FirestoreHelper.getCartProduct(id, cartItem.first) { product ->
                    if (product.id != "") {
                        itemView.findViewById<TextView>(R.id.product_name).text = product.name
                        val quantity = "Qty: ${cartItem.second}"
                        itemView.findViewById<TextView>(R.id.product_quantity).text = quantity
                        Glide.with(itemView.context).load(product.images[0]).into(itemView.findViewById(R.id.product_image))
                    }
                }
            } else {
                FirestoreHelper.getOrderProduct(id, cartItem.first) { product ->
                    if (product.id != "") {
                        itemView.findViewById<TextView>(R.id.product_name).text = product.name
                        val quantity = "Qty: ${cartItem.second}"
                        itemView.findViewById<TextView>(R.id.product_quantity).text = quantity
                        Glide.with(itemView.context).load(product.images[0]).into(itemView.findViewById(R.id.product_image))
                    }
                }
            }
        }
    }
}