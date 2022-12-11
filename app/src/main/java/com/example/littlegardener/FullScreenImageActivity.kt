package com.example.littlegardener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var photoView: PhotoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
        photoView = findViewById(R.id.photo_view)
        intent.getStringExtra("image")?.let {
            Glide.with(this).load(it).into(photoView)
        }
    }
}