package com.luis.artelyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.luis.artelyapp.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MessageViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _currentMessage = MutableStateFlow("")
    val currentMessage: StateFlow<String> = _currentMessage.asStateFlow()

    private var currentChatId: Int = 0

    fun loadMessagesForChat(chatId: Int) {
        currentChatId = chatId
        _messages.value = when (chatId) {
            1 -> listOf(
                Message(1, 1, 101, 1, "Hola, me interesa tu obra"),
                Message(2, 1, 1, 101, "Gracias por tu interés!"),
                Message(3, 1, 101, 1, "¿Cuál es el precio?"),
                Message(4, 1, 1, 101, "Te puedo ofrecer un precio especial")
            )
            2 -> listOf(
                Message(5, 2, 102, 1, "¿Cuánto cuesta esta pieza?"),
                Message(6, 2, 1, 102, "Te envío los detalles"),
                Message(7, 2, 102, 1, "Perfecto, espero tu respuesta")
            )
            3 -> listOf(
                Message(8, 3, 103, 1, "Excelente trabajo"),
                Message(9, 3, 1, 103, "¡Muchas gracias!")
            )
            4 -> listOf(
                Message(10, 4, 104, 1, "Me encanta tu estilo"),
                Message(11, 4, 1, 104, "¡Muchas gracias!"),
                Message(12, 4, 104, 1, "¿Haces comisiones?"),
                Message(13, 4, 1, 104, "Sí, claro. Cuéntame qué tienes en mente")
            )
            else -> emptyList()
        }
    }

    fun updateCurrentMessage(message: String) {
        _currentMessage.value = message
    }

    fun sendMessage() {
        if (_currentMessage.value.isNotBlank()) {
            val newMessage = Message(
                id_Message = _messages.value.size + 1,
                id_Chat = currentChatId,
                id_Artist = 1,
                id_Customer = 101,
                content = _currentMessage.value
            )
            _messages.value = _messages.value + newMessage
            _currentMessage.value = ""
        }
    }

    fun isMessageFromCurrentUser(message: Message): Boolean {
        // Devuelve true si el id_Customer es 1 (usuario actual)
        return message.id_Customer == 1
    }
}