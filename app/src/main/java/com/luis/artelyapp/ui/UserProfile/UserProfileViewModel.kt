package com.luis.artelyapp.ui.UserProfile

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfileInfo(
    val name: String = "Aristote",
    val bio: String = "Artista visual especializado en retratos contemporáneos y arte figurativo",
    val location: String = "París, Francia",
    val profileImageUri: Uri? = null
)

class UserProfileViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow(UserProfileInfo())
    val userProfile: StateFlow<UserProfileInfo> = _userProfile.asStateFlow()

    private val _userArtworks = MutableStateFlow<List<UserArtwork>>(
        // Agregar algunas obras de ejemplo para probar
        listOf(

            UserArtwork(
                id = 1001,
                title = "Noche Estrellada\nSobre el Ródano",
                artist = "Vincent van Gogh",
                description = "Paisaje nocturno impresionista",
                imageUri = null,
                price = "$2,500",
                isForSale = true
            )
        )
    )
    val userArtworks: StateFlow<List<UserArtwork>> = _userArtworks.asStateFlow()

    private var nextId = 2000 // Empezar desde 2000 para evitar conflictos

    fun addArtwork(title: String, description: String, imageUri: Uri?) {
        val newArtwork = UserArtwork(
            id = nextId++,
            title = title,
            artist = "Aristote", // Por ahora el usuario actual
            description = description,
            imageUri = imageUri,
            isForSale = false
        )

        _userArtworks.value = _userArtworks.value + newArtwork
    }

    fun removeArtwork(artworkId: Int) {
        _userArtworks.value = _userArtworks.value.filter { it.id != artworkId }
    }

    fun toggleForSale(artworkId: Int, price: String? = null) {
        _userArtworks.value = _userArtworks.value.map { artwork ->
            if (artwork.id == artworkId) {
                artwork.copy(isForSale = !artwork.isForSale, price = price)
            } else {
                artwork
            }
        }
    }

    fun updateProfile(name: String, bio: String, location: String, profileImageUri: Uri?) {
        _userProfile.value = UserProfileInfo(
            name = name,
            bio = bio,
            location = location,
            profileImageUri = profileImageUri
        )
    }
}
