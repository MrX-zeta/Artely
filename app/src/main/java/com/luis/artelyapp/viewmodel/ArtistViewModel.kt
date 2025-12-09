package com.luis.artelyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luis.artelyapp.model.Artist
import com.luis.artelyapp.model.ArtistStats
import com.luis.artelyapp.model.Artwork
import com.luis.artelyapp.repository.ArtistRepository
import com.luis.artelyapp.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados del ViewModel
 */
sealed class ArtistUiState {
    object Idle : ArtistUiState()
    object Loading : ArtistUiState()
    data class Success(
        val artist: Artist,
        val stats: ArtistStats,
        val artworks: List<Artwork>,
        val isOwnProfile: Boolean = false,
        val isFollowing: Boolean = false
    ) : ArtistUiState()
    data class Error(val message: String) : ArtistUiState()
}

class ArtistViewModel(
    private val repository: ArtistRepository = ArtistRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // Estado general de la UI
    private val _uiState = MutableStateFlow<ArtistUiState>(ArtistUiState.Idle)
    val uiState: StateFlow<ArtistUiState> = _uiState.asStateFlow()

    // Lista de artworks (para mantener compatibilidad)
    private val _artworks = MutableStateFlow<List<Artwork>>(emptyList())
    val artworks: StateFlow<List<Artwork>> = _artworks.asStateFlow()

    // Artista actual (para mantener compatibilidad)
    private val _currentArtist = MutableStateFlow<Artist?>(null)
    val currentArtist: StateFlow<Artist?> = _currentArtist.asStateFlow()

    // Estadísticas del artista
    private val _stats = MutableStateFlow<ArtistStats?>(null)
    val stats: StateFlow<ArtistStats?> = _stats.asStateFlow()

    /**
     * Carga los datos del artista desde Firebase
     */
    fun loadArtistData(artistId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ArtistUiState.Loading

                val artist = repository.getArtistById(artistId)

                if (artist == null) {
                    _uiState.value = ArtistUiState.Error("No se encontró el artista")
                    return@launch
                }

                _currentArtist.value = artist

                // Cargar stats y artworks en paralelo
                val stats = repository.getArtistStats(artistId)
                val artworks = repository.getArtworksByArtist(artistId)

                _stats.value = stats
                _artworks.value = artworks

                // Verificar si el perfil pertenece al usuario actual
                val currentUserId = authRepository.getCurrentUserId()
                val isOwnProfile = currentUserId == artistId

                // Verificar si el usuario actual sigue a este artista
                val isFollowing = if (currentUserId != null && !isOwnProfile) {
                    repository.isFollowing(currentUserId, artistId)
                } else {
                    false
                }

                _uiState.value = ArtistUiState.Success(
                    artist = artist,
                    stats = stats,
                    artworks = artworks,
                    isOwnProfile = isOwnProfile,
                    isFollowing = isFollowing
                )

            } catch (e: Exception) {
                _uiState.value = ArtistUiState.Error(
                    e.message ?: "Error desconocido al cargar el artista"
                )
            }
        }
    }

    /**
     * Carga los datos del artista por nombre de usuario
     */
    fun loadArtistByName(userName: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ArtistUiState.Loading

                val artist = repository.getArtistByUserName(userName)

                if (artist == null) {
                    _uiState.value = ArtistUiState.Error("No se encontró el artista '$userName'")
                    return@launch
                }

                loadArtistData(artist.id_User)

            } catch (e: Exception) {
                _uiState.value = ArtistUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Carga solo las obras del artista desde Firebase
     */
    fun loadArtworks(artistId: String) {
        viewModelScope.launch {
            try {
                val artworks = repository.getArtworksByArtist(artistId)
                _artworks.value = artworks
            } catch (e: Exception) {
                _uiState.value = ArtistUiState.Error(
                    e.message ?: "Error al cargar las obras"
                )
            }
        }
    }

    /**
     * Obtiene obras de galería (no en venta)
     */
    fun getGalleryArtworks(): List<Artwork> {
        return _artworks.value.filter { it.Status != "Available" || it.Price == 0.0 }
    }

    /**
     * Obtiene obras en venta
     */
    fun getForSaleArtworks(): List<Artwork> {
        return _artworks.value.filter { it.Status == "Available" && it.Price > 0 }
    }

    /**
     * Método según diagrama de clases
     * Sube una nueva obra de arte a Firebase
     */
    fun uploadArtwork(
        title: String,
        description: String,
        imageUrl: String,
        price: Double = 0.0,
        technique: String = "",
        status: String = "Available"
    ) {
        viewModelScope.launch {
            try {
                val artistId = _currentArtist.value?.id_User ?: return@launch

                val newArtwork = Artwork(
                    id_ArtWork = java.util.UUID.randomUUID().toString(),
                    id_Artist = artistId,
                    Title = title,
                    Description = description,
                    Price = price,
                    Technique = technique,
                    Status = status,
                    ImageUrl = imageUrl
                )

                // Guardar en Firebase
                val success = repository.saveArtwork(newArtwork)

                if (success) {
                    // Actualizar localmente
                    _artworks.value = _artworks.value + newArtwork

                    // Actualizar estadísticas
                    _stats.value?.let { currentStats ->
                        val updatedStats = currentStats.copy(
                            totalArtworks = currentStats.totalArtworks + 1
                        )
                        _stats.value = updatedStats
                        repository.updateArtistStats(artistId, updatedStats)
                    }
                }

            } catch (e: Exception) {
                _uiState.value = ArtistUiState.Error(
                    e.message ?: "Error al subir la obra"
                )
            }
        }
    }

    /**
     * Método según diagrama de clases
     * Edita una obra de arte existente
     */
    fun editArtwork(
        artworkId: String,
        title: String,
        description: String,
        price: Double,
        technique: String,
        status: String
    ) {
        viewModelScope.launch {
            try {
                val updatedArtwork = _artworks.value.find { it.id_ArtWork == artworkId }?.copy(
                    Title = title,
                    Description = description,
                    Price = price,
                    Technique = technique,
                    Status = status
                )

                if (updatedArtwork != null) {
                    // Actualizar en Firebase
                    val success = repository.updateArtwork(updatedArtwork)

                    if (success) {
                        // Actualizar localmente
                        _artworks.value = _artworks.value.map { artwork ->
                            if (artwork.id_ArtWork == artworkId) updatedArtwork else artwork
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.value = ArtistUiState.Error(
                    e.message ?: "Error al editar la obra"
                )
            }
        }
    }

    /**
     * Elimina una obra de arte
     */
    fun deleteArtwork(artworkId: String) {
        viewModelScope.launch {
            try {
                val artistId = _currentArtist.value?.id_User ?: return@launch

                // Eliminar de Firebase
                val success = repository.deleteArtwork(artworkId)

                if (success) {
                    // Actualizar localmente
                    _artworks.value = _artworks.value.filter { it.id_ArtWork != artworkId }

                    // Actualizar estadísticas
                    _stats.value?.let { currentStats ->
                        val updatedStats = currentStats.copy(
                            totalArtworks = maxOf(0, currentStats.totalArtworks - 1)
                        )
                        _stats.value = updatedStats
                        repository.updateArtistStats(artistId, updatedStats)
                    }
                }

            } catch (e: Exception) {
                _uiState.value = ArtistUiState.Error(
                    e.message ?: "Error al eliminar la obra"
                )
            }
        }
    }

    /**
     * Recarga todos los datos del artista actual
     */
    fun refresh() {
        _currentArtist.value?.let { artist ->
            loadArtistData(artist.id_User)
        }
    }

    /**
     * Sigue o deja de seguir a un artista
     */
    fun toggleFollow(artistId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = authRepository.getCurrentUserId()
                if (currentUserId == null) {
                    return@launch
                }

                val currentState = _uiState.value as? ArtistUiState.Success ?: return@launch
                val isCurrentlyFollowing = currentState.isFollowing

                val success = if (isCurrentlyFollowing) {
                    repository.unfollowArtist(currentUserId, artistId)
                } else {
                    repository.followArtist(currentUserId, artistId)
                }

                if (success) {
                    // Actualizar el estado local
                    val updatedStats = currentState.stats.copy(
                        followers = if (isCurrentlyFollowing) {
                            maxOf(0, currentState.stats.followers - 1)
                        } else {
                            currentState.stats.followers + 1
                        }
                    )

                    _uiState.value = currentState.copy(
                        isFollowing = !isCurrentlyFollowing,
                        stats = updatedStats
                    )
                }
            } catch (e: Exception) {
                // Log del error pero no cambiamos el estado
                android.util.Log.e("ArtistViewModel", "Error al cambiar seguimiento: ${e.message}")
            }
        }
    }
}
