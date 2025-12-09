package com.luis.artelyapp.model

data class Message(
    val id_Message: String = "",
    val id_Chat: String = "",
    val id_Artist: String = "",
    val id_Customer: String = "",
    val senderId: String = "",
    val content: String = "",
    val isRead: Boolean = false
) {
    constructor() : this("", "", "", "", "", "", false)
}
