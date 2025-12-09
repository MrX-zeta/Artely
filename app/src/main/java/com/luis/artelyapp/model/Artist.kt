package com.luis.artelyapp.model

import com.google.firebase.database.PropertyName

/**
 * Modelo de Artista
 * Hereda de User y agrega campos específicos de artista
 *
 * Nota: Constructor sin argumentos requerido por Firebase para deserialización
 * Los @PropertyName mapean los nombres de Firebase (minúsculas) a Kotlin (PascalCase)
 */
data class Artist(
    // Propiedades heredadas de User
    @get:PropertyName("id_User")
    @set:PropertyName("id_User")
    override var id_User: String = "",

    @get:PropertyName("userName")
    @set:PropertyName("userName")
    override var UserName: String = "",

    @get:PropertyName("email")
    @set:PropertyName("email")
    override var Email: String = "",

    @get:PropertyName("role")
    @set:PropertyName("role")
    override var Role: String = "Artist",

    // Propiedades específicas de Artista
    @get:PropertyName("bio")
    @set:PropertyName("bio")
    var Bio: String = "",

    @get:PropertyName("location")
    @set:PropertyName("location")
    var Location: String = "",

    // Foto de perfil
    @get:PropertyName("profileImageUrl")
    @set:PropertyName("profileImageUrl")
    var profileImageUrl: String = "",

    // Token de notificación FCM
    @get:PropertyName("fcmToken")
    @set:PropertyName("fcmToken")
    var fcmToken: String = ""
) : User(id_User, UserName, Email, Role) {

    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", "Artist", "", "", "", "")
}
