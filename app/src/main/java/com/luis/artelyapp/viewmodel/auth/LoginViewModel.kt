package com.luis.artelyapp.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luis.artelyapp.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para el Login
 * Capa de presentación - Maneja la lógica de UI y estados
 */
class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // Estados de los campos de entrada
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _showPassword = MutableStateFlow(false)
    val showPassword: StateFlow<Boolean> = _showPassword.asStateFlow()

    // Estado de la UI
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el email
     */
    fun onEmailChange(value: String) {
        _email.value = value
        if (_uiState.value is LoginUiState.Error) {
            _uiState.value = LoginUiState.Idle
        }
    }

    /**
     * Actualiza la contraseña
     */
    fun onPasswordChange(value: String) {
        _password.value = value
        if (_uiState.value is LoginUiState.Error) {
            _uiState.value = LoginUiState.Idle
        }
    }

    /**
     * Alterna la visibilidad de la contraseña
     */
    fun togglePasswordVisibility() {
        _showPassword.value = !_showPassword.value
    }

    /**
     * Valida los campos del formulario
     */
    private fun validateInputs(): String? {
        return when {
            _email.value.isBlank() -> "El correo electrónico no puede estar vacío"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches() ->
                "Ingresa un correo electrónico válido"
            _password.value.isBlank() -> "La contraseña no puede estar vacía"
            _password.value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    /**
     * Realiza el login
     */
    fun login(onSuccess: () -> Unit) {
        // Validar entradas
        val validationError = validateInputs()
        if (validationError != null) {
            _uiState.value = LoginUiState.Error(validationError)
            return
        }

        // Iniciar proceso de login
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val result = authRepository.login(_email.value, _password.value)

                if (result.isSuccess) {
                    Log.d("LoginViewModel", "Login exitoso: ${result.getOrNull()?.email}")
                    _uiState.value = LoginUiState.Success
                    onSuccess()
                } else {
                    val exception = result.exceptionOrNull()
                    val errorMessage = when {
                        exception?.message?.contains("password") == true ->
                            "Contraseña incorrecta"
                        exception?.message?.contains("user") == true ->
                            "Usuario no encontrado"
                        exception?.message?.contains("network") == true ->
                            "Error de conexión. Verifica tu internet"
                        else -> exception?.message ?: "Error al iniciar sesión"
                    }
                    Log.e("LoginViewModel", "Error en login: $errorMessage", exception)
                    _uiState.value = LoginUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Excepción en login", e)
                _uiState.value = LoginUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    /**
     * Resetea el estado a Idle
     */
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    /**
     * Limpia los campos
     */
    fun clearFields() {
        _email.value = ""
        _password.value = ""
        _showPassword.value = false
        _uiState.value = LoginUiState.Idle
    }

    /**
     * Limpia la sesión actual de Firebase Auth
     */
    fun clearSession() {
        Log.d("LoginViewModel", "🔓 Cerrando sesión anterior de Firebase Auth")
        authRepository.logout()
        clearFields()
    }
}

