package com.luis.artelyapp.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.luis.artelyapp.view.main.GalleryScreen
import com.luis.artelyapp.view.chatView.ChatView
import com.luis.artelyapp.view.messageView.MessageView
import com.luis.artelyapp.view.ArtistProfile.ArtistProfileScreen
import com.luis.artelyapp.view.ArtworkDetail.ArtworkDetailScreen
import com.luis.artelyapp.view.UserProfile.UserProfileScreen
import com.luis.artelyapp.view.UserProfile.UserProfileViewModel
import com.luis.artelyapp.view.UserProfile.EditProfileScreen
import com.luis.artelyapp.view.uploadwork.Upload
import com.luis.artelyapp.view.EditArtwork.EditArtworkScreen
import com.luis.artelyapp.view.CreatePost.CreatePostScreen
import com.luis.artelyapp.view.Favorites.FavoritesScreen

sealed class Screen(val route: String) {
    object Gallery : Screen("gallery")
    object Chat : Screen("chat")
    object Message : Screen("message/{chatId}") {
        fun createRoute(chatId: String) = "message/$chatId"
    }
    object Profile : Screen("profile/{artistId}") {
        fun createRoute(artistId: String) = "profile/${Uri.encode(artistId)}"
    }
    object ArtworkDetail : Screen("artworkdetail/{artworkId}/{artistId}") {
        fun createRoute(artworkId: String, artistId: String) = "artworkdetail/$artworkId/$artistId"
    }
    object UserProfile : Screen("userprofile")
    object EditProfile : Screen("editprofile")
    object Upload : Screen("upload")
    object EditArtwork : Screen("editartwork/{artworkId}") {
        fun createRoute(artworkId: String) = "editartwork/$artworkId"
    }
    object CreatePost : Screen("createpost")
    object Favorites : Screen("favorites")
}

