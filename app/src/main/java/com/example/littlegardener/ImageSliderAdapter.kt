package com.example.littlegardener

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageSliderAdapter(private val imagesList: MutableList<String>): SliderViewAdapter<ImageSliderAdapter.ViewImages>() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewImages {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.product_image, parent, false)
        return ViewImages(view)
    }

    override fun onBindViewHolder(holder: ViewImages, position: Int) {
        val image = imagesList[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, FullScreenImageActivity::class.java)
            intent.putExtra("image", image)
            holder.itemView.context.startActivity(intent)
        }
        holder.bind(image)
    }

    override fun getCount(): Int {
        return imagesList.size
    }

    class ViewImages(itemView: View): ViewHolder(itemView) {
        fun bind(image: String) {
            Glide
                .with(itemView)
                .load(image)
                .into(itemView.findViewById(R.id.product_image))
        }
    }
}