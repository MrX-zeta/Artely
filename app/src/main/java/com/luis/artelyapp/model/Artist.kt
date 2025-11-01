package com.luis.artelyapp.model

data class Artist(
    // Propiedades heredadas de User
    override val id_User: Int,
    override val Email: String,
    override val Psswd: String,
    override val Role: String,

    // Propiedades propias de Artist
    val Bio: String,
    val Location: String
) : User(id_User, Email, Psswd, Role)
