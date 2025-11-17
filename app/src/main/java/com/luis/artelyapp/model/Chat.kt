package com.luis.artelyapp.model
data class Chat(
    val id_User:Int,
    val userName: String,
    val id_Chat: Int,
    val messages: List<Message> = emptyList()
)