package com.luis.artelyapp.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.luis.artelyapp.ui.main.GalleryScreen
import com.luis.artelyapp.ui.view.ChatView
import com.luis.artelyapp.ui.view.MessageView
import com.luis.artelyapp.ui.ArtistProfile.ArtistProfileScreen

sealed class Screen(val route: String) {
    object Gallery : Screen("gallery")
    object Chat : Screen("chat")
    object Message : Screen("message/{chatId}") {
        fun createRoute(chatId: Int) = "message/$chatId"
    }
    object Profile : Screen("profile/{artistName}") {
        fun createRoute(artistName: String) = "profile/${Uri.encode(artistName)}"
    }
}

@Composable
fun NavManager() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Gallery.route
    ) {
        composable(Screen.Gallery.route) {
            GalleryScreen(
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route)
                },
                onArtistClick = { artistName ->
                    navController.navigate(Screen.Profile.createRoute(artistName))
                }
            )
        }

        composable(Screen.Chat.route) {
            ChatView(
                onNavigateToMessage = { chatId ->
                    navController.navigate(Screen.Message.createRoute(chatId))
                }
            )
        }

        composable(
            route = Screen.Message.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getInt("chatId") ?: 0
            MessageView(chatId = chatId)
        }

        composable(
            route = Screen.Profile.route,
            arguments = listOf(
                navArgument("artistName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val artistName = backStackEntry.arguments?.getString("artistName") ?: ""
            ArtistProfileScreen(
                artistName = artistName,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}