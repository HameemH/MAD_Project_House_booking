package com.example.mad_project_house_booking
import androidx.annotation.DrawableRes

data class Room(
    val name: String,
    val price: String,
    val isAvailable: Boolean,
    @DrawableRes val imageResId: Int
)