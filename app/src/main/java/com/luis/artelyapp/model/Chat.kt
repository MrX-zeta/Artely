package com.luis.artelyapp.model

data class Chat(
    val id_Chat: String = "",
    val id_Artist: String = "",
    val id_Customer: String = ""
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "")
}
