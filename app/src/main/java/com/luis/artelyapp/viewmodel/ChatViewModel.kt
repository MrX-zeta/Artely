package com.luis.artelyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.luis.artelyapp.model.Chat
import com.luis.artelyapp.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel : ViewModel() {
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        // Datos de ejemplo - Reemplazar con datos reales de tu backend/base de datos
        _chats.value = listOf(
            Chat(
                id_User = 101,
                userName = "Pablo Picasso",
                id_Chat = 1,
                messages = listOf(
                    Message(1, 101, 1, 1,"Hola, me interesa tu obra"),
                    Message(1, 1, 101, 1,"Gracias por tu interés!")
                )
            ),
            Chat(
                id_User = 102,
                userName = "Frida Kahlo",
                id_Chat = 2,
                messages = listOf(
                    Message(2, 102, 1, 1,"¿Cuánto cuesta esta pieza?"),
                    Message(2, 1, 102, 1,"Te envío los detalles")
                )
            ),
            Chat(
                id_User = 103,
                userName = "Leonardo da Vinci",
                id_Chat = 3,
                messages = listOf(
                    Message(3, 103, 1, 1,"Excelente trabajo")
                )
            ),
            Chat(
                id_User = 104,
                userName = "Vincent van Gogh",
                id_Chat = 4,
                messages = listOf(
                    Message(4, 104, 1, 1,"Me encanta tu estilo"),
                    Message(4, 1, 104, 1,"¡Muchas gracias!")
                )
            )
        )
    }

    fun getChatById(chatId: Int): Chat? {
        return _chats.value.find { it.id_Chat == chatId }
    }
}