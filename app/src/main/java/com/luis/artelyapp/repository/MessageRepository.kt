package com.luis.artelyapp.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luis.artelyapp.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository para manejar operaciones de persistencia de Messages con Firebase
 * Según diagrama de clases, contiene métodos de negocio
 */
class MessageRepository {

    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.getReference("messages")

    companion object {
        private const val TAG = "MessageRepository"
    }

    /**
     * Envía un mensaje y lo guarda en Firebase
     */
    suspend fun sendMessage(
        chatId: String,
        artistId: String,
        customerId: String,
        senderId: String,
        content: String
    ): Result<Message> {
        return try {
            val newMessageRef = messagesRef.push()
            val messageId = newMessageRef.key ?: ""

            val newMessage = Message(
                id_Message = messageId,
                id_Chat = chatId,
                id_Artist = artistId,
                id_Customer = customerId,
                senderId = senderId,
                content = content,
                isRead = false
            )

            messagesRef.child(messageId).setValue(newMessage).await()
            Log.d(TAG, "Mensaje $messageId enviado correctamente por usuario $senderId")
            Result.success(newMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Error al enviar mensaje: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene los mensajes de un chat en TIEMPO REAL.
     * Devuelve un Flow que emite la lista actualizada cada vez que algo cambia en la BD.
     */
    fun getMessagesRealtime(chatId: String): Flow<List<Message>> = callbackFlow {
        val query = messagesRef.orderByChild("id_Chat").equalTo(chatId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(Message::class.java)
                }.sortedBy { it.id_Message } // Ordenar cronológicamente

                trySend(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        Log.d(TAG, "Iniciando escucha en tiempo real para chat: $chatId")
        query.addValueEventListener(listener)

        awaitClose {
            Log.d(TAG, "Deteniendo escucha en tiempo real para chat: $chatId")
            query.removeEventListener(listener)
        }
    }

    /**
     * Obtiene todos los mensajes de un chat (Una sola vez)
     */
    suspend fun getMessagesByChat(chatId: String): Result<List<Message>> {
        return try {
            val snapshot = messagesRef
                .orderByChild("id_Chat")
                .equalTo(chatId)
                .get()
                .await()

            val messages = snapshot.children.mapNotNull {
                it.getValue(Message::class.java)
            }.sortedBy { it.id_Message }

            Result.success(messages)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener mensajes: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Marca como LEÍDOS todos los mensajes de un chat que NO fueron enviados por el usuario actual.
     */
    suspend fun markMessagesAsRead(chatId: String, currentUserId: String): Result<Boolean> {
        return try {
            Log.d(TAG, "🔍 Buscando mensajes no leídos en chat: $chatId para usuario: $currentUserId")

            val snapshot = messagesRef
                .orderByChild("id_Chat")
                .equalTo(chatId)
                .get()
                .await()

            var markedCount = 0

            // Actualizar cada mensaje individualmente para asegurar propagación
            for (child in snapshot.children) {
                val message = child.getValue(Message::class.java)
                val messageKey = child.key

                if (message != null && messageKey != null) {
                    Log.d(TAG, "📨 Mensaje ${messageKey}: senderId=${message.senderId}, currentUser=$currentUserId, isRead=${message.isRead}")

                    if (message.senderId != currentUserId && !message.isRead) {
                        // Actualizar directamente el nodo individual
                        messagesRef.child(messageKey).child("isRead").setValue(true).await()
                        markedCount++
                        Log.d(TAG, "✅ Mensaje ${messageKey} marcado como leído")
                    }
                }
            }

            if (markedCount > 0) {
                Log.d(TAG, "✅ Se marcaron $markedCount mensajes como leídos en chat $chatId")
            } else {
                Log.d(TAG, "ℹ️ No hay mensajes para marcar como leídos en chat $chatId")
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al marcar mensajes como leídos: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ... otros métodos (getAllMessages, deleteMessage) se mantienen igual ...
    suspend fun getAllMessages(): Result<List<Message>> {
        return try {
            val snapshot = messagesRef.get().await()
            val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMessage(messageId: String): Result<Boolean> {
        return try {
            messagesRef.child(messageId).removeValue().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    fun getUnreadCountRealtime(chatId: String, currentUserId: String): Flow<Int> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val unreadCount = snapshot.children.mapNotNull {
                    it.getValue(Message::class.java)
                }.count { message ->
                    message.id_Chat == chatId &&
                    message.senderId != currentUserId &&
                    !message.isRead
                }
                trySend(unreadCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error al escuchar conteo de no leídos: ${error.message}")
                trySend(0)
            }
        }

        messagesRef.orderByChild("id_Chat").equalTo(chatId).addValueEventListener(listener)

        awaitClose {
            messagesRef.orderByChild("id_Chat").equalTo(chatId).removeEventListener(listener)
        }
    }


    fun getAllMessagesRealtime(): Flow<List<Message>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(Message::class.java)
                }
                Log.d(TAG, "🔄 Actualización global de mensajes: ${messages.size} mensajes")
                trySend(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error al escuchar mensajes globales: ${error.message}")
                close(error.toException())
            }
        }

        Log.d(TAG, "🎧 Iniciando listener global de mensajes")
        messagesRef.addValueEventListener(listener)

        awaitClose {
            Log.d(TAG, "🛑 Deteniendo listener global de mensajes")
            messagesRef.removeEventListener(listener)
        }
    }
}
