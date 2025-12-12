package com.luis.artelyapp.view.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.luis.artelyapp.navigation.NavManager
import com.luis.artelyapp.view.auth.LoginScreen
import com.luis.artelyapp.view.auth.RegisterScreen
import com.luis.artelyapp.view.loading.LoadingScreen
import com.luis.artelyapp.view.onboarding.WelcomeScreen
import com.luis.artelyapp.viewmodel.MainViewModel
import com.luis.artelyapp.viewmodel.Screen

@Composable
fun NavigationHost(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    // Manejo del botón de retroceso del sistema
    BackHandler(enabled = true) {
        if (!viewModel.goBack()) {
            activity?.finish()
        }
    }

    // Renderizado de la pantalla actual
    when (currentScreen) {
        is Screen.Loading -> LoadingScreen()
        is Screen.Welcome -> WelcomeScreen(
            onExplore = { viewModel.navigateAndClearStack(Screen.Gallery) },
            onGetStarted = { viewModel.navigateTo(Screen.Register) }
        )
        is Screen.Register -> RegisterScreen(
            onRegistered = {
                // Después del registro, limpiar stack para que no se pueda volver a Welcome/Register
                viewModel.navigateAndClearStack(Screen.Gallery)
            },
            onBack = { viewModel.goBack() },
            onLogin = { viewModel.navigateTo(Screen.Login) }
        )
        is Screen.Login -> LoginScreen(
            onLogin = {
                // Después del login, limpiar stack para que no se pueda volver a Welcome/Login
                viewModel.navigateAndClearStack(Screen.Gallery)
            },
            onBack = { viewModel.goBack() },
            onRegister = { viewModel.navigateTo(Screen.Register) }
        )
        is Screen.Gallery -> GalleryScreen(
            onLogout = {
                // Navegar a Login y limpiar el back stack
                viewModel.navigateAndClearStack(Screen.Login)
            }
        )
    }
}

/**
 * Pantalla principal de la app
 */
@Composable
private fun GalleryScreen(onLogout: () -> Unit = {}) {
    NavManager(onLogout = onLogout)
}

