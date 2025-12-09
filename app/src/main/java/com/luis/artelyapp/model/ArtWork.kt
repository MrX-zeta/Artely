package com.luis.artelyapp.model

import com.google.firebase.database.PropertyName

data class Artwork(
    @get:PropertyName("id_ArtWork")
    @set:PropertyName("id_ArtWork")
    var id_ArtWork: String = "",

    @get:PropertyName("id_Artist")
    @set:PropertyName("id_Artist")
    var id_Artist: String = "",

    @get:PropertyName("title")
    @set:PropertyName("title")
    var Title: String = "",

    @get:PropertyName("description")
    @set:PropertyName("description")
    var Description: String = "",

    @get:PropertyName("price")
    @set:PropertyName("price")
    var Price: Double = 0.0,

    @get:PropertyName("technique")
    @set:PropertyName("technique")
    var Technique: String = "",

    @get:PropertyName("status")
    @set:PropertyName("status")
    var Status: String = "",

    @get:PropertyName("imageUrl")
    @set:PropertyName("imageUrl")
    var ImageUrl: String = ""
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", "", 0.0, "", "", "")
}
