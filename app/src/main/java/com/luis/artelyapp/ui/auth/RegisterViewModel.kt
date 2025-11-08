package com.luis.artelyapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<Boolean?>(null)
    val success: StateFlow<Boolean?> = _success.asStateFlow()

    fun onUsernameChange(value: String) { _username.value = value }
    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun onConfirmPasswordChange(value: String) { _confirmPassword.value = value }

    fun submit() {
        // Validación simple
        if (_username.value.isBlank()) {
            _error.value = "Ingresa un nombre de usuario"
            return
        }
        if (_email.value.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            _error.value = "Ingresa un correo válido"
            return
        }
        if (_password.value.length < 6) {
            _error.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }
        if (_password.value != _confirmPassword.value) {
            _error.value = "Las contraseñas no coinciden"
            return
        }

        // Simular envío
        _error.value = null
        _loading.value = true
        viewModelScope.launch {
            delay(900)
            _loading.value = false
            _success.value = true
        }
    }

    fun clearSuccess() { _success.value = null }
}
