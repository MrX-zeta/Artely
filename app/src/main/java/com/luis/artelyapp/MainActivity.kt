package com.luis.artelyapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.luis.artelyapp.ui.auth.LoginScreen
import com.luis.artelyapp.ui.auth.RegisterScreen
import com.luis.artelyapp.ui.main.GalleryScreen
import com.luis.artelyapp.ui.onboarding.WelcomeScreen
import com.luis.artelyapp.ui.UserProfile.UserProfileScreen
import com.luis.artelyapp.ui.CreatePost.CreatePostScreen
import com.luis.artelyapp.ui.theme.ArtelyAppTheme
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArtelyAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PhoneContainer { RootContent() }
                }
            }
        }
    }
}

private sealed class Screen {
    object Welcome : Screen()
    object Register : Screen()
    object Login : Screen()
    data class Gallery(val isUserAuthenticated: Boolean = false) : Screen()
    object CreatePost : Screen()
    object UserProfile : Screen()
}

@Composable
private fun RootContent() {
    val navStack = remember { mutableStateListOf<Screen>(Screen.Welcome) }
    val current = navStack.last()

    val context = LocalContext.current
    val activity = (context as? Activity)

    fun navigateTo(screen: Screen) {
        navStack.add(screen)
    }
    fun goBack() {
        if (navStack.size > 1) navStack.removeAt(navStack.lastIndex)
        else activity?.finish()
    }

    BackHandler(enabled = true) { goBack() }

    when (current) {
        is Screen.Welcome -> WelcomeScreen(
            onExplore = { navigateTo(Screen.Gallery(isUserAuthenticated = false)) },
            onGetStarted = { navigateTo(Screen.Register) }
        )
        is Screen.Register -> RegisterScreen(
            onRegistered = { navigateTo(Screen.Gallery(isUserAuthenticated = true)) },
            onBack = { goBack() },
            onLogin = { navigateTo(Screen.Login) }
        )
        is Screen.Login -> LoginScreen(
            onLogin = { navigateTo(Screen.Gallery(isUserAuthenticated = true)) },
            onBack = { goBack() },
            onRegister = { navigateTo(Screen.Register) }
        )
        is Screen.Gallery -> GalleryScreen(
            onProfileClick = {
                if (current.isUserAuthenticated) {
                    navigateTo(Screen.UserProfile)
                } else {
                    navigateTo(Screen.Login)
                }
            },
            onAddPostClick = {
                if (current.isUserAuthenticated) {
                    navigateTo(Screen.CreatePost)
                } else {
                    navigateTo(Screen.Login)
                }
            }
        )
        is Screen.UserProfile -> UserProfileScreen(
            onBackClick = { goBack() }
        )
        is Screen.CreatePost -> CreatePostScreen(
            onBackClick = { goBack() },
            onPublishPost = { title, description ->
                // Aquí puedes manejar la lógica para guardar el post
                goBack() // Regresa a la galería después de publicar
            }
        )
    }
}

@Composable
fun PhoneContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
