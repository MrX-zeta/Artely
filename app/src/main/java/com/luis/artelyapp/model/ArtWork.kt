package com.luis.artelyapp.model

data class Artwork(
    val id: Int,
    val title: String,
    val artist: String,
    val price: String? = null,
    val imageUrl: String = "",
    val isForSale: Boolean = false
)