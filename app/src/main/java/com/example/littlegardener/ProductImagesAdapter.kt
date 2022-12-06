package com.example.littlegardener

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductImagesAdapter(private val imagesList: List<Uri>): RecyclerView.Adapter<ProductImagesAdapter.ViewImage>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewImage {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_image, parent, false)
        return ViewImage(view)
    }

    override fun onBindViewHolder(holder: ViewImage, position: Int) {
        val image = imagesList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    class ViewImage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(image: Uri) {
            Glide.with(itemView.context)
                .load(image)
                .placeholder(R.drawable.logo)
                .into(itemView.findViewById(R.id.product_image))
        }
    }
}