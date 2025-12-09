package com.luis.artelyapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.luis.artelyapp.model.Artist
import com.luis.artelyapp.model.Customer
import kotlinx.coroutines.tasks.await

/**
 * Repository para manejar la autenticación de usuarios
 * Capa de datos - Maneja Firebase Auth y Realtime Database
 */
class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    companion object {
        private const val TAG = "AuthRepository"
    }

    /**
     * Obtiene el usuario actual autenticado
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    /**
     * Verifica si hay un usuario autenticado
     */
    fun isUserLoggedIn(): Boolean = getCurrentUser() != null

    /**
     * Obtiene el ID del usuario actual (String - UID de Firebase)
     */
    fun getCurrentUserId(): String? {
        return getCurrentUser()?.uid
    }

    /**
     * Obtiene el UID de Firebase del usuario actual (String) - mismo que getCurrentUserId()
     * @deprecated Usar getCurrentUserId() en su lugar
     */
    @Deprecated("Usar getCurrentUserId() en su lugar")
    fun getCurrentUserUID(): String? {
        return getCurrentUser()?.uid
    }

    /**
     * Obtiene el rol del usuario actual desde la base de datos
     * @return "Artist" o "Customer", null si no se encuentra
     */
    suspend fun getCurrentUserRole(): Result<String?> {
        return try {
            val userId = getCurrentUserId()

            if (userId == null) {
                return Result.success(null)
            }

            // Buscar en artists usando UID como clave
            val artistSnapshot = database.getReference("artists")
                .child(userId)
                .get()
                .await()

            if (artistSnapshot.exists()) {
                val role = artistSnapshot.child("role").getValue(String::class.java)
                return Result.success(role ?: "Artist")
            }

            // Buscar en customers usando UID como clave
            val customerSnapshot = database.getReference("customers")
                .child(userId)
                .get()
                .await()

            if (customerSnapshot.exists()) {
                val role = customerSnapshot.child("role").getValue(String::class.java)
                return Result.success(role ?: "Customer")
            }

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra un nuevo usuario con email y contraseña
     */
    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Error al crear usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Guarda los datos del artista en la base de datos
     * Nota: La contraseña NO se guarda aquí, Firebase Auth la maneja de forma segura
     */
    suspend fun saveArtistProfile(userId: String, username: String, email: String): Result<Unit> {
        return try {
            val artist = Artist(
                id_User = userId,
                UserName = username,
                Email = email,
                Role = "Artist",
                Bio = "",
                Location = ""
            )

            database.getReference("artists")
                .child(userId)
                .setValue(artist)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Guarda los datos del seguidor en la base de datos
     * Nota: La contraseña NO se guarda aquí, Firebase Auth la maneja de forma segura
     */
    suspend fun saveCustomerProfile(userId: String, username: String, email: String): Result<Unit> {
        return try {
            val customer = Customer(
                id_User = userId,
                UserName = username,
                Email = email,
                Role = "Customer"
            )

            database.getReference("customers")
                .child(userId)
                .setValue(customer)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra sesión del usuario actual
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Verifica si existen usuarios registrados en el sistema
     * Revisa tanto en la colección de artistas como de customers
     */
    suspend fun hasRegisteredUsers(): Boolean {
        return try {
            android.util.Log.d(TAG, "🔍 Verificando si existen usuarios registrados...")
            val artistsRef = database.getReference("artists")
            val customersRef = database.getReference("customers")

            android.util.Log.d(TAG, "📡 Consultando artistas...")
            val artistsSnapshot = artistsRef.limitToFirst(1).get().await()
            android.util.Log.d(TAG, "👨‍🎨 Artistas encontrados: ${artistsSnapshot.exists()}, count: ${artistsSnapshot.childrenCount}")

            android.util.Log.d(TAG, "📡 Consultando customers...")
            val customersSnapshot = customersRef.limitToFirst(1).get().await()
            android.util.Log.d(TAG, "👥 Customers encontrados: ${customersSnapshot.exists()}, count: ${customersSnapshot.childrenCount}")

            val hasUsers = artistsSnapshot.exists() || customersSnapshot.exists()
            android.util.Log.d(TAG, "✅ Resultado final - ¿Existen usuarios registrados? $hasUsers")
            hasUsers
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Error al verificar usuarios registrados: ${e.message}", e)
            // En caso de error, asumimos que NO hay usuarios (comportamiento seguro para primera instalación)
            false
        }
    }

    /**
     * Envía un email de recuperación de contraseña
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina el usuario actual de Firebase Authentication
     * IMPORTANTE: El usuario debe estar autenticado para eliminarse a sí mismo
     */
    suspend fun deleteCurrentUser(): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                user.delete().await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("No hay usuario autenticado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el perfil de un artista (nombre, bio, ubicación y foto)
     */
    suspend fun updateArtistProfile(email: String, newName: String, newBio: String, newLocation: String, profileImageUrl: String? = null): Result<Unit> {
        return try {
            android.util.Log.d(TAG, "🔄 Actualizando perfil de artista con email: $email")
            android.util.Log.d(TAG, "📝 Nuevos valores - Nombre: $newName, Bio: $newBio, Ubicación: $newLocation")
            android.util.Log.d(TAG, "🖼️ Foto de perfil: $profileImageUrl")

            // Buscar el artista por email
            val artistsRef = database.getReference("artists")
            val snapshot = artistsRef
                .orderByChild("email")
                .equalTo(email)
                .limitToFirst(1)
                .get()
                .await()

            android.util.Log.d(TAG, "📊 Snapshot obtenido, children count: ${snapshot.childrenCount}")

            val artistKey = snapshot.children.firstOrNull()?.key
            if (artistKey == null) {
                android.util.Log.e(TAG, "❌ No se encontró el artista con email: $email")
                return Result.failure(Exception("No se encontró el artista con email: $email"))
            }

            android.util.Log.d(TAG, "🔑 Artist key encontrada: $artistKey")

            // Actualizar los campos incluyendo la foto si se proporciona
            val updates = mutableMapOf<String, Any>(
                "userName" to newName,
                "bio" to newBio,
                "location" to newLocation
            )
            if (profileImageUrl != null) {
                updates["profileImageUrl"] = profileImageUrl
            }

            android.util.Log.d(TAG, "📤 Enviando actualización a Firebase...")
            artistsRef.child(artistKey).updateChildren(updates).await()
            android.util.Log.d(TAG, "✅ Perfil actualizado exitosamente en Firebase")

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Error al actualizar perfil de artista: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Actualiza el perfil de un customer (nombre y foto)
     */
    suspend fun updateCustomerProfile(email: String, newName: String, profileImageUrl: String? = null): Result<Unit> {
        return try {
            android.util.Log.d(TAG, "🔄 Actualizando perfil de customer con email: $email")
            android.util.Log.d(TAG, "📝 Nuevo nombre: $newName")
            android.util.Log.d(TAG, "🖼️ Foto de perfil: $profileImageUrl")

            // Buscar el customer por email
            val customersRef = database.getReference("customers")
            val snapshot = customersRef
                .orderByChild("email")
                .equalTo(email)
                .limitToFirst(1)
                .get()
                .await()

            android.util.Log.d(TAG, "📊 Snapshot obtenido, children count: ${snapshot.childrenCount}")

            val customerKey = snapshot.children.firstOrNull()?.key
            if (customerKey == null) {
                android.util.Log.e(TAG, "❌ No se encontró el customer con email: $email")
                return Result.failure(Exception("No se encontró el customer con email: $email"))
            }

            android.util.Log.d(TAG, "🔑 Customer key encontrada: $customerKey")

            // Actualizar el nombre y la foto de perfil si se proporciona
            val updates = mutableMapOf<String, Any>("userName" to newName)
            if (profileImageUrl != null) {
                updates["profileImageUrl"] = profileImageUrl
            }

            android.util.Log.d(TAG, "📤 Enviando actualización a Firebase...")
            customersRef.child(customerKey).updateChildren(updates).await()
            android.util.Log.d(TAG, "✅ Perfil actualizado exitosamente en Firebase")

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Error al actualizar perfil de customer: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Elimina el perfil del usuario de Realtime Database (Artista o Cliente)
     * @param userId El UID de Firebase del usuario
     * @param isArtist true si es artista, false si es cliente
     */
    suspend fun deleteUserProfile(userId: String, isArtist: Boolean): Result<Unit> {
        return try {
            val reference = if (isArtist) {
                database.getReference("artists").child(userId)
            } else {
                database.getReference("customers").child(userId)
            }

            reference.removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina completamente un usuario (Authentication + Database)
     * El usuario debe estar autenticado
     * @param isArtist true si es artista, false si es cliente
     */
    suspend fun deleteUserAccount(isArtist: Boolean): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user == null) {
                return Result.failure(Exception("No hay usuario autenticado"))
            }

            // 1. Primero eliminar de Database
            val deleteProfileResult = deleteUserProfile(user.uid, isArtist)
            if (deleteProfileResult.isFailure) {
                return deleteProfileResult
            }

            // 2. Después eliminar de Authentication
            user.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

