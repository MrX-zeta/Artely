package com.luis.artelyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luis.artelyapp.model.Message
import com.luis.artelyapp.repository.AuthRepository
import com.luis.artelyapp.repository.ChatRepository
import com.luis.artelyapp.repository.MessageRepository
import com.luis.artelyapp.repository.ArtistRepository
import com.luis.artelyapp.repository.CustomerRepository
import com.luis.artelyapp.model.Artist
import com.luis.artelyapp.model.Customer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OtherUser(
    val id: String,
    val userName: String,
    val profileImageUrl: String,
    val isArtist: Boolean
)

class MessageViewModel(
    private val messageRepository: MessageRepository = MessageRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val chatRepository: ChatRepository = ChatRepository(),
    private val artistRepository: ArtistRepository = ArtistRepository(),
    private val customerRepository: CustomerRepository = CustomerRepository()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _currentMessage = MutableStateFlow("")
    val currentMessage: StateFlow<String> = _currentMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _otherUser = MutableStateFlow<OtherUser?>(null)
    val otherUser: StateFlow<OtherUser?> = _otherUser.asStateFlow()

    private val _isOtherUserOnline = MutableStateFlow(false)
    val isOtherUserOnline: StateFlow<Boolean> = _isOtherUserOnline.asStateFlow()

    private val _artist = MutableStateFlow<Artist?>(null)
    val artist: StateFlow<Artist?> = _artist.asStateFlow()

    private val _isArtistOnline = MutableStateFlow(false)
    val isArtistOnline: StateFlow<Boolean> = _isArtistOnline.asStateFlow()

    private var currentChatId: String = ""


    var currentUserId: String = ""
        private set

    companion object {
        private const val TAG = "MessageViewModel"
    }

    init {
        currentUserId = authRepository.getCurrentUserId() ?: ""
        Log.d(TAG, "Usuario actual ID: $currentUserId")
    }

    fun loadMessagesForChat(chatId: String) {
        currentChatId = chatId

        // Marcar mensajes como leídos inmediatamente al entrar al chat
        viewModelScope.launch {
            markMessagesAsRead(chatId)
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "Cargando mensajes en tiempo real para chat: $chatId")

            messageRepository.getMessagesRealtime(chatId).collect { messagesList ->
                _messages.value = messagesList
                Log.d(TAG, "✅ Actualización en tiempo real: ${messagesList.size} mensajes")

                // Volver a marcar como leídos cada vez que llegan nuevos mensajes
                markMessagesAsRead(chatId)
            }
        }

        viewModelScope.launch {
            loadOtherUserData(chatId)
            _isLoading.value = false
        }
    }

    fun markMessagesAsRead(chatId: String) {
        viewModelScope.launch {
            // Pasamos currentUserId para no marcar como leídos MIS propios mensajes
            messageRepository.markMessagesAsRead(chatId, currentUserId).onFailure { e ->
                 Log.e(TAG, "Error al marcar mensajes como leídos: ${e.message}")
            }
        }
    }

    private suspend fun loadOtherUserData(chatId: String) {
        chatRepository.getParticipants(chatId).fold(
            onSuccess = { participants ->
                if (participants.size >= 2) {
                    // participants[0] es el artistId, participants[1] es el customerId
                    val artistId = participants[0]
                    val customerId = participants[1]

                    // Determinar quién es el "otro" usuario
                    val otherUserId = if (currentUserId == artistId) {
                        customerId // Si soy el artista, el otro es el customer
                    } else {
                        artistId // Si soy el customer, el otro es el artista
                    }

                    val isOtherUserArtist = (otherUserId == artistId)

                    Log.d(TAG, "Usuario actual: $currentUserId")
                    Log.d(TAG, "Cargando datos del otro usuario: $otherUserId (${if (isOtherUserArtist) "Artista" else "Customer"})")

                    if (isOtherUserArtist) {
                        // El otro usuario es un artista
                        val artistData = artistRepository.getArtistById(otherUserId)
                        if (artistData != null) {
                            _otherUser.value = OtherUser(
                                id = artistData.id_User,
                                userName = artistData.UserName,
                                profileImageUrl = artistData.profileImageUrl,
                                isArtist = true
                            )
                            // Mantener compatibilidad
                            _artist.value = artistData
                            _isArtistOnline.value = false

                            Log.d(TAG, "✅ Datos del artista cargados: ${artistData.UserName}")
                        } else {
                            Log.w(TAG, "⚠️ No se encontraron datos del artista")
                        }
                    } else {
                        // El otro usuario es un customer
                        val customerData = customerRepository.getCustomerById(otherUserId)
                        if (customerData != null) {
                            _otherUser.value = OtherUser(
                                id = customerData.id_User,
                                userName = customerData.UserName,
                                profileImageUrl = customerData.profileImageUrl,
                                isArtist = false
                            )
                            // Mantener compatibilidad con vista antigua
                            _artist.value = Artist(
                                id_User = customerData.id_User,
                                UserName = customerData.UserName,
                                Email = customerData.Email,
                                profileImageUrl = customerData.profileImageUrl
                            )
                            _isArtistOnline.value = false

                            Log.d(TAG, "✅ Datos del customer cargados: ${customerData.UserName}")
                        } else {
                            Log.w(TAG, "⚠️ No se encontraron datos del customer")
                        }
                    }

                    _isOtherUserOnline.value = false // Por ahora simulado
                }
            },
            onFailure = { exception ->
                Log.e(TAG, "❌ Error al obtener participantes del chat: ${exception.message}")
            }
        )
    }

    /**
     * Actualiza el contenido del mensaje actual
     */
    fun updateCurrentMessage(message: String) {
        _currentMessage.value = message
    }

    /**
     * Envía un mensaje al chat actual
     */
    fun sendMessage() {
        if (_currentMessage.value.isBlank()) {
            Log.w(TAG, "⚠️ Intento de enviar mensaje vacío")
            return
        }
        
        // NO cambiamos isLoading a true aquí para evitar parpadeos en la UI,
        // ya que la actualización vendrá por el canal realtime

        viewModelScope.launch {
            _error.value = null

            Log.d(TAG, "Enviando mensaje al chat: $currentChatId")

            // Obtener los participantes del chat para determinar artistId y customerId
            chatRepository.getParticipants(currentChatId).fold(
                onSuccess = { participants ->
                    if (participants.size >= 2) {
                        val artistId = participants[0]
                        val customerId = participants[1]

                        // Enviar el mensaje con el senderId (quien realmente lo envía)
                        messageRepository.sendMessage(
                            chatId = currentChatId,
                            artistId = artistId,
                            customerId = customerId,
                            senderId = currentUserId,  // 👈 NUEVO: Quién envía el mensaje
                            content = _currentMessage.value
                        ).fold(
                            onSuccess = { 
                                // Éxito: Limpiamos el campo de texto
                                // NO agregamos el mensaje manualmente a _messages,
                                // esperamos a que Firebase nos avise por el canal realtime
                                _currentMessage.value = ""
                                Log.d(TAG, "✅ Mensaje enviado correctamente por usuario $currentUserId")
                            },
                            onFailure = { exception ->
                                _error.value = "Error al enviar mensaje: ${exception.message}"
                                Log.e(TAG, "❌ Error al enviar mensaje: ${exception.message}")
                            }
                        )
                    } else {
                        _error.value = "No se pudieron obtener los participantes del chat"
                        Log.e(TAG, "❌ Participantes insuficientes en el chat")
                    }
                },
                onFailure = { exception ->
                    _error.value = "Error al obtener participantes: ${exception.message}"
                    Log.e(TAG, "❌ Error al obtener participantes: ${exception.message}")
                }
            )
        }
    }

    /**
     * Determina si un mensaje fue enviado por el usuario actual
     *
     * Ahora usa el campo 'senderId' que identifica directamente quién envió el mensaje
     */
    fun isMessageFromCurrentUser(message: Message): Boolean {
        val isFromCurrentUser = message.senderId == currentUserId
        return isFromCurrentUser
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Refresca los mensajes del chat actual
     */
    fun refreshMessages() {
        if (currentChatId.isNotEmpty()) {
            loadMessagesForChat(currentChatId)
        }
    }
}