@Composable
fun NavManager(onLogout: () -> Unit = {}) {
    val navController = rememberNavController()
    val userProfileViewModel: UserProfileViewModel = viewModel()
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity

    NavHost(
        navController = navController,
        startDestination = Screen.Gallery.route
    ) {
        composable(Screen.Gallery.route) {
            // BackHandler para Gallery: salir de la app
            androidx.activity.compose.BackHandler {
                activity?.finish()
            }

            GalleryScreen(
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route)
                },
                onArtistClick = { artistId ->
                    navController.navigate(Screen.Profile.createRoute(artistId))
                },
                onArtworkClick = { artworkId, artistId ->
                    navController.navigate(Screen.ArtworkDetail.createRoute(artworkId, artistId))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.UserProfile.route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }

        composable(Screen.Chat.route) {
            // BackHandler para Chat: ir a Gallery
            androidx.activity.compose.BackHandler {
                navController.navigate(Screen.Gallery.route) {
                    popUpTo(Screen.Gallery.route) { inclusive = true }
                }
            }

            ChatView(
                onNavigateToMessage = { chatId ->
                    navController.navigate(Screen.Message.createRoute(chatId))
                },
                onNavigateToGallery = {
                    navController.navigate(Screen.Gallery.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.UserProfile.route)
                }
            )
        }

        composable(
            route = Screen.Message.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            MessageView(
                chatId = chatId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Profile.route,
            arguments = listOf(
                navArgument("artistId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // BackHandler para Artist Profile: ir a Gallery
            androidx.activity.compose.BackHandler {
                navController.navigate(Screen.Gallery.route) {
                    popUpTo(Screen.Gallery.route) { inclusive = true }
                }
            }

            val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
            ArtistProfileScreen(
                artistId = artistId,
                onBackClick = {
                    navController.navigate(Screen.Gallery.route) {
                        popUpTo(Screen.Gallery.route) { inclusive = true }
                    }
                },
                onArtworkClick = { artworkId ->
                    navController.navigate(Screen.ArtworkDetail.createRoute(artworkId, artistId))
                },
                onNavigateToGallery = {
                    navController.navigate(Screen.Gallery.route) {
                        popUpTo(Screen.Gallery.route) { inclusive = true }
                    }
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Gallery.route)
                    }
                },
                onNavigateToUserProfile = {
                    navController.navigate(Screen.UserProfile.route) {
                        popUpTo(Screen.Gallery.route)
                    }
                }
            )
        }

        composable(
            route = Screen.ArtworkDetail.route,
            arguments = listOf(
                navArgument("artworkId") { type = NavType.StringType },
                navArgument("artistId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val artworkId = backStackEntry.arguments?.getString("artworkId") ?: ""
            val artistId = backStackEntry.arguments?.getString("artistId") ?: ""

            ArtworkDetailScreen(
                artworkId = artworkId,
                artistId = artistId,
                onBackClick = { navController.popBackStack() },
                onNavigateToArtistProfile = { artistId ->
                    navController.navigate(Screen.Profile.createRoute(artistId)) {
                        popUpTo(Screen.Gallery.route)
                    }
                },
                onNavigateToOwnProfile = {
                    navController.navigate(Screen.UserProfile.route) {
                        popUpTo(Screen.Gallery.route)
                    }
                },
                onNavigateToChatWithArtist = { chatId ->
                    navController.navigate(Screen.Message.createRoute(chatId))
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }

        composable(Screen.UserProfile.route) {
            // BackHandler para UserProfile: ir a Gallery
            androidx.activity.compose.BackHandler {
                navController.navigate(Screen.Gallery.route) {
                    popUpTo(Screen.Gallery.route) { inclusive = true }
                }
            }

            UserProfileScreen(
                onBackClick = {
                    // También manejar el botón de back del TopBar
                    navController.navigate(Screen.Gallery.route) {
                        popUpTo(Screen.Gallery.route) { inclusive = true }
                    }
                },
                onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToUpload = { navController.navigate(Screen.Upload.route) },
                onEditArtwork = { artworkId ->
                    navController.navigate(Screen.EditArtwork.createRoute(artworkId))
                },
                onNavigateToGallery = { navController.navigate(Screen.Gallery.route) },
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onLogout = onLogout,
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
                onPublishWork = { title, description, technique, imageUri, isForSale, price ->
                    // Agregar la obra al ViewModel
                    userProfileViewModel.addArtwork(title, description, technique, imageUri, isForSale, price)
                    // Regresar a UserProfile
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditArtwork.route,
            arguments = listOf(
                navArgument("artworkId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val artworkId = backStackEntry.arguments?.getString("artworkId") ?: ""
            val artwork = userProfileViewModel.getArtworkById(artworkId)

            if (artwork != null) {
                EditArtworkScreen(
                    artworkId = artworkId,
                    currentTitle = artwork.title,
                    currentDescription = artwork.description,
                    currentImageUri = artwork.imageUri,
                    currentIsForSale = artwork.isForSale,
                    currentPrice = artwork.price,
                    onBackClick = { navController.popBackStack() },
                    onSaveChanges = { id: String, title: String, description: String, imageUri: Uri?, isForSale: Boolean, price: String? ->
                        userProfileViewModel.updateArtwork(id, title, description, imageUri, isForSale, price)
                        navController.popBackStack()
                    }
                )
            }
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

        composable(Screen.Favorites.route) {
            androidx.activity.compose.BackHandler {
                navController.navigate(Screen.Gallery.route) {
                    popUpTo(Screen.Gallery.route) { inclusive = true }
                }
            }

            FavoritesScreen(
                onBackClick = {
                    navController.navigate(Screen.Gallery.route) {
                        popUpTo(Screen.Gallery.route) { inclusive = true }
                    }
                },
                onArtworkClick = { artworkId, artistId ->
                    navController.navigate(Screen.ArtworkDetail.createRoute(artworkId, artistId))
                },
                onNavigateToGallery = {
                    navController.navigate(Screen.Gallery.route) {
                        popUpTo(Screen.Gallery.route) { inclusive = true }
                    }
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Gallery.route)
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.UserProfile.route) {
                        popUpTo(Screen.Gallery.route)
                    }
                }
            )
        }
    }
}