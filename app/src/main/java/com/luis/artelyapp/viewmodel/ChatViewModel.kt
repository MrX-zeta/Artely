package com.luis.artelyapp.ui.view.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.luis.artelyapp.model.Chat
import com.luis.artelyapp.model.Message

class ChatViewModel : ViewModel() {
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    init {
        loadSampleChats()
    }

    private fun loadSampleChats() {
        _chats.value = listOf(
            Chat(
                id_User = 1,
                userName = "Aristote",
                id_Chat = 1,
                messages = listOf(
                    Message(id_Chat = 1, id_Artist = 1, id_Customer = 2, content = "Hola, ya no se encuentra disponible, disculpa")
                )
            ),
            Chat(
                id_User = 2,
                userName = "Francisco López",
                id_Chat = 2,
                messages = listOf(
                    Message(id_Chat = 2, id_Artist = 2, id_Customer = 1, content = "Buenas noches, está en $1,200")
                )
            ),
            Chat(
                id_User = 3,
                userName = "Pablo",
                id_Chat = 3,
                messages = listOf(
                    Message(id_Chat = 3, id_Artist = 3, id_Customer = 1, content = "Perfecto, en eso quedamos")
                )
            ),
            Chat(
                id_User = 4,
                userName = "José Méndez",
                id_Chat = 4,
                messages = listOf(
                    Message(id_Chat = 4, id_Artist = 4, id_Customer = 1, content = "Si, de nada")
                )
            )
        )
    }
}