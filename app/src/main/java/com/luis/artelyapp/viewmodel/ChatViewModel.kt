package com.luis.artelyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.luis.artelyapp.model.Chat
import com.luis.artelyapp.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Modelo auxiliar para la UI que combina Chat con información adicional
data class ChatDisplay(
    val chat: Chat,
    val userName: String,
    val messages: List<Message>
)

class ChatViewModel : ViewModel() {
    private val _chatsDisplay = MutableStateFlow<List<ChatDisplay>>(emptyList())
    val chats: StateFlow<List<ChatDisplay>> = _chatsDisplay.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        // Datos de ejemplo - Reemplazar con datos reales de tu backend/base de datos
        // En la implementación real, cargarías los mensajes desde MessageRepository
        _chatsDisplay.value = listOf(
            ChatDisplay(
                chat = Chat(
                    id_Chat = 1,
                    id_Artist = 101,
                    id_Customer = 1
                ),
                userName = "Pablo Picasso",
                messages = listOf(
                    Message(1, 1, 101, 1, "Hola, me interesa tu obra"),
                    Message(2, 1, 1, 101, "Gracias por tu interés!")
                )
            ),
            ChatDisplay(
                chat = Chat(
                    id_Chat = 2,
                    id_Artist = 102,
                    id_Customer = 1
                ),
                userName = "Frida Kahlo",
                messages = listOf(
                    Message(3, 2, 102, 1, "¿Cuánto cuesta esta pieza?"),
                    Message(4, 2, 1, 102, "Te envío los detalles")
                )
            ),
            ChatDisplay(
                chat = Chat(
                    id_Chat = 3,
                    id_Artist = 103,
                    id_Customer = 1
                ),
                userName = "Leonardo da Vinci",
                messages = listOf(
                    Message(5, 3, 103, 1, "Excelente trabajo")
                )
            ),
            ChatDisplay(
                chat = Chat(
                    id_Chat = 4,
                    id_Artist = 104,
                    id_Customer = 1
                ),
                userName = "Vincent van Gogh",
                messages = listOf(
                    Message(6, 4, 104, 1, "Me encanta tu estilo"),
                    Message(7, 4, 1, 104, "¡Muchas gracias!")
                )
            )
        )
    }

    fun getChatById(chatId: Int): ChatDisplay? {
        return _chatsDisplay.value.find { it.chat.id_Chat == chatId }
    }
}