package com.luis.artelyapp.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.luis.artelyapp.ui.main.GalleryScreen
import com.luis.artelyapp.ui.view.ChatView
import com.luis.artelyapp.ui.view.MessageView
import com.luis.artelyapp.ui.ArtistProfile.ArtistProfileScreen
import com.luis.artelyapp.ui.UserProfile.UserProfileScreen
import com.luis.artelyapp.ui.UserProfile.UserProfileViewModel
import com.luis.artelyapp.ui.UserProfile.EditProfileScreen
import com.luis.artelyapp.ui.uploadwork.Upload
import com.luis.artelyapp.ui.CreatePost.CreatePostScreen

sealed class Screen(val route: String) {
    object Gallery : Screen("gallery")
    object Chat : Screen("chat")
    object Message : Screen("message/{chatId}") {
        fun createRoute(chatId: Int) = "message/$chatId"
    }
    object Profile : Screen("profile/{artistName}") {
        fun createRoute(artistName: String) = "profile/${Uri.encode(artistName)}"
    }
    object UserProfile : Screen("userprofile")
    object EditProfile : Screen("editprofile")
    object Upload : Screen("upload")
    object CreatePost : Screen("createpost")
}

@Composable
fun NavManager() {
    val navController = rememberNavController()
    val userProfileViewModel: UserProfileViewModel = viewModel()

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
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.UserProfile.route)
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

        composable(Screen.UserProfile.route) {
            UserProfileScreen(
                onBackClick = { navController.popBackStack() },
                onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToUpload = { navController.navigate(Screen.Upload.route) },
                onNavigateToGallery = { navController.navigate(Screen.Gallery.route) },
                onNavigateToSearch = { navController.navigate(Screen.Gallery.route) }, // Por ahora va a Gallery
                onNavigateToCreate = { navController.navigate(Screen.CreatePost.route) },
                viewModel = userProfileViewModel
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSave = { name, bio, location, profileImageUri ->
                    userProfileViewModel.updateProfile(name, bio, location, profileImageUri)
                },
                viewModel = userProfileViewModel
            )
        }

        composable(Screen.Upload.route) {
            Upload(
                onBackClick = { navController.popBackStack() },
                onPublishWork = { title, description, imageUri, isForSale ->
                    // Agregar la obra al ViewModel
                    userProfileViewModel.addArtwork(title, description, imageUri, isForSale)
                    // Regresar a UserProfile
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CreatePost.route) {
            CreatePostScreen(
                onBackClick = { navController.popBackStack() },
                onPublishPost = { title, description ->
                    // Por ahora solo regresamos a la pantalla anterior
                    navController.popBackStack()
                }
            )
        }
    }
}