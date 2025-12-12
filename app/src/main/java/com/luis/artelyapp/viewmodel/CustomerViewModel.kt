package com.luis.artelyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.luis.artelyapp.model.Customer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CustomerViewModel : ViewModel() {
    private val _currentCustomer = MutableStateFlow<Customer?>(null)
    val currentCustomer: StateFlow<Customer?> = _currentCustomer.asStateFlow()

    // Método según diagrama de clases
    fun sendMessage(chatId: Int, content: String) {
        // TODO: Implementar lógica de envío de mensaje
        // Delegar a MessageViewModel o ChatViewModel
    }

    // Método según diagrama de clases
    fun viewNotifications() {
        // TODO: Implementar lógica de visualización de notificaciones
        // Navegar a pantalla de notificaciones
    }

    // Método según diagrama de clases
    fun browseGallery() {
        // TODO: Implementar lógica de navegación a galería
    }

    fun loadCustomerData(customerId: Int) {
        // TODO: Cargar datos del cliente desde repository
    }
}

