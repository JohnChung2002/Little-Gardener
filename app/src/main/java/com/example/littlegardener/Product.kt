package com.example.littlegardener

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var id: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var description: String = "",
    var category: String = "",
    var images: List<String> = listOf(),
    var seller: String = "",
): Parcelable
