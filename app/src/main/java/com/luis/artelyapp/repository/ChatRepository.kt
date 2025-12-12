package com.luis.artelyapp.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.luis.artelyapp.model.Chat
import kotlinx.coroutines.tasks.await

/**
 * Repository para manejar operaciones de persistencia de Chats con Firebase
 * Según diagrama de clases, contiene métodos de negocio
 */
class ChatRepository {

    private val database = FirebaseDatabase.getInstance()
    private val chatsRef = database.getReference("chats")

    companion object {
        private const val TAG = "ChatRepository"
    }

    /**
     * Método según diagrama de clases - loadMessages()
     * Obtiene un chat por su ID desde Firebase
     */
    suspend fun loadMessages(chatId: String): Result<Chat> {
        return try {
            val snapshot = chatsRef.child(chatId).get().await()
            val chat = snapshot.getValue(Chat::class.java)

            if (chat != null) {
                Log.d(TAG, "Chat $chatId cargado correctamente")
                Result.success(chat)
            } else {
                Log.w(TAG, "Chat $chatId no encontrado")
                Result.failure(Exception("Chat no encontrado"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar chat: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Método según diagrama de clases - getParticipants()
     * Obtiene los participantes de un chat (artistId, customerId)
     */
    suspend fun getParticipants(chatId: String): Result<List<String>> {
        return try {
            val snapshot = chatsRef.child(chatId).get().await()
            val chat = snapshot.getValue(Chat::class.java)

            if (chat != null) {
                val participants = listOf(chat.id_Artist, chat.id_Customer)
                Log.d(TAG, "Participantes del chat $chatId: $participants")
                Result.success(participants)
            } else {
                Result.failure(Exception("Chat no encontrado"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener participantes: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los chats de un usuario (como artista o como cliente)
     */
    suspend fun getChatsByUser(userId: String): Result<List<Chat>> {
        return try {
            val snapshot = chatsRef.get().await()
            val chats = snapshot.children.mapNotNull {
                it.getValue(Chat::class.java)
            }.filter {
                it.id_Artist == userId || it.id_Customer == userId
            }

            Log.d(TAG, "Se obtuvieron ${chats.size} chats del usuario $userId")
            Result.success(chats)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener chats del usuario: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los chats desde Firebase
     */
    suspend fun getAllChats(): Result<List<Chat>> {
        return try {
            val snapshot = chatsRef.get().await()
            val chats = snapshot.children.mapNotNull {
                it.getValue(Chat::class.java)
            }

            Log.d(TAG, "Se obtuvieron ${chats.size} chats")
            Result.success(chats)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener todos los chats: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Crea un nuevo chat entre un artista y un cliente
     */
    suspend fun createChat(artistId: String, customerId: String): Result<Chat> {
        return try {
            // Generar nuevo ID
            val newChatRef = chatsRef.push()
            val chatId = newChatRef.key ?: ""

            val newChat = Chat(
                id_Chat = chatId,
                id_Artist = artistId,
                id_Customer = customerId
            )

            chatsRef.child(chatId).setValue(newChat).await()
            Log.d(TAG, "Chat $chatId creado correctamente")
            Result.success(newChat)
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear chat: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Busca si existe un chat entre un artista y un cliente
     */
    suspend fun findChatBetweenUsers(artistId: String, customerId: String): Result<Chat?> {
        return try {
            val snapshot = chatsRef.get().await()
            val chat = snapshot.children.mapNotNull {
                it.getValue(Chat::class.java)
            }.find {
                (it.id_Artist == artistId && it.id_Customer == customerId) ||
                (it.id_Artist == customerId && it.id_Customer == artistId)
            }

            Result.success(chat)
        } catch (e: Exception) {
            Log.e(TAG, "Error al buscar chat: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Elimina un chat de Firebase
     */
    suspend fun deleteChat(chatId: String): Result<Boolean> {
        return try {
            chatsRef.child(chatId).removeValue().await()
            Log.d(TAG, "Chat $chatId eliminado correctamente")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar chat: ${e.message}")
            Result.failure(e)
        }
    }
}
