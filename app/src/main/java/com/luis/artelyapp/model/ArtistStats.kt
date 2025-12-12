package com.luis.artelyapp.model

/**
 * Estadísticas de un artista
 * Nota: Todos los parámetros tienen valores por defecto para permitir
 * la deserialización de Firebase (requiere constructor sin argumentos)
 */
data class ArtistStats(
    val totalArtworks: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val likes: Int = 0
)

