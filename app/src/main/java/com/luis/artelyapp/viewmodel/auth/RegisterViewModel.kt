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
 * ViewModel para el Registro
 * Capa de presentación - Maneja la lógica de UI y estados
 */
class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // Estados de los campos de entrada
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _userType = MutableStateFlow("Comprador")
    val userType: StateFlow<String> = _userType.asStateFlow()

    private val _showPassword = MutableStateFlow(false)
    val showPassword: StateFlow<Boolean> = _showPassword.asStateFlow()

    private val _showConfirmPassword = MutableStateFlow(false)
    val showConfirmPassword: StateFlow<Boolean> = _showConfirmPassword.asStateFlow()

    // Estado de la UI
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el nombre de usuario
     */
    fun onUsernameChange(value: String) {
        _username.value = value
        if (_uiState.value is RegisterUiState.Error) {
            _uiState.value = RegisterUiState.Idle
        }
    }

    /**
     * Actualiza el email
     */
    fun onEmailChange(value: String) {
        _email.value = value
        if (_uiState.value is RegisterUiState.Error) {
            _uiState.value = RegisterUiState.Idle
        }
    }

    /**
     * Actualiza la contraseña
     */
    fun onPasswordChange(value: String) {
        _password.value = value
        if (_uiState.value is RegisterUiState.Error) {
            _uiState.value = RegisterUiState.Idle
        }
    }

    /**
     * Actualiza la confirmación de contraseña
     */
    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
        if (_uiState.value is RegisterUiState.Error) {
            _uiState.value = RegisterUiState.Idle
        }
    }

    /**
     * Actualiza el tipo de usuario
     */
    fun onUserTypeChange(value: String) {
        _userType.value = value
    }

    /**
     * Alterna la visibilidad de la contraseña
     */
    fun togglePasswordVisibility() {
        _showPassword.value = !_showPassword.value
    }

    /**
     * Alterna la visibilidad de la confirmación de contraseña
     */
    fun toggleConfirmPasswordVisibility() {
        _showConfirmPassword.value = !_showConfirmPassword.value
    }

    /**
     * Valida los campos del formulario
     */
    private fun validateInputs(): String? {
        return when {
            _username.value.isBlank() -> "El nombre de usuario no puede estar vacío"
            _username.value.length < 3 -> "El nombre de usuario debe tener al menos 3 caracteres"
            _email.value.isBlank() -> "El correo electrónico no puede estar vacío"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches() ->
                "Ingresa un correo electrónico válido"
            _password.value.isBlank() -> "La contraseña no puede estar vacía"
            _password.value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            _confirmPassword.value.isBlank() -> "Confirma tu contraseña"
            _password.value != _confirmPassword.value -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    /**
     * Realiza el registro
     */
    fun register(onSuccess: () -> Unit) {
        // Validar entradas
        val validationError = validateInputs()
        if (validationError != null) {
            _uiState.value = RegisterUiState.Error(validationError)
            return
        }

        // Iniciar proceso de registro
        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            try {
                // 1. Crear usuario en Firebase Auth
                val registerResult = authRepository.register(_email.value, _password.value)

                if (registerResult.isFailure) {
                    val exception = registerResult.exceptionOrNull()
                    val errorMessage = when {
                        exception?.message?.contains("already in use") == true ->
                            "Este correo ya está registrado"
                        exception?.message?.contains("network") == true ->
                            "Error de conexión. Verifica tu internet"
                        else -> exception?.message ?: "Error al crear la cuenta"
                    }
                    Log.e("RegisterViewModel", "Error en registro: $errorMessage", exception)
                    _uiState.value = RegisterUiState.Error(errorMessage)
                    return@launch
                }

                val firebaseUser = registerResult.getOrNull()
                if (firebaseUser == null) {
                    _uiState.value = RegisterUiState.Error("Error al obtener el usuario")
                    return@launch
                }

                // 2. Guardar perfil en Realtime Database
                Log.d("RegisterViewModel", "📝 Guardando perfil en BD para ${_userType.value}: ${_email.value}")
                val saveResult = if (_userType.value == "Artista") {
                    authRepository.saveArtistProfile(
                        userId = firebaseUser.uid,
                        username = _username.value,
                        email = _email.value
                    )
                } else {
                    authRepository.saveCustomerProfile(
                        userId = firebaseUser.uid,
                        username = _username.value,
                        email = _email.value
                    )
                }

                if (saveResult.isSuccess) {
                    Log.d("RegisterViewModel", "✅ Perfil guardado correctamente en BD")

                    // 3. Verificar que el perfil se guardó correctamente
                    Log.d("RegisterViewModel", "🔍 Verificando que el perfil existe en BD...")
                    kotlinx.coroutines.delay(800) // Delay para asegurar que la BD se actualizó completamente

                    val roleVerification = authRepository.getCurrentUserRole().getOrNull()
                    if (roleVerification != null) {
                        Log.d("RegisterViewModel", "✅ Perfil verificado en BD con rol: $roleVerification")
                        Log.d("RegisterViewModel", "🚀 Registro completado exitosamente para ${_userType.value}: ${_email.value}")

                        // Cambiar estado a Success
                        _uiState.value = RegisterUiState.Success

                        // Pequeño delay adicional para asegurar propagación completa
                        kotlinx.coroutines.delay(300)

                        // Ejecutar callback de navegación
                        Log.d("RegisterViewModel", "🧭 Ejecutando navegación a Gallery...")
                        onSuccess()
                    } else {
                        Log.e("RegisterViewModel", "❌ Error: Perfil no encontrado en BD después de guardarlo")
                        _uiState.value = RegisterUiState.Error("Error al verificar el perfil guardado")
                        // Limpiar la sesión corrupta
                        authRepository.logout()
                    }
                } else {
                    val errorMessage = saveResult.exceptionOrNull()?.message
                        ?: "Error al guardar el perfil"
                    Log.e("RegisterViewModel", "❌ Error al guardar perfil: $errorMessage")
                    _uiState.value = RegisterUiState.Error(errorMessage)
                    // Limpiar el usuario de Auth si no se pudo guardar el perfil
                    authRepository.logout()
                }

            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Excepción en registro", e)
                _uiState.value = RegisterUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    /**
     * Resetea el estado a Idle
     */
    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }

    /**
     * Limpia todos los campos
     */
    fun clearFields() {
        _username.value = ""
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
        _userType.value = "Comprador"
        _showPassword.value = false
        _showConfirmPassword.value = false
        _uiState.value = RegisterUiState.Idle
    }

    /**
     * Limpia el estado de éxito (usado en LaunchedEffect)
     */
    fun clearSuccess() {
        if (_uiState.value is RegisterUiState.Success) {
            _uiState.value = RegisterUiState.Idle
        }
    }
}

