package com.example.littlegardener

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeCategoryAdapter(private val categoryList: List<String>): RecyclerView.Adapter<HomeCategoryAdapter.ViewCategory>() {
    interface CategoryListener {
        fun onCategoryClicked(category: String)
    }

    private lateinit var callback: CategoryListener
    private lateinit var viewContext: Context
    private lateinit var viewActivity: Activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewCategory {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        viewContext = parent.context
        viewActivity = viewContext as Activity
        callback = viewActivity as CategoryListener
        return ViewCategory(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewCategory, position: Int) {
        val category = categoryList[position]
        holder.itemView.setOnClickListener {
            callback.onCategoryClicked(category)
        }
        holder.bind(category)
    }

    class ViewCategory(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(category: String) {
            itemView.findViewById<TextView>(R.id.category).text = category
        }
    }
}