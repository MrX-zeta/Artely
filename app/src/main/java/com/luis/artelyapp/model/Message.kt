package com.luis.artelyapp.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
    var id_Message: String = "",
    var id_Chat: String = "",
    var id_Artist: String = "",
    var id_Customer: String = "",
    var senderId: String = "",
    var content: String = "",
    var isRead: Boolean = false
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", "", "", "", false)
}
