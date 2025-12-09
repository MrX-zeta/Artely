package com.luis.artelyapp.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.luis.artelyapp.model.Artwork
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para operaciones con Firebase Realtime Database
 * relacionadas con obras de arte (Artworks)
 */
class ArtworkRepository {

    private val database = FirebaseDatabase.getInstance()
    private val artworksRef = database.getReference("artworks")
    private val artistRepository = ArtistRepository()

    companion object {
        private const val TAG = "ArtworkRepository"
    }

    /**
     * Método según diagrama de clases
     * Actualiza los detalles de una obra de arte en Firebase
     */
    suspend fun updateDetails(
        artworkId: Int,
        title: String,
        description: String,
        price: Double,
        technique: String
    ): Result<Artwork> {
        return try {
            // Obtener la obra actual de Firebase
            val snapshot = artworksRef.child(artworkId.toString()).get().await()
            val currentArtwork = snapshot.getValue(Artwork::class.java)

            if (currentArtwork == null) {
                return Result.failure(Exception("Obra de arte no encontrada"))
            }

            // Crear obra actualizada
            val updatedArtwork = currentArtwork.copy(
                Title = title,
                Description = description,
                Price = price,
                Technique = technique
            )

            // Guardar en Firebase
            artworksRef.child(artworkId.toString()).setValue(updatedArtwork).await()

            Log.d(TAG, "Detalles de obra $artworkId actualizados correctamente")
            Result.success(updatedArtwork)

        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar detalles de obra: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Método según diagrama de clases
     * Cambia el estado de una obra de arte
     * Estados posibles: "Available", "Sold", "Reserved", "Hidden", "Exhibition"
     */
    suspend fun changeStatus(artworkId: Int, newStatus: String): Result<Boolean> {
        return try {
            // Validar estado
            val validStatuses = listOf("Available", "Sold", "Reserved", "Hidden", "Exhibition")
            if (newStatus !in validStatuses) {
                return Result.failure(Exception("Estado inválido: $newStatus"))
            }

            // Actualizar solo el campo Status
            artworksRef.child(artworkId.toString())
                .child("Status")
                .setValue(newStatus)
                .await()

            Log.d(TAG, "Estado de obra $artworkId cambiado a: $newStatus")
            Result.success(true)

        } catch (e: Exception) {
            Log.e(TAG, "Error al cambiar estado de obra: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene una obra de arte por su ID desde Firebase
     */
    suspend fun getArtworkById(artworkId: Int): Result<Artwork> {
        return try {
            val snapshot = artworksRef.child(artworkId.toString()).get().await()
            val artwork = snapshot.getValue(Artwork::class.java)

            if (artwork != null) {
                Log.d(TAG, "Obra $artworkId obtenida correctamente")
                Result.success(artwork)
            } else {
                Log.w(TAG, "Obra $artworkId no encontrada")
                Result.failure(Exception("Obra de arte no encontrada"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener obra: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Crea una nueva obra de arte con imagen local
     */
    suspend fun createArtwork(
        artistId: String,
        title: String,
        description: String,
        imageUrl: String,
        price: Double,
        technique: String = "",
        status: String = "Available"
    ): Result<Artwork> {
        return try {
            // Generar ID único para la obra
            val artworkId = java.util.UUID.randomUUID().toString()

            val artwork = Artwork(
                id_ArtWork = artworkId,
                id_Artist = artistId,
                Title = title,
                Description = description,
                ImageUrl = imageUrl,
                Price = price,
                Technique = technique,
                Status = status
            )

            // Guardar en Firebase
            artworksRef.child(artworkId).setValue(artwork).await()

            Log.d(TAG, "✅ Obra creada exitosamente: $artworkId")
            Log.d(TAG, "📝 Título: $title")
            Log.d(TAG, "🎨 Artista: $artistId")
            Log.d(TAG, "🖼️ Imagen: $imageUrl")

            // Incrementar el contador de obras del artista
            val incrementSuccess = artistRepository.incrementArtworkCount(artistId)
            if (incrementSuccess) {
                Log.d(TAG, "📊 Contador de obras actualizado correctamente")
            } else {
                Log.w(TAG, "⚠️ No se pudo actualizar el contador de obras")
            }

            Result.success(artwork)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al crear obra: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las obras de arte desde Firebase
     */
    suspend fun getAllArtworks(): Result<List<Artwork>> {
        return try {
            val snapshot = artworksRef.get().await()
            val artworks = snapshot.children.mapNotNull {
                it.getValue(Artwork::class.java)
            }

            Log.d(TAG, "Se obtuvieron ${artworks.size} obras de arte")
            Result.success(artworks)

        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener todas las obras: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las obras de un artista específico desde Firebase
     */
    suspend fun getArtworksByArtist(artistId: String): Result<List<Artwork>> {
        return try {
            val snapshot = artworksRef
                .orderByChild("id_Artist")
                .equalTo(artistId)
                .get()
                .await()

            val artworks = snapshot.children.mapNotNull {
                it.getValue(Artwork::class.java)
            }

            Log.d(TAG, "Se obtuvieron ${artworks.size} obras del artista $artistId")
            Result.success(artworks)

        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener obras del artista: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Guarda o crea una nueva obra de arte en Firebase
     */
    suspend fun saveArtwork(artwork: Artwork): Result<Boolean> {
        return try {
            artworksRef.child(artwork.id_ArtWork.toString()).setValue(artwork).await()
            Log.d(TAG, "Obra ${artwork.id_ArtWork} guardada correctamente")
            Result.success(true)

        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar obra: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Elimina una obra de arte de Firebase
     */
    suspend fun deleteArtwork(artworkId: Int): Result<Boolean> {
        return try {
            artworksRef.child(artworkId.toString()).removeValue().await()
            Log.d(TAG, "Obra $artworkId eliminada correctamente")
            Result.success(true)

        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar obra: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene obras disponibles para la venta
     */
    suspend fun getAvailableArtworks(): Result<List<Artwork>> {
        return try {
            val snapshot = artworksRef
                .orderByChild("Status")
                .equalTo("Available")
                .get()
                .await()

            val artworks = snapshot.children.mapNotNull {
                it.getValue(Artwork::class.java)
            }.filter { it.Price > 0 }

            Log.d(TAG, "Se obtuvieron ${artworks.size} obras disponibles")
            Result.success(artworks)

        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener obras disponibles: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Busca obras por técnica
     */
    suspend fun getArtworksByTechnique(technique: String): Result<List<Artwork>> {
        return try {
            val snapshot = artworksRef
                .orderByChild("Technique")
                .equalTo(technique)
                .get()
                .await()

            val artworks = snapshot.children.mapNotNull {
                it.getValue(Artwork::class.java)
            }

            Log.d(TAG, "Se obtuvieron ${artworks.size} obras con técnica: $technique")
            Result.success(artworks)

        } catch (e: Exception) {
            Log.e(TAG, "Error al buscar obras por técnica: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Busca obras en un rango de precio
     */
    suspend fun getArtworksByPriceRange(minPrice: Double, maxPrice: Double): Result<List<Artwork>> {
        return try {
            val snapshot = artworksRef.get().await()
            val artworks = snapshot.children.mapNotNull {
                it.getValue(Artwork::class.java)
            }.filter { it.Price in minPrice..maxPrice }

            Log.d(TAG, "Se obtuvieron ${artworks.size} obras en rango de precio \$$minPrice - \$$maxPrice")
            Result.success(artworks)

        } catch (e: Exception) {
            Log.e(TAG, "Error al buscar obras por precio: ${e.message}")
            Result.failure(e)
        }
    }
}
