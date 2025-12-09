package com.luis.artelyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luis.artelyapp.model.Chat
import com.luis.artelyapp.model.Message
import com.luis.artelyapp.model.Artist
import com.luis.artelyapp.model.Customer
import com.luis.artelyapp.repository.ChatRepository
import com.luis.artelyapp.repository.MessageRepository
import com.luis.artelyapp.repository.ArtistRepository
import com.luis.artelyapp.repository.CustomerRepository
import com.luis.artelyapp.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Modelo auxiliar para la UI que combina Chat con información adicional
 */
data class ChatDisplay(
    val chat: Chat,
    val userName: String,
    val profileImageUrl: String = "",
    val messages: List<Message>,
    val lastMessageTime: Long = 0L,
    val unreadCount: Int = 0 // Nuevo campo: Cantidad de mensajes sin leer
)

/**
 * Estados de la lista de chats
 */
sealed class ChatUiState {
    object Loading : ChatUiState()
    data class Success(val chats: List<ChatDisplay>) : ChatUiState()
    object Empty : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

class ChatViewModel(
    private val chatRepository: ChatRepository = ChatRepository(),
    private val messageRepository: MessageRepository = MessageRepository(),
    private val artistRepository: ArtistRepository = ArtistRepository(),
    private val customerRepository: CustomerRepository = CustomerRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Mantenemos este para compatibilidad con la vista actual
    private val _chatsDisplay = MutableStateFlow<List<ChatDisplay>>(emptyList())
    val chats: StateFlow<List<ChatDisplay>> = _chatsDisplay.asStateFlow()

    init {
        loadChatsRealtime()
        startGlobalMessageListener()
    }

    /**
     * Inicia un listener global que escucha TODOS los cambios en mensajes
     * Esto permite actualizar el contador de no leídos en tiempo real
     */
    private fun startGlobalMessageListener() {
        viewModelScope.launch {
            messageRepository.getAllMessagesRealtime().collect { allMessages ->
                android.util.Log.d("ChatViewModel", "🔔 Cambios detectados en mensajes - Actualizando contadores")
                // Recargar los chats cuando detectemos cambios en los mensajes
                val currentUserId = authRepository.getCurrentUserId()
                if (currentUserId != null) {
                    chatRepository.getChatsByUser(currentUserId).fold(
                        onSuccess = { chatList ->
                            processChatList(chatList, currentUserId)
                        },
                        onFailure = { error ->
                            android.util.Log.e("ChatViewModel", "Error al actualizar chats: ${error.message}")
                        }
                    )
                }
            }
        }
    }


    fun loadChatsRealtime() {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading

            val currentUserId = authRepository.getCurrentUserId()

            if (currentUserId != null) {

                chatRepository.getChatsByUser(currentUserId).fold(
                    onSuccess = { chatList ->
                        processChatList(chatList, currentUserId)
                        // El startGlobalMessageListener ya maneja las actualizaciones en tiempo real
                        android.util.Log.d("ChatViewModel", "✅ Chats cargados. Listener global activo.")
                    },
                    onFailure = { error ->
                        _uiState.value = ChatUiState.Error(
                            error.message ?: "Error al cargar los chats"
                        )
                    }
                )
            } else {
                _uiState.value = ChatUiState.Error("Usuario no identificado")
            }
        }
    }

