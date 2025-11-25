package com.luis.artelyapp.model

data class Message(
    val id_Message: Int,
    val id_Chat: Int,
    val id_Artist: Int,
    val id_Customer: Int,
    val content: String
)