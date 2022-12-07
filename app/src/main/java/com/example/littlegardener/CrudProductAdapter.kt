package com.example.littlegardener

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CrudProductAdapter(private val type: String, private val productList: List<Product>): RecyclerView.Adapter<CrudProductAdapter.ViewProduct>() {
    interface OnProductClickListener {
        fun onProductEditClick(product: Product)
        fun onProductShowClick(product: Product)
    }

    private lateinit var viewContext: Context
    private lateinit var viewActivity: Activity
    private lateinit var callback: OnProductClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewProduct {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        viewContext = parent.context
        viewActivity = viewContext as Activity
        callback = viewActivity as OnProductClickListener
        return ViewProduct(view)
    }

    override fun onBindViewHolder(holder: ViewProduct, position: Int) {
        val productItem = productList[position]
        if (type == "edit") {
            holder.itemView.setOnClickListener {
                callback.onProductEditClick(productItem)
            }
        } else if (type == "delete") {
            holder.itemView.setOnClickListener {
                confirmDelete(productItem)
            }
        } else {
            holder.itemView.setOnClickListener {
                callback.onProductShowClick(productItem)
            }
        }
        holder.bind(productItem)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    private fun confirmDelete(product: Product) {
        AlertDialog.Builder(viewContext)
            .setTitle("Delete product")
            .setMessage("Are you sure you want to delete ${product.name}?")
            .setPositiveButton("Yes") { _, _ ->
                FirestoreHelper.deleteProduct(product.id, product.category)
            }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }

    class ViewProduct(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(product: Product) {
            itemView.findViewById<TextView>(R.id.product_name).text = product.name
            val price = "RM %,.2f".format(product.price)
            itemView.findViewById<TextView>(R.id.product_price).text = price
            itemView.findViewById<TextView>(R.id.product_category).text = product.category
            Glide.with(itemView.context)
                .load(product.images[0])
                .placeholder(R.drawable.logo)
                .into(itemView.findViewById(R.id.product_image))
        }
    }
}