package com.luis.artelyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luis.artelyapp.model.Artwork
import com.luis.artelyapp.model.Favorite
import com.luis.artelyapp.repository.AuthRepository
import com.luis.artelyapp.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados UI para la pantalla de favoritos
 */
sealed class FavoriteUiState {
    object Loading : FavoriteUiState()
    data class Success(val favorites: List<Pair<Favorite, Artwork?>>) : FavoriteUiState()
    data class Error(val message: String) : FavoriteUiState()
    object Empty : FavoriteUiState()
}

/**
 * ViewModel para gestionar favoritos siguiendo MVVM
 */
class FavoriteViewModel : ViewModel() {

    private val favoriteRepository = FavoriteRepository()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow<FavoriteUiState>(FavoriteUiState.Loading)
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _currentFavoriteId = MutableStateFlow<String?>(null)

    companion object {
        private const val TAG = "FavoriteViewModel"
    }

    /**
     * Carga los favoritos del usuario actual
     */
    fun loadFavorites() {
        val customerId = authRepository.getCurrentUserId()
        if (customerId == null) {
            _uiState.value = FavoriteUiState.Error("Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            _uiState.value = FavoriteUiState.Loading
            try {
                favoriteRepository.getFavoritesByCustomer(customerId).collect { favorites ->
                    if (favorites.isEmpty()) {
                        _uiState.value = FavoriteUiState.Empty
                    } else {
                        _uiState.value = FavoriteUiState.Success(favorites)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar favoritos: ${e.message}")
                _uiState.value = FavoriteUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Verifica si una obra está en favoritos
     */
    fun checkIsFavorite(artworkId: String) {
        val customerId = authRepository.getCurrentUserId() ?: return

        viewModelScope.launch {
            val result = favoriteRepository.isFavorite(customerId, artworkId)
            result.fold(
                onSuccess = { favoriteId ->
                    _isFavorite.value = favoriteId != null
                    _currentFavoriteId.value = favoriteId
                },
                onFailure = { error ->
                    Log.e(TAG, "Error al verificar favorito: ${error.message}")
                    _isFavorite.value = false
                }
            )
        }
    }

    /**
     * Agrega una obra a favoritos
     */
    fun addToFavorites(artworkId: String, artistId: String, onSuccess: () -> Unit = {}) {
        val customerId = authRepository.getCurrentUserId()
        if (customerId == null) {
            Log.e(TAG, "Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            val result = favoriteRepository.addFavorite(customerId, artworkId, artistId)
            result.fold(
                onSuccess = { favorite ->
                    _isFavorite.value = true
                    _currentFavoriteId.value = favorite.id_Favorite
                    Log.d(TAG, "Obra agregada a favoritos exitosamente")
                    onSuccess()
                },
                onFailure = { error ->
                    Log.e(TAG, "Error al agregar a favoritos: ${error.message}")
                }
            )
        }
    }

    /**
     * Elimina una obra de favoritos
     */
    fun removeFromFavorites(favoriteId: String? = null, onSuccess: () -> Unit = {}) {
        val idToRemove = favoriteId ?: _currentFavoriteId.value
        if (idToRemove == null) {
            Log.e(TAG, "No hay ID de favorito para eliminar")
            return
        }

        viewModelScope.launch {
            val result = favoriteRepository.removeFavorite(idToRemove)
            result.fold(
                onSuccess = {
                    _isFavorite.value = false
                    _currentFavoriteId.value = null
                    Log.d(TAG, "Obra eliminada de favoritos exitosamente")
                    onSuccess()
                },
                onFailure = { error ->
                    Log.e(TAG, "Error al eliminar de favoritos: ${error.message}")
                }
            )
        }
    }

    /**
     * Alterna el estado de favorito de una obra
     */
    fun toggleFavorite(artworkId: String, artistId: String, onAdded: () -> Unit = {}) {
        if (_isFavorite.value) {
            removeFromFavorites()
        } else {
            addToFavorites(artworkId, artistId, onAdded)
        }
    }
}

