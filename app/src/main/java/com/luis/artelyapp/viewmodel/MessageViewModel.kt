package com.luis.artelyapp.ui.view.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.luis.artelyapp.model.Message

class MessageViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _currentMessage = MutableStateFlow("")
    val currentMessage: StateFlow<String> = _currentMessage

    private val currentUserId = 1

    init {
        loadSampleMessages()
    }

    private fun loadSampleMessages() {
        _messages.value = listOf(
            Message(id_Chat = 1, id_Artist = 2, id_Customer = 1, content = "Hola, estoy interesado en tu pintura"),
            Message(id_Chat = 1, id_Artist = 1, id_Customer = 1, content = "Hola, buenas tardes"),
            Message(id_Chat = 1, id_Artist = 2, id_Customer = 1, content = "Está en $1500"),
            Message(id_Chat = 1, id_Artist = 1, id_Customer = 1, content = "Ah perfecto, muchas gracias")
        )
    }

    fun updateCurrentMessage(message: String) {
        _currentMessage.value = message
    }

    fun sendMessage() {
        if (_currentMessage.value.isNotBlank()) {
            val newMessage = Message(
                id_Chat = 1,
                id_Artist = currentUserId,
                id_Customer = currentUserId,
                content = _currentMessage.value
            )
            _messages.value = _messages.value + newMessage
            _currentMessage.value = ""
        }
    }

    fun isMessageFromCurrentUser(message: Message): Boolean {
        return message.id_Artist == currentUserId
    }
}