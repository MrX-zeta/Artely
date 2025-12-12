package com.luis.artelyapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Type alias para evitar conflictos con caché del compilador
typealias NotificationModel = com.luis.artelyapp.model.Notification

class NotificationViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notifications: StateFlow<List<NotificationModel>> = _notifications.asStateFlow()

    // Método según diagrama de clases
    fun send(customerId: String, messageText: String, location: String) {
        // TODO: Implementar lógica de envío de notificación
        // Normalmente esto estaría en un Repository
    }

    // Método según diagrama de clases
    fun markAsRead(notificationId: String) {
        // TODO: Marcar notificación como leída
        _notifications.value = _notifications.value.map { notification ->
            if (notification.id_Notification == notificationId) {
                // Actualizar estado de leída
                notification
            } else {
                notification
            }
        }
    }

    fun loadNotifications(customerId: String) {
        // TODO: Cargar notificaciones desde repository
        // Datos de ejemplo
        _notifications.value = listOf(
            NotificationModel(
                id_Notification = "1",
                id_Customer = customerId,
                MessageText = "Nueva obra disponible",
                Location = "Galería Principal"
            ),
            NotificationModel(
                id_Notification = "2",
                id_Customer = customerId,
                MessageText = "Mensaje nuevo",
                Location = "Chat"
            ),
            NotificationModel(
                id_Notification = "3",
                id_Customer = customerId,
                MessageText = "Tu oferta fue aceptada",
                Location = "Ventas"
            )
        )
    }
}

