package com.luis.artelyapp.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luis.artelyapp.repository.ArtworkRepository
import com.luis.artelyapp.repository.AuthRepository
import com.luis.artelyapp.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados de la galería
 */
sealed class GalleryUiState {
    object Loading : GalleryUiState()
    data class Success(val artworks: List<com.luis.artelyapp.model.Artwork>) : GalleryUiState()
    object Empty : GalleryUiState()
    data class Error(val message: String) : GalleryUiState()
}

class GalleryViewModel(
    private val repository: ArtworkRepository = ArtworkRepository(),
    private val favoriteRepository: FavoriteRepository = FavoriteRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Loading)
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    private val _favoritesCount = MutableStateFlow(0)
    val favoritesCount: StateFlow<Int> = _favoritesCount.asStateFlow()

    init {
        loadArtworks()
        loadFavoritesCount()
    }

    /**
     * Carga el conteo de favoritos del usuario actual
     */
    private fun loadFavoritesCount() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                // Usar Flow para escuchar cambios en tiempo real
                favoriteRepository.getFavoritesByCustomer(userId).collect { favorites ->
                    _favoritesCount.value = favorites.size
                }
            }
        }
    }

    /**
     * Carga todas las obras desde Firebase
     */
    fun loadArtworks() {
        viewModelScope.launch {
            _uiState.value = GalleryUiState.Loading

            repository.getAllArtworks().fold(
                onSuccess = { artworks ->
                    _uiState.value = if (artworks.isEmpty()) {
                        GalleryUiState.Empty
                    } else {
                        GalleryUiState.Success(artworks)
                    }
                },
                onFailure = { error ->
                    _uiState.value = GalleryUiState.Error(
                        error.message ?: "Error al cargar las obras"
                    )
                }
            )
        }
    }

    /**
     * Recarga las obras
     */
    fun refresh() {
        loadArtworks()
    }

    fun setTab(index: Int) {
        _selectedTab.value = index
    }
}

