package com.luis.artelyapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.luis.artelyapp.ui.auth.LoginScreen
import com.luis.artelyapp.ui.auth.RegisterScreen
import com.luis.artelyapp.ui.onboarding.WelcomeScreen
import com.luis.artelyapp.ui.theme.ArtelyAppTheme
import androidx.compose.ui.platform.LocalContext
import com.luis.artelyapp.ui.navigation.NavManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArtelyAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RootContent()
                }
            }
        }
    }
}

private sealed class Screen {
    object Welcome : Screen()
    object Register : Screen()
    object Login : Screen()
    object Gallery : Screen()
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
        is Screen.Welcome -> PhoneContainer { WelcomeScreen(
            onExplore = { navigateTo(Screen.Gallery) },
            onGetStarted = { navigateTo(Screen.Register) }
        ) }

        is Screen.Register -> PhoneContainer { RegisterScreen(
            onRegistered = { navigateTo(Screen.Gallery) },
            onBack = { goBack() },
            onLogin = { navigateTo(Screen.Login) }
        ) }

        is Screen.Login -> PhoneContainer { LoginScreen(
            onLogin = { navigateTo(Screen.Gallery) },
            onBack = { goBack() },
            onRegister = { navigateTo(Screen.Register) }
        ) }

        is Screen.Gallery -> PhoneContainer(clip = false) { NavManager() }
    }
}

@Composable
fun PhoneContainer(clip: Boolean = true, content: @Composable () -> Unit) {
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D))
    )

    val baseModifier = if (clip) {
        Modifier.clip(RoundedCornerShape(24.dp)).background(backgroundBrush)
    } else {
        Modifier.background(backgroundBrush)
    }

    Box(
        modifier = baseModifier
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
