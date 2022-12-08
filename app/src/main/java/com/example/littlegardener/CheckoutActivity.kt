package com.example.littlegardener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CheckoutActivity : AppCompatActivity() {
    private lateinit var type: String
    private lateinit var productList: MutableList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
    }
}