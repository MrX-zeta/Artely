package com.luis.artelyapp.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Repositorio para manejar el almacenamiento local de imágenes
 * Las imágenes se guardan en el directorio interno de la app
 */
class ImageRepository(private val context: Context) {

    companion object {
        private const val TAG = "ImageRepository"
        private const val ARTWORKS_DIR = "artworks"
        private const val PROFILES_DIR = "profiles"
        private const val IMAGE_QUALITY = 85 // Calidad de compresión JPEG (0-100)
        private const val MAX_IMAGE_SIZE = 1920 // Tamaño máximo en píxeles
    }

    /**
     * Obtiene o crea el directorio para obras de arte
     */
    private fun getArtworksDir(): File {
        val dir = File(context.filesDir, ARTWORKS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
            Log.d(TAG, "📁 Directorio de obras creado: ${dir.absolutePath}")
        }
        return dir
    }

    /**
     * Obtiene o crea el directorio para perfiles
     */
    private fun getProfilesDir(): File {
        val dir = File(context.filesDir, PROFILES_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
            Log.d(TAG, "📁 Directorio de perfiles creado: ${dir.absolutePath}")
        }
        return dir
    }

    /**
     * Guarda una imagen de obra de arte localmente
     * @param imageUri URI de la imagen original
     * @param artistId ID del artista
     * @return Ruta local de la imagen guardada
     */
    suspend fun uploadArtworkImage(imageUri: Uri, artistId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "📤 Guardando imagen de obra...")

                // Generar nombre único para la imagen
                val fileName = "artwork_${artistId}_${UUID.randomUUID()}.jpg"
                val destinationFile = File(getArtworksDir(), fileName)

                // Copiar y comprimir la imagen
                val success = copyAndCompressImage(imageUri, destinationFile)

                if (success) {
                    val imagePath = destinationFile.absolutePath
                    Log.d(TAG, "✅ Imagen guardada exitosamente")
                    Log.d(TAG, "📍 Ruta: $imagePath")
                    Result.success(imagePath)
                } else {
                    Log.e(TAG, "❌ Error al guardar imagen")
                    Result.failure(Exception("Error al guardar imagen"))
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al guardar imagen de obra: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Guarda una imagen de perfil localmente
     * @param imageUri URI de la imagen original
     * @param userId ID del usuario
     * @return Ruta local de la imagen guardada
     */
    suspend fun uploadProfileImage(imageUri: Uri, userId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "📤 Guardando imagen de perfil...")

                val fileName = "profile_${userId}.jpg"
                val destinationFile = File(getProfilesDir(), fileName)

                // Si ya existe, eliminarla primero
                if (destinationFile.exists()) {
                    destinationFile.delete()
                    Log.d(TAG, "🗑️ Imagen de perfil anterior eliminada")
                }

                val success = copyAndCompressImage(imageUri, destinationFile)

                if (success) {
                    val imagePath = destinationFile.absolutePath
                    Log.d(TAG, "✅ Imagen de perfil guardada exitosamente")
                    Log.d(TAG, "📍 Ruta: $imagePath")
                    Result.success(imagePath)
                } else {
                    Log.e(TAG, "❌ Error al guardar imagen de perfil")
                    Result.failure(Exception("Error al guardar imagen de perfil"))
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al guardar imagen de perfil: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Copia y comprime una imagen desde URI a un archivo
     */
    private fun copyAndCompressImage(sourceUri: Uri, destinationFile: File): Boolean {
        return try {
            // Abrir stream de entrada
            val inputStream = context.contentResolver.openInputStream(sourceUri)
                ?: return false

            // Decodificar bitmap
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            // Calcular factor de escala si la imagen es muy grande
            val scaleFactor = calculateScaleFactor(options.outWidth, options.outHeight)

            // Decodificar con escala
            val inputStream2 = context.contentResolver.openInputStream(sourceUri)
                ?: return false

            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = scaleFactor
            }
            val bitmap = BitmapFactory.decodeStream(inputStream2, null, scaledOptions)
            inputStream2.close()

            if (bitmap == null) {
                Log.e(TAG, "❌ No se pudo decodificar la imagen")
                return false
            }

            // Guardar con compresión JPEG
            val outputStream = FileOutputStream(destinationFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)
            outputStream.flush()
            outputStream.close()
            bitmap.recycle()

            Log.d(TAG, "📊 Tamaño del archivo: ${destinationFile.length() / 1024} KB")
            true

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al copiar y comprimir imagen: ${e.message}", e)
            false
        }
    }

    /**
     * Calcula el factor de escala para reducir imágenes grandes
     */
    private fun calculateScaleFactor(width: Int, height: Int): Int {
        var inSampleSize = 1
        if (width > MAX_IMAGE_SIZE || height > MAX_IMAGE_SIZE) {
            val halfWidth = width / 2
            val halfHeight = height / 2

            while (halfWidth / inSampleSize >= MAX_IMAGE_SIZE &&
                   halfHeight / inSampleSize >= MAX_IMAGE_SIZE) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * Elimina una imagen del almacenamiento local
     * @param imagePath Ruta local de la imagen
     */
    suspend fun deleteImage(imagePath: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (imagePath.isBlank()) {
                    return@withContext Result.success(true)
                }

                val file = File(imagePath)
                if (file.exists()) {
                    val deleted = file.delete()
                    if (deleted) {
                        Log.d(TAG, "🗑️ Imagen eliminada: $imagePath")
                        Result.success(true)
                    } else {
                        Log.w(TAG, "⚠️ No se pudo eliminar la imagen")
                        Result.success(false)
                    }
                } else {
                    Log.d(TAG, "📂 Imagen no existe: $imagePath")
                    Result.success(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al eliminar imagen: ${e.message}", e)
                Result.success(true) // No fallar si no se puede eliminar
            }
        }
    }

    /**
     * Actualiza una imagen (elimina la anterior y guarda la nueva)
     */
    suspend fun updateArtworkImage(
        oldImagePath: String?,
        newImageUri: Uri,
        artistId: String
    ): Result<String> {
        return try {
            // Eliminar imagen anterior si existe
            if (!oldImagePath.isNullOrBlank()) {
                Log.d(TAG, "🗑️ Eliminando imagen anterior...")
                deleteImage(oldImagePath)
            }

            // Subir nueva imagen
            uploadArtworkImage(newImageUri, artistId)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al actualizar imagen: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Verifica si una imagen existe en el almacenamiento local
     */
    fun imageExists(imagePath: String): Boolean {
        return try {
            File(imagePath).exists()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtiene el tamaño total de las imágenes almacenadas
     */
    fun getTotalStorageSize(): Long {
        var totalSize = 0L
        try {
            val artworksDir = getArtworksDir()
            val profilesDir = getProfilesDir()

            artworksDir.listFiles()?.forEach { file ->
                totalSize += file.length()
            }

            profilesDir.listFiles()?.forEach { file ->
                totalSize += file.length()
            }

            Log.d(TAG, "📊 Almacenamiento total usado: ${totalSize / 1024 / 1024} MB")
        } catch (e: Exception) {
            Log.e(TAG, "Error al calcular tamaño: ${e.message}")
        }
        return totalSize
    }
}

