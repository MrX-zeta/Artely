package com.luis.artelyapp.model

data class Artwork(
    val id_ArtWork: Int,
    val id_Artist: Int,
    val Title: String,
    val Description: String,
    val Price: Double,
    val Technique: String,
    val Status: String,
    val ImageUrl: String
)