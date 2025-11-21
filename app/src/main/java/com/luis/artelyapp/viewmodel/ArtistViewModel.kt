package com.luis.artelyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.luis.artelyapp.model.Artist
import com.luis.artelyapp.model.Artwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArtistViewModel : ViewModel() {
    private val _artworks = MutableStateFlow<List<Artwork>>(emptyList())
    val artworks: StateFlow<List<Artwork>> = _artworks.asStateFlow()

    private val _currentArtist = MutableStateFlow<Artist?>(null)
    val currentArtist: StateFlow<Artist?> = _currentArtist.asStateFlow()

    // Método según diagrama de clases
    fun uploadArtwork(
        title: String,
        description: String,
        imageUrl: String,
        price: Double = 0.0,
        technique: String = "",
        status: String = "Available"
    ) {
        // TODO: Implementar lógica de subida de artwork
        val newArtwork = Artwork(
            id_ArtWork = _artworks.value.size + 1,
            id_Artist = _currentArtist.value?.id_User ?: 0,
            Title = title,
            Description = description,
            Price = price,
            Technique = technique,
            Status = status,
            ImageUrl = imageUrl
        )
        _artworks.value = _artworks.value + newArtwork
    }

    // Método según diagrama de clases
    fun editArtwork(
        artworkId: Int,
        title: String,
        description: String,
        price: Double,
        technique: String,
        status: String
    ) {
        // TODO: Implementar lógica de edición de artwork
        _artworks.value = _artworks.value.map { artwork ->
            if (artwork.id_ArtWork == artworkId) {
                artwork.copy(
                    Title = title,
                    Description = description,
                    Price = price,
                    Technique = technique,
                    Status = status
                )
            } else {
                artwork
            }
        }
    }

    fun loadArtistData(artistId: Int) {
        // TODO: Cargar datos del artista desde repository
    }

    fun loadArtworks(artistId: Int) {
        // TODO: Cargar artworks del artista desde repository
    }
}
