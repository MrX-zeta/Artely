package com.luis.artelyapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface Screen {
    @kotlinx.serialization.Serializable
    data object Loading : Screen
    @kotlinx.serialization.Serializable
    data object Welcome : Screen
    @kotlinx.serialization.Serializable
    data object Register : Screen
    @kotlinx.serialization.Serializable
    data object Login : Screen
    @kotlinx.serialization.Serializable
    data object Gallery : Screen
}

sealed interface NavigationEvent {
    data object NavigateBack : NavigationEvent
    data object ExitApp : NavigationEvent
}

class MainViewModel(
    private val authRepository: com.luis.artelyapp.repository.AuthRepository = com.luis.artelyapp.repository.AuthRepository()
) : ViewModel() {
    // Empezamos con Loading para evitar destellos de pantallas incorrectas
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Loading)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val navStack = mutableListOf<Screen>(Screen.Loading)

    init {
        // Determinar la pantalla inicial de forma asíncrona
        determineInitialScreen()
    }

    /**
     * Determina la pantalla inicial según:
     * 1. Si hay un usuario autenticado Y tiene perfil en BD → Gallery
     * 2. Si hay un usuario autenticado pero NO tiene perfil → Logout y Welcome (sesión corrupta)
     * 3. Si hay usuarios registrados pero nadie autenticado → Login
     * 4. Si no hay usuarios registrados → Welcome
     */
    private fun determineInitialScreen() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val currentUser = authRepository.getCurrentUser()

                if (currentUser != null) {
                    android.util.Log.d("MainViewModel", "🔍 Usuario autenticado encontrado: ${currentUser.email}")

                    // Verificar si el usuario tiene un perfil en la base de datos
                    val userRole = authRepository.getCurrentUserRole().getOrNull()

                    if (userRole != null) {
                        // Caso 1: Usuario autenticado con perfil válido → Gallery
                        android.util.Log.d("MainViewModel", "🚀 Usuario con perfil válido ($userRole) → Gallery")
                        updateInitialScreen(Screen.Gallery)
                    } else {
                        // Caso 2: Usuario autenticado pero sin perfil en BD (sesión corrupta)
                        android.util.Log.w("MainViewModel", "⚠️ Usuario autenticado sin perfil en BD → Limpiando sesión")
                        authRepository.logout()

                        // Verificar si hay otros usuarios registrados
                        val hasUsers = authRepository.hasRegisteredUsers()
                        if (hasUsers) {
                            android.util.Log.d("MainViewModel", "🚀 Sesión limpiada, hay otros usuarios → Login")
                            updateInitialScreen(Screen.Login)
                        } else {
                            android.util.Log.d("MainViewModel", "🚀 Sesión limpiada, no hay usuarios → Welcome")
                            updateInitialScreen(Screen.Welcome)
                        }
                    }
                } else {
                    android.util.Log.d("MainViewModel", "🔍 No hay usuario autenticado")

                    // Verificar si hay usuarios registrados
                    val hasUsers = authRepository.hasRegisteredUsers()

                    if (hasUsers) {
                        // Caso 3: Hay usuarios pero nadie autenticado → Login
                        android.util.Log.d("MainViewModel", "🚀 Hay usuarios registrados → Login")
                        updateInitialScreen(Screen.Login)
                    } else {
                        // Caso 4: No hay usuarios → Welcome
                        android.util.Log.d("MainViewModel", "🚀 No hay usuarios → Welcome")
                        updateInitialScreen(Screen.Welcome)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "❌ Error al determinar pantalla inicial: ${e.message}", e)
                // En caso de error, mostrar Welcome (comportamiento seguro)
                updateInitialScreen(Screen.Welcome)
            }
        }
    }

    private fun updateInitialScreen(screen: Screen) {
        navStack.clear()
        navStack.add(screen)
        _currentScreen.value = screen
    }

    fun navigateTo(screen: Screen) {
        navStack.add(screen)
        _currentScreen.value = screen
    }

    fun goBack(): Boolean {
        return if (navStack.size > 1) {
            navStack.removeAt(navStack.lastIndex)
            _currentScreen.value = navStack.last()
            true
        } else {
            false
        }
    }

    fun canGoBack(): Boolean = navStack.size > 1

    fun resetNavigation() {
        navStack.clear()
        navStack.add(Screen.Welcome)
        _currentScreen.value = Screen.Welcome
    }

    fun replaceScreen(screen: Screen) {
        if (navStack.isNotEmpty()) {
            navStack.removeAt(navStack.lastIndex)
        }
        navStack.add(screen)
        _currentScreen.value = screen
    }

    /**
     * Navega a una pantalla limpiando todo el historial anterior
     * Útil después de login/registro para evitar volver a esas pantallas
     */
    fun navigateAndClearStack(screen: Screen) {
        navStack.clear()
        navStack.add(screen)
        _currentScreen.value = screen
        android.util.Log.d("MainViewModel", "🧹 Stack limpiado - Nueva pantalla: $screen")
    }
}

