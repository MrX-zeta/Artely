package com.luis.artelyapp.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.luis.artelyapp.model.Artist
import com.luis.artelyapp.model.ArtistStats
import com.luis.artelyapp.model.Artwork
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para operaciones con Firebase Realtime Database
 * relacionadas con artistas y sus obras
 */
class ArtistRepository {

    private val database = FirebaseDatabase.getInstance()
    private val artistsRef = database.getReference("artists")
    private val artworksRef = database.getReference("artworks")
    private val statsRef = database.getReference("stats")

    companion object {
        private const val TAG = "ArtistRepository"
    }

    /**
     * Obtiene los datos de un artista por su ID
     */
    suspend fun getArtistById(artistId: String): Artist? {
        return try {
            val snapshot = artistsRef.child(artistId).get().await()
            snapshot.getValue(Artist::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener artista $artistId: ${e.message}")
            null
        }
    }

    /**
     * Obtiene los datos de un artista por su UID de Firebase
     */
    suspend fun getArtistByUID(uid: String): Artist? {
        return try {
            val snapshot = artistsRef.child(uid).get().await()
            snapshot.getValue(Artist::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener artista por UID $uid: ${e.message}")
            null
        }
    }

    /**
     * Busca un artista por email
     */
    suspend fun getArtistByEmail(email: String): Artist? {
        return try {
            val snapshot = artistsRef
                .orderByChild("email")
                .equalTo(email)
                .limitToFirst(1)
                .get()
                .await()

            snapshot.children.firstOrNull()?.getValue(Artist::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener artista por email $email: ${e.message}")
            null
        }
    }

    /**
     * Método de debug: Lista todos los artistas en Firebase para debugging
     */
    suspend fun debugListAllArtists(): Map<String, Artist> {
        return try {
            val snapshot = artistsRef.get().await()
            val artists = mutableMapOf<String, Artist>()

            for (child in snapshot.children) {
                val artist = child.getValue(Artist::class.java)
                if (artist != null) {
                    artists[child.key ?: "unknown"] = artist
                    Log.d(TAG, "🐛 DEBUG - Artista encontrado: key=${child.key}, name=${artist.UserName}, email=${artist.Email}")
                }
            }

            Log.d(TAG, "🐛 DEBUG - Total artistas encontrados: ${artists.size}")
            artists
        } catch (e: Exception) {
            Log.e(TAG, "Error al listar artistas: ${e.message}")
            emptyMap()
        }
    }

    /**
     * Obtiene los datos de un artista por su nombre de usuario
     */
    suspend fun getArtistByUserName(userName: String): Artist? {
        return try {
            val snapshot = artistsRef
                .orderByChild("userName")
                .equalTo(userName)
                .limitToFirst(1)
                .get()
                .await()

            snapshot.children.firstOrNull()?.getValue(Artist::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener artista por nombre $userName: ${e.message}")
            null
        }
    }

    /**
     * Obtiene las estadísticas de un artista
     */
    suspend fun getArtistStats(artistId: String): ArtistStats {
        return try {
            val snapshot = statsRef.child(artistId).get().await()
            snapshot.getValue(ArtistStats::class.java) ?: ArtistStats(
                totalArtworks = 0,
                followers = 0,
                following = 0,
                likes = 0
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener estadísticas: ${e.message}")
            ArtistStats(totalArtworks = 0, followers = 0, following = 0, likes = 0)
        }
    }

    /**
     * Obtiene las obras de un artista
     */
    suspend fun getArtworksByArtist(artistId: String): List<Artwork> {
        return try {
            val snapshot = artworksRef
                .orderByChild("id_Artist")
                .equalTo(artistId)
                .get()
                .await()

            snapshot.children.mapNotNull { it.getValue(Artwork::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener obras: ${e.message}")
            emptyList()
        }
    }

    /**
     * Guarda o actualiza una obra de arte
     */
    suspend fun saveArtwork(artwork: Artwork): Boolean {
        return try {
            artworksRef.child(artwork.id_ArtWork).setValue(artwork).await()
            Log.d(TAG, "Obra guardada correctamente")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar obra: ${e.message}")
            false
        }
    }

    /**
     * Actualiza una obra de arte existente
     */
    suspend fun updateArtwork(artwork: Artwork): Boolean {
        return saveArtwork(artwork)
    }

    /**
     * Elimina una obra de arte
     */
    suspend fun deleteArtwork(artworkId: String): Boolean {
        return try {
            artworksRef.child(artworkId).removeValue().await()
            Log.d(TAG, "Obra eliminada correctamente")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar obra: ${e.message}")
            false
        }
    }

    /**
     * Actualiza las estadísticas de un artista
     */
    suspend fun updateArtistStats(artistId: String, stats: ArtistStats): Boolean {
        return try {
            statsRef.child(artistId).setValue(stats).await()
            Log.d(TAG, "Estadísticas actualizadas")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estadísticas: ${e.message}")
            false
        }
    }

    /**
     * Incrementa el contador de obras del artista
     */
    suspend fun incrementArtworkCount(artistId: String): Boolean {
        return try {
            // Obtener estadísticas actuales
            val currentStats = getArtistStats(artistId)

            // Incrementar el contador de obras
            val updatedStats = currentStats.copy(
                totalArtworks = currentStats.totalArtworks + 1
            )

            // Guardar las estadísticas actualizadas
            statsRef.child(artistId).setValue(updatedStats).await()
            Log.d(TAG, "✅ Contador de obras incrementado para artista $artistId: ${updatedStats.totalArtworks}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al incrementar contador de obras: ${e.message}")
            false
        }
    }

    /**
     * Decrementa el contador de obras del artista
     */
    suspend fun decrementArtworkCount(artistId: String): Boolean {
        return try {
            // Obtener estadísticas actuales
            val currentStats = getArtistStats(artistId)

            // Decrementar el contador de obras (asegurar que no sea negativo)
            val updatedStats = currentStats.copy(
                totalArtworks = maxOf(0, currentStats.totalArtworks - 1)
            )

            // Guardar las estadísticas actualizadas
            statsRef.child(artistId).setValue(updatedStats).await()
            Log.d(TAG, "✅ Contador de obras decrementado para artista $artistId: ${updatedStats.totalArtworks}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al decrementar contador de obras: ${e.message}")
            false
        }
    }

    /**
     * Verifica si un usuario sigue a un artista
     */
    suspend fun isFollowing(userId: String, artistId: String): Boolean {
        return try {
            val followsRef = database.getReference("follows")
            val snapshot = followsRef.child(userId).child(artistId).get().await()
            snapshot.exists()
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al verificar seguimiento: ${e.message}")
            false
        }
    }

    /**
     * Sigue a un artista
     */
    suspend fun followArtist(userId: String, artistId: String): Boolean {
        return try {
            val followsRef = database.getReference("follows")

            // Guardar la relación de seguimiento
            followsRef.child(userId).child(artistId).setValue(true).await()

            // Incrementar contador de seguidores del artista
            val currentStats = getArtistStats(artistId)
            val updatedStats = currentStats.copy(
                followers = currentStats.followers + 1
            )
            statsRef.child(artistId).setValue(updatedStats).await()

            Log.d(TAG, "✅ Usuario $userId ahora sigue a artista $artistId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al seguir artista: ${e.message}")
            false
        }
    }

    /**
     * Deja de seguir a un artista
     */
    suspend fun unfollowArtist(userId: String, artistId: String): Boolean {
        return try {
            val followsRef = database.getReference("follows")

            // Eliminar la relación de seguimiento
            followsRef.child(userId).child(artistId).removeValue().await()

            // Decrementar contador de seguidores del artista
            val currentStats = getArtistStats(artistId)
            val updatedStats = currentStats.copy(
                followers = maxOf(0, currentStats.followers - 1)
            )
            statsRef.child(artistId).setValue(updatedStats).await()

            Log.d(TAG, "✅ Usuario $userId dejó de seguir a artista $artistId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al dejar de seguir artista: ${e.message}")
            false
        }
    }
}

