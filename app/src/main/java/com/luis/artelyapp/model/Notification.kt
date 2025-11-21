package com.luis.artelyapp.model

data class Notification(
    val id_Notification: Int,
    val id_Customer: Int,
    val MessageText: String,
    val Location: String
)
