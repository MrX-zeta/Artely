package com.luis.artelyapp.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luis.artelyapp.model.Artwork
import com.luis.artelyapp.model.Favorite
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para operaciones con Firebase Realtime Database
 * relacionadas con favoritos
 */
class FavoriteRepository {

    private val database = FirebaseDatabase.getInstance()
    private val favoritesRef = database.getReference("favorites")
    private val artworksRef = database.getReference("artworks")

    companion object {
        private const val TAG = "FavoriteRepository"
    }

    /**
     * Agrega una obra a favoritos
     */
    suspend fun addFavorite(customerId: String, artworkId: String, artistId: String): Result<Favorite> {
        return try {
            val favoriteId = favoritesRef.push().key ?: return Result.failure(Exception("Error al generar ID"))

            val favorite = Favorite(
                id_Favorite = favoriteId,
                id_Customer = customerId,
                id_ArtWork = artworkId,
                id_Artist = artistId,
                timestamp = System.currentTimeMillis()
            )

            favoritesRef.child(favoriteId).setValue(favorite).await()
            Log.d(TAG, "Favorito agregado: $favoriteId")
            Result.success(favorite)
        } catch (e: Exception) {
            Log.e(TAG, "Error al agregar favorito: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Elimina una obra de favoritos
     */
    suspend fun removeFavorite(favoriteId: String): Result<Unit> {
        return try {
            favoritesRef.child(favoriteId).removeValue().await()
            Log.d(TAG, "Favorito eliminado: $favoriteId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar favorito: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Verifica si una obra está en favoritos de un customer
     */
    suspend fun isFavorite(customerId: String, artworkId: String): Result<String?> {
        return try {
            val snapshot = favoritesRef
                .orderByChild("id_Customer")
                .equalTo(customerId)
                .get()
                .await()

            var favoriteId: String? = null
            for (child in snapshot.children) {
                val favorite = child.getValue(Favorite::class.java)
                if (favorite?.id_ArtWork == artworkId) {
                    favoriteId = favorite.id_Favorite
                    break
                }
            }

            Result.success(favoriteId)
        } catch (e: Exception) {
            Log.e(TAG, "Error al verificar favorito: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los favoritos de un customer con sus obras completas
     */
    fun getFavoritesByCustomer(customerId: String): Flow<List<Pair<Favorite, Artwork?>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoritesList = mutableListOf<Pair<Favorite, Artwork?>>()

                for (child in snapshot.children) {
                    val favorite = child.getValue(Favorite::class.java)
                    if (favorite != null) {
                        // Por ahora agregamos el favorito sin la obra, la cargaremos después
                        favoritesList.add(Pair(favorite, null))
                    }
                }

                // Ahora cargamos las obras correspondientes
                if (favoritesList.isNotEmpty()) {
                    loadArtworksForFavorites(favoritesList) { completedList ->
                        trySend(completedList)
                    }
                } else {
                    trySend(emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error al obtener favoritos: ${error.message}")
                close(error.toException())
            }
        }

        favoritesRef
            .orderByChild("id_Customer")
            .equalTo(customerId)
            .addValueEventListener(listener)

        awaitClose {
            favoritesRef.removeEventListener(listener)
        }
    }

    /**
     * Carga las obras completas para una lista de favoritos
     */
    private fun loadArtworksForFavorites(
        favorites: List<Pair<Favorite, Artwork?>>,
        onComplete: (List<Pair<Favorite, Artwork?>>) -> Unit
    ) {
        val completedList = mutableListOf<Pair<Favorite, Artwork?>>()
        var loadedCount = 0

        favorites.forEach { (favorite, _) ->
            artworksRef.child(favorite.id_ArtWork).get().addOnSuccessListener { snapshot ->
                val artwork = snapshot.getValue(Artwork::class.java)
                completedList.add(Pair(favorite, artwork))
                loadedCount++

                if (loadedCount == favorites.size) {
                    // Ordenar por timestamp descendente (más recientes primero)
                    val sortedList = completedList.sortedByDescending { it.first.timestamp }
                    onComplete(sortedList)
                }
            }.addOnFailureListener { error ->
                Log.e(TAG, "Error al cargar obra ${favorite.id_ArtWork}: ${error.message}")
                completedList.add(Pair(favorite, null))
                loadedCount++

                if (loadedCount == favorites.size) {
                    val sortedList = completedList.sortedByDescending { it.first.timestamp }
                    onComplete(sortedList)
                }
            }
        }
    }

    /**
     * Obtiene el conteo de favoritos de un customer
     */
    suspend fun getFavoritesCount(customerId: String): Result<Int> {
        return try {
            val snapshot = favoritesRef
                .orderByChild("id_Customer")
                .equalTo(customerId)
                .get()
                .await()

            Result.success(snapshot.childrenCount.toInt())
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener conteo de favoritos: ${e.message}")
            Result.failure(e)
        }
    }
}