    /**
     * OBSOLETO: Ya no se usa porque startGlobalMessageListener es más eficiente
     * Inicia actualizaciones en tiempo real para los chats
     */
    /*
    private fun startRealtimeUpdates(currentUserId: String) {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000) // Actualizar cada 1 segundo (más rápido)
                chatRepository.getChatsByUser(currentUserId).fold(
                    onSuccess = { chatList ->
                        processChatList(chatList, currentUserId)
                    },
                    onFailure = { /* Ignorar errores silenciosamente en actualizaciones */ }
                )
            }
        }
    }
    */

    /**
     * Carga todos los chats desde Firebase (versión no en tiempo real)
     */
    fun loadChats() {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading

            val currentUserId = authRepository.getCurrentUserId()
            
            if (currentUserId != null) {
                chatRepository.getChatsByUser(currentUserId).fold(
                    onSuccess = { chatList ->
                        processChatList(chatList, currentUserId)
                    },
                    onFailure = { error ->
                        _uiState.value = ChatUiState.Error(
                            error.message ?: "Error al cargar los chats"
                        )
                    }
                )
            } else {
                _uiState.value = ChatUiState.Error("Usuario no identificado")
            }
        }
    }

    /**
     * Procesa la lista de chats para añadir mensajes y metadatos
     */
    private suspend fun processChatList(chatList: List<Chat>, currentUserId: String) {
        if (chatList.isEmpty()) {
            _uiState.value = ChatUiState.Empty
            _chatsDisplay.value = emptyList()
        } else {
            // Cargar mensajes y detalles para cada chat
            val chatsWithMessages = chatList.map { chat ->
                loadChatMessages(chat, currentUserId)
            }
            // Ordenar por el mensaje más reciente
            .sortedByDescending { it.lastMessageTime }

            _chatsDisplay.value = chatsWithMessages
            _uiState.value = ChatUiState.Success(chatsWithMessages)
        }
    }

    /**
     * Carga los mensajes de un chat específico y los datos del otro usuario
     */
    private suspend fun loadChatMessages(chat: Chat, currentUserId: String): ChatDisplay {
        val messages = messageRepository.getMessagesByChat(chat.id_Chat).getOrElse {
            emptyList()
        }

        // Determinar quién es el "otro" usuario en el chat
        val otherUserId = if (currentUserId == chat.id_Artist) {
            chat.id_Customer 
        } else {
            chat.id_Artist 
        }

        // Calcular mensajes no leídos (aquellos que NO envié yo y isRead es false)
        val unreadCount = messages.count { !it.isRead && it.senderId != currentUserId }

        // LOG resumido solo si hay mensajes no leídos
        if (unreadCount > 0) {
            android.util.Log.d("ChatViewModel", "📊 Chat ${chat.id_Chat.take(8)}...: $unreadCount mensajes NO LEÍDOS de ${messages.size} totales")
        }

        // Obtener tiempo del último mensaje
        val lastMessageTime = messages.lastOrNull()?.id_Message?.toLongOrNull() ?: 0L 
        // Nota: Si usas timestamps reales en Message, úsalos aquí. 
        // Si usas push() keys de Firebase, contienen el timestamp implícito pero es complejo extraerlo.
        // Para simplificar, asumimos que el orden de la lista es cronológico.

        // Intentar cargar datos del otro usuario
        var userName = "Usuario"
        var profileImageUrl = ""

        // Intentar obtener datos como artista
        val artist = artistRepository.getArtistById(otherUserId)
        if (artist != null) {
            userName = artist.UserName
            profileImageUrl = artist.profileImageUrl
        } else {
            // Si no es artista, intentar como customer
            val customer = customerRepository.getCustomerById(otherUserId)
            if (customer != null) {
                userName = customer.UserName
                profileImageUrl = customer.profileImageUrl
            }
        }

        return ChatDisplay(
            chat = chat,
            userName = userName,
            profileImageUrl = profileImageUrl,
            messages = messages,
            lastMessageTime = lastMessageTime, // O System.currentTimeMillis() si prefieres
            unreadCount = unreadCount
        )
    }

    /**
     * Obtiene un chat por su ID
     */
    fun getChatById(chatId: String): ChatDisplay? {
        return _chatsDisplay.value.find { it.chat.id_Chat == chatId }
    }

    /**
     * Recarga los chats, iniciando el monitoreo en tiempo real
     */
    fun refresh() {
        loadChatsRealtime()
    }
}
