package com.luis.artelyapp.model

data class ArtistStats(
    val totalArtworks: Int,
    val followers: Int,
    val following: Int = 0,
    val likes: Int = 0
)

