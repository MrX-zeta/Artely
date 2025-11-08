package com.luis.artelyapp.ui.main

/**
 * Modelos ligeros para la UI de la galería.
 */
data class Artwork(
    val id: Long,
    val title: String,
    val artist: String,
    val description: String? = null
)

