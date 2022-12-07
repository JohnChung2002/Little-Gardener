package com.example.littlegardener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageSliderAdapter(private val imagesList: MutableList<String>): RecyclerView.Adapter<ImageSliderAdapter.ViewImages>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewImages {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_image, parent, false)
        return ViewImages(view)
    }

    override fun onBindViewHolder(holder: ViewImages, position: Int) {
        val image = imagesList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    class ViewImages(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(image: String) {
            Glide
                .with(itemView)
                .load(image)
                .into(itemView.findViewById(R.id.product_image))
        }
    }
}