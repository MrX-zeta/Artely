package com.luis.artelyapp.view.ArtistProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Clase local para el artista (ya que eliminaste el modelo)
data class ArtistData(
    val id: Int,
    val email: String,
    val bio: String,
    val location: String,
    val role: String = "Artist"
)

// Clase local para las imágenes/obras de arte
data class ImageData(
    val id: Int,
    val artistId: Int,
    val status: String,
    val description: String,
    val url: String,
    val title: String = "",
    val price: String? = null
)

data class ProfileUiState(
    val isLoading: Boolean = false,
    val artist: ArtistData? = null,
    val artworks: List<ImageData> = emptyList(),
    val artworksForSale: List<ImageData> = emptyList(),
    val collections: List<ArtCollection> = emptyList(),
    val stats: ArtistStatsViewModel = ArtistStatsViewModel(),
    val selectedTab: ProfileTabViewModel = ProfileTabViewModel.GALLERY,
    val isFollowing: Boolean = false,
    val error: String? = null
)

// Renombro las clases para evitar duplicación con Profile.kt
data class ArtistStatsViewModel(
    val totalArtworks: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val likes: Int = 0
)

data class ArtCollection(
    val id: Int,
    val name: String,
    val description: String,
    val artworks: List<ImageData>,
    val thumbnailUrl: String?
)

enum class ProfileTabViewModel {
    GALLERY,
    FOR_SALE
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadArtistProfile()
    }

    fun loadArtistProfile(artistId: Int? = null) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Simular carga de datos del artista
                val artist = loadArtistData(artistId)
                val artworks = loadArtistArtworks(artistId)
                val stats = loadArtistStats(artistId)
                val collections = loadArtistCollections(artistId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    artist = artist,
                    artworks = artworks.filter { it.status != "for_sale" },
                    artworksForSale = artworks.filter { it.status == "for_sale" },
                    collections = collections,
                    stats = stats
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido al cargar el perfil"
                )
            }
        }
    }

    fun selectTab(tab: ProfileTabViewModel) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun toggleFollow() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val newFollowState = !currentState.isFollowing

                // Simular API call para seguir/dejar de seguir
                _uiState.value = currentState.copy(
                    isFollowing = newFollowState,
                    stats = currentState.stats.copy(
                        followers = if (newFollowState)
                            currentState.stats.followers + 1
                        else
                            currentState.stats.followers - 1
                    )
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al actualizar estado de seguimiento"
                )
            }
        }
    }

    fun likeArtwork(imageId: Int) {
        viewModelScope.launch {
            try {
                // Simular like/unlike de obra de arte
                // Aquí iría la llamada a la API

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al dar like a la obra"
                )
            }
        }
    }

    fun shareProfile() {
        // Funcionalidad para compartir perfil
        // Aquí iría la lógica de compartir
    }

    fun reportProfile() {
        // Funcionalidad para reportar perfil
        // Aquí iría la lógica de reporte
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshProfile() {
        val currentArtistId = _uiState.value.artist?.id
        loadArtistProfile(currentArtistId)
    }

    // Funciones simuladas para cargar datos
    private fun loadArtistData(artistId: Int?): ArtistData {
        // Simular datos del artista (Aristote del diseño)
        return ArtistData(
            id = artistId ?: 1,
            email = "aristote@artely.com",
            bio = "Artista visual especializado en retratos contemporáneos y arte figurativo",
            location = "París, Francia",
            role = "Artist"
        )
    }

    private fun loadArtistArtworks(artistId: Int?): List<ImageData> {
        // Simular obras de arte
        return listOf(
            ImageData(
                id = 1,
                artistId = artistId ?: 1,
                status = "public",
                description = "La Gioconda - Leonardo da Vinci",
                url = "https://example.com/gioconda.jpg",
                title = "La Gioconda"
            ),
            ImageData(
                id = 2,
                artistId = artistId ?: 1,
                status = "public",
                description = "Retrato Contemporáneo",
                url = "https://example.com/retrato.jpg",
                title = "Retrato Contemporáneo"
            ),
            ImageData(
                id = 3,
                artistId = artistId ?: 1,
                status = "for_sale",
                description = "Reflejos",
                url = "https://example.com/reflejos.jpg",
                title = "Reflejos",
                price = "$2,100"
            )
        )
    }

    private fun loadArtistStats(@Suppress("UNUSED_PARAMETER") artistId: Int?): ArtistStatsViewModel {
        // Simular estadísticas del artista
        return ArtistStatsViewModel(
            totalArtworks = 127,
            followers = 2300,
            following = 156,
            likes = 8945
        )
    }

    private fun loadArtistCollections(@Suppress("UNUSED_PARAMETER") artistId: Int?): List<ArtCollection> {
        // Simular colecciones del artista
        return listOf(
            ArtCollection(
                id = 1,
                name = "Retratos Contemporáneos",
                description = "Una colección de retratos modernos",
                artworks = emptyList(),
                thumbnailUrl = "https://example.com/collection1.jpg"
            ),
            ArtCollection(
                id = 2,
                name = "Arte Figurativo",
                description = "Obras de arte figurativo clásico",
                artworks = emptyList(),
                thumbnailUrl = "https://example.com/collection2.jpg"
            )
        )
    }
}
