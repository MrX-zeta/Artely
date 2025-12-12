package com.luis.artelyapp.model

data class Notification(
    val id_Notification: String = "",
    val id_Customer: String = "",
    val MessageText: String = "",
    val Location: String = ""
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", "")
}
