package com.example.moodbites

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodItem(
    val name: String,
    val price: Double,
    var quantity: Int = 1 // Default quantity is 1
) : Parcelable
