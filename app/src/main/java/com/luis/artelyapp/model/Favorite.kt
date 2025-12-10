package com.luis.artelyapp.model

import com.google.firebase.database.PropertyName

/**
 * Modelo de Favoritos
 * Representa una obra de arte marcada como favorita por un customer
 */
data class Favorite(
    @get:PropertyName("id_Favorite")
    @set:PropertyName("id_Favorite")
    var id_Favorite: String = "",

    @get:PropertyName("id_Customer")
    @set:PropertyName("id_Customer")
    var id_Customer: String = "",

    @get:PropertyName("id_ArtWork")
    @set:PropertyName("id_ArtWork")
    var id_ArtWork: String = "",

    @get:PropertyName("id_Artist")
    @set:PropertyName("id_Artist")
    var id_Artist: String = "",

    @get:PropertyName("timestamp")
    @set:PropertyName("timestamp")
    var timestamp: Long = 0L
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", "", 0L)
}


