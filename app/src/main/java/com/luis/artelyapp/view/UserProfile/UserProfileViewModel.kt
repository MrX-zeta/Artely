package com.luis.artelyapp.view.UserProfile

import android.net.Uri
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.luis.artelyapp.model.Artist
import com.luis.artelyapp.model.Customer
import com.luis.artelyapp.repository.ArtistRepository
import com.luis.artelyapp.repository.ArtworkRepository
import com.luis.artelyapp.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Información del perfil del usuario
 */
data class UserProfileInfo(
    val name: String = "",
    val bio: String = "",
    val location: String = "",
    val profileImageUri: Uri? = null
)

/**
 * Estados del perfil del usuario
 */
sealed class UserProfileUiState {
    object Idle : UserProfileUiState()
    object Loading : UserProfileUiState()
    data class Success(
        val name: String,
        val bio: String,
        val location: String,
        val artworksCount: Int,
        val followersCount: Int,
        val followingCount: Int
    ) : UserProfileUiState()
    object Empty : UserProfileUiState()
    data class Error(val message: String) : UserProfileUiState()
}

class UserProfileViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val artistRepository: ArtistRepository = ArtistRepository()
    private val artworkRepository: ArtworkRepository = ArtworkRepository()
    private val customerRepository: CustomerRepository = CustomerRepository()
    private val authRepository: com.luis.artelyapp.repository.AuthRepository = com.luis.artelyapp.repository.AuthRepository()

    // ImageRepository necesita Context
    private val imageRepository: com.luis.artelyapp.repository.ImageRepository by lazy {
        com.luis.artelyapp.repository.ImageRepository(application.applicationContext)
    }

    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Idle)
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfileInfo())
    val userProfile: StateFlow<UserProfileInfo> = _userProfile.asStateFlow()

    private val _userArtworks = MutableStateFlow<List<UserArtwork>>(emptyList())
    val userArtworks: StateFlow<List<UserArtwork>> = _userArtworks.asStateFlow()

    // Estado para saber si el usuario actual es artista o customer
    private val _isArtist = MutableStateFlow(true)
    val isArtist: StateFlow<Boolean> = _isArtist.asStateFlow()

    init {
        // Limpiar cualquier dato en caché y cargar perfil del usuario actual
        clearProfileData()
        loadCurrentUserProfile()
    }

    /**
     * Limpia todos los datos del perfil
     */
    private fun clearProfileData() {
        _userProfile.value = UserProfileInfo()
        _userArtworks.value = emptyList()
        _isArtist.value = true
        _uiState.value = UserProfileUiState.Idle
        android.util.Log.d("UserProfileVM", "🧹 Datos del perfil limpiados")
    }

    /**
     * Método público para forzar una recarga completa del perfil
     */
    fun reloadProfile() {
        clearProfileData()
        loadCurrentUserProfile()
    }

    /**
     * Carga el perfil del usuario actualmente autenticado
     */
    fun loadCurrentUserProfile() {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading
            android.util.Log.d("UserProfileVM", "🔄 Iniciando carga de perfil...")

            val currentUser = authRepository.getCurrentUser()
            val currentUserEmail = currentUser?.email
            val currentUserId = currentUser?.uid

            android.util.Log.d("UserProfileVM", "👤 Usuario autenticado: ${currentUser != null}")
            android.util.Log.d("UserProfileVM", "🆔 UID de Firebase Auth: $currentUserId")
            android.util.Log.d("UserProfileVM", "📧 Email del usuario: $currentUserEmail")

            if (currentUser == null || currentUserEmail == null) {
                android.util.Log.e("UserProfileVM", "❌ No hay usuario autenticado")
                loadDefaultProfile()
                return@launch
            }

            // Estrategia simplificada: buscar directamente por email
            android.util.Log.d("UserProfileVM", "🔍 Buscando usuario por email...")

            // Intentar encontrar como artista primero
            val artist = artistRepository.getArtistByEmail(currentUserEmail)
            if (artist != null) {
                android.util.Log.d("UserProfileVM", "✅ Usuario encontrado como artista: ${artist.UserName}")
                loadArtistProfileData(artist)
                return@launch
            }

            // Intentar encontrar como customer
            val customer = customerRepository.getCustomerByEmail(currentUserEmail)
            if (customer != null) {
                android.util.Log.d("UserProfileVM", "✅ Usuario encontrado como customer: ${customer.UserName}")
                loadCustomerProfileData(customer)
                return@launch
            }

            // Si no se encuentra por email, usar el método de fallback anterior
            android.util.Log.d("UserProfileVM", "⚠️ No encontrado por email, usando método de fallback...")

            // currentUserId ya fue declarado arriba, no necesitamos redeclararlo
            if (currentUserId != null) {
                loadArtistProfileWithFallback(currentUserId, currentUserId)
            } else {
                android.util.Log.e("UserProfileVM", "❌ No se pudo encontrar el usuario")
                _uiState.value = UserProfileUiState.Error("Usuario no encontrado")
            }
        }
    }

    /**
     * Carga el perfil por defecto (vacío)
     */
    private fun loadDefaultProfile() {
        _userProfile.value = UserProfileInfo(
            name = "",
            bio = "",
            location = "",
            profileImageUri = null
        )
        _userArtworks.value = emptyList()
        _uiState.value = UserProfileUiState.Empty
    }

    /**
     * Carga los datos de perfil de un artista encontrado
     */
    private fun loadArtistProfileData(artist: Artist) {
        viewModelScope.launch {
            android.util.Log.d("UserProfileVM", "🎨 Cargando datos del artista: ${artist.UserName}")
            android.util.Log.d("UserProfileVM", "📝 Datos del artista desde BD:")
            android.util.Log.d("UserProfileVM", "   - Nombre: '${artist.UserName}'")
            android.util.Log.d("UserProfileVM", "   - Bio: '${artist.Bio}'")
            android.util.Log.d("UserProfileVM", "   - Ubicación: '${artist.Location}'")
            android.util.Log.d("UserProfileVM", "   - Email: '${artist.Email}'")

            // Establecer que el usuario es artista
            _isArtist.value = true

            try {
                // Cargar estadísticas y obras
                val stats = artistRepository.getArtistStats(artist.id_User)
                android.util.Log.d("UserProfileVM", "📊 Stats cargadas: obras=${stats.totalArtworks}, seguidores=${stats.followers}")

                // Intentar cargar las obras
                artworkRepository.getArtworksByArtist(artist.id_User).fold(
                    onSuccess = { artworks ->
                        android.util.Log.d("UserProfileVM", "🖼️ Obras cargadas: ${artworks.size}")
                        val userArtworks = artworks.map { artwork ->
                            // Convertir ruta local a Uri para mostrar imagen
                            android.util.Log.d("UserProfileVM", "📸 Procesando obra: ${artwork.Title}")
                            android.util.Log.d("UserProfileVM", "📍 Ruta de imagen: ${artwork.ImageUrl}")

                            val imageUri = if (artwork.ImageUrl.isNotEmpty()) {
                                try {
                                    val file = java.io.File(artwork.ImageUrl)
                                    android.util.Log.d("UserProfileVM", "📂 Archivo existe: ${file.exists()}")
                                    android.util.Log.d("UserProfileVM", "📂 Ruta absoluta: ${file.absolutePath}")

                                    if (file.exists()) {
                                        val uri = Uri.fromFile(file)
                                        android.util.Log.d("UserProfileVM", "✅ Uri creada: $uri")
                                        uri
                                    } else {
                                        android.util.Log.e("UserProfileVM", "❌ Archivo no existe en: ${file.absolutePath}")
                                        null
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("UserProfileVM", "❌ Error al crear Uri: ${e.message}", e)
                                    null
                                }
                            } else {
                                android.util.Log.w("UserProfileVM", "⚠️ ImageUrl está vacío")
                                null
                            }

                            UserArtwork(
                                id = artwork.id_ArtWork,
                                title = artwork.Title,
                                artist = artist.UserName,
                                description = artwork.Description,
                                imageUri = imageUri,
                                price = if (artwork.Price > 0) "$${artwork.Price}" else null,
                                isForSale = artwork.Status == "Available" && artwork.Price > 0
                            )
                        }
                        _userArtworks.value = userArtworks
                    },
                    onFailure = { error ->
                        android.util.Log.w("UserProfileVM", "⚠️ No se pudieron cargar las obras: ${error.message}")
                        _userArtworks.value = emptyList()
                    }
                )

                // Convertir la URL o ruta local de la imagen a Uri si existe
                val profileImageUri = if (artist.profileImageUrl.isNotEmpty()) {
                    android.util.Log.d("UserProfileVM", "📸 Convirtiendo ruta de imagen de perfil a Uri: ${artist.profileImageUrl}")
                    Uri.parse(artist.profileImageUrl)
                } else {
                    android.util.Log.d("UserProfileVM", "⚠️ No hay foto de perfil guardada")
                    null
                }

                // Actualizar el perfil
                _userProfile.value = UserProfileInfo(
                    name = artist.UserName,
                    bio = artist.Bio,
                    location = artist.Location,
                    profileImageUri = profileImageUri
                )

                android.util.Log.d("UserProfileVM", "📦 UserProfile actualizado:")
                android.util.Log.d("UserProfileVM", "   - Nombre: '${_userProfile.value.name}'")
                android.util.Log.d("UserProfileVM", "   - Bio: '${_userProfile.value.bio}'")
                android.util.Log.d("UserProfileVM", "   - Ubicación: '${_userProfile.value.location}'")

                _uiState.value = UserProfileUiState.Success(
                    name = artist.UserName,
                    bio = artist.Bio,
                    location = artist.Location,
                    artworksCount = stats.totalArtworks,
                    followersCount = stats.followers,
                    followingCount = stats.following
                )

                android.util.Log.d("UserProfileVM", "✅ Perfil de artista cargado correctamente")

            } catch (e: Exception) {
                android.util.Log.e("UserProfileVM", "❌ Error al cargar datos del artista: ${e.message}")
                _uiState.value = UserProfileUiState.Error("Error al cargar el perfil")
            }
        }
    }

    /**
     * Carga los datos de perfil de un customer encontrado
     */
    private fun loadCustomerProfileData(customer: Customer) {
        viewModelScope.launch {
            android.util.Log.d("UserProfileVM", "👥 Cargando datos del customer: ${customer.UserName}")
            android.util.Log.d("UserProfileVM", "🖼️ URL de foto de perfil: ${customer.profileImageUrl}")

            // Establecer que el usuario NO es artista (es customer)
            _isArtist.value = false

            try {
                // Convertir la URL o ruta local de la imagen a Uri si existe
                val profileImageUri = if (customer.profileImageUrl.isNotEmpty()) {
                    android.util.Log.d("UserProfileVM", "📸 Convirtiendo ruta de imagen a Uri: ${customer.profileImageUrl}")
                    Uri.parse(customer.profileImageUrl)
                } else {
                    android.util.Log.d("UserProfileVM", "⚠️ No hay foto de perfil guardada")
                    null
                }

                // Actualizar perfil básico
                _userProfile.value = UserProfileInfo(
                    name = customer.UserName,
                    bio = "",
                    location = "",
                    profileImageUri = profileImageUri
                )

                _userArtworks.value = emptyList()

                _uiState.value = UserProfileUiState.Success(
                    name = customer.UserName,
                    bio = "",
                    location = "",
                    artworksCount = 0,
                    followersCount = 0,
                    followingCount = 0
                )

                android.util.Log.d("UserProfileVM", "✅ Perfil de customer cargado correctamente")

            } catch (e: Exception) {
                android.util.Log.e("UserProfileVM", "❌ Error al cargar datos del customer: ${e.message}")
                _uiState.value = UserProfileUiState.Error("Error al cargar el perfil")
            }
        }
    }

    /**
     * Carga el perfil de un artista con fallback entre ID y UID
     */
    private fun loadArtistProfileWithFallback(artistId: String, uid: String) {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading
            android.util.Log.d("UserProfileVM", "🎨 loadArtistProfileWithFallback - ID: $artistId, UID: $uid")

            // DEBUG: Listar todos los artistas para encontrar el problema
            val allArtists = artistRepository.debugListAllArtists()
            android.util.Log.d("UserProfileVM", "🐛 DEBUG - Artistas en Firebase: ${allArtists.keys}")

            // Intentar primero con el ID hasheado
            var artist = artistRepository.getArtistById(artistId)
            android.util.Log.d("UserProfileVM", "🔍 Búsqueda por ID $artistId: ${if (artist != null) "ENCONTRADO" else "NO ENCONTRADO"}")

            // Si no se encuentra, intentar con el UID directo
            if (artist == null) {
                android.util.Log.d("UserProfileVM", "🔄 No encontrado por ID, intentando con UID...")
                artist = artistRepository.getArtistByUID(uid)
                android.util.Log.d("UserProfileVM", "🔍 Búsqueda por UID $uid: ${if (artist != null) "ENCONTRADO" else "NO ENCONTRADO"}")
            }

            // Si aún no se encuentra, intentar buscar por email usando el método específico
            if (artist == null) {
                android.util.Log.d("UserProfileVM", "🔄 Buscando por email usando método específico...")
                val currentUserEmail = authRepository.getCurrentUser()?.email
                if (currentUserEmail != null) {
                    artist = artistRepository.getArtistByEmail(currentUserEmail)
                    android.util.Log.d("UserProfileVM", "🔍 Búsqueda por email $currentUserEmail: ${if (artist != null) "ENCONTRADO" else "NO ENCONTRADO"}")
                }
            }

            // Último intento: buscar manualmente en la lista debug
            if (artist == null) {
                android.util.Log.d("UserProfileVM", "🔄 Último intento: buscando en la lista debug...")
                val currentUserEmail = authRepository.getCurrentUser()?.email
                artist = allArtists.values.find { it.Email == currentUserEmail }
                android.util.Log.d("UserProfileVM", "🔍 Búsqueda manual por email $currentUserEmail: ${if (artist != null) "ENCONTRADO con key=${allArtists.entries.find { it.value.Email == currentUserEmail }?.key}" else "NO ENCONTRADO"}")
            }

            android.util.Log.d("UserProfileVM", "👤 Artista obtenido: ${artist?.UserName}")

            if (artist == null) {
                android.util.Log.e("UserProfileVM", "❌ No se encontró el artista con ID: $artistId ni UID: $uid")
                _uiState.value = UserProfileUiState.Error("No se encontró el perfil del artista")
                return@launch
            }

            // Usar el ID del artista encontrado para las estadísticas
            val realArtistId = artist.id_User

            // Cargar estadísticas y obras
            val stats = artistRepository.getArtistStats(realArtistId)
            android.util.Log.d("UserProfileVM", "📊 Stats: obras=${stats.totalArtworks}, seguidores=${stats.followers}")

            artworkRepository.getArtworksByArtist(realArtistId).fold(
                onSuccess = { artworks ->
                    // Convertir Artwork a UserArtwork
                    val userArtworks = artworks.map { artwork ->
                        UserArtwork(
                            id = artwork.id_ArtWork,
                            title = artwork.Title,
                            artist = artist.UserName,
                            description = artwork.Description,
                            imageUri = null, // TODO: Cargar desde Firebase Storage
                            price = if (artwork.Price > 0) "$${artwork.Price}" else null,
                            isForSale = artwork.Status == "Available" && artwork.Price > 0
                        )
                    }

                    _userArtworks.value = userArtworks
                    _userProfile.value = UserProfileInfo(
                        name = artist.UserName,
                        bio = artist.Bio,
                        location = artist.Location,
                        profileImageUri = null
                    )

                    _uiState.value = UserProfileUiState.Success(
                        name = artist.UserName,
                        bio = artist.Bio,
                        location = artist.Location,
                        artworksCount = stats.totalArtworks,
                        followersCount = stats.followers,
                        followingCount = stats.following
                    )
                },
                onFailure = { error ->
                    android.util.Log.e("UserProfileVM", "❌ Error al cargar obras: ${error.message}")
                    // Aún mostrar el perfil aunque no se puedan cargar las obras
                    _userProfile.value = UserProfileInfo(
                        name = artist.UserName,
                        bio = artist.Bio,
                        location = artist.Location,
                        profileImageUri = null
                    )
                    _userArtworks.value = emptyList()
                    _uiState.value = UserProfileUiState.Success(
                        name = artist.UserName,
                        bio = artist.Bio,
                        location = artist.Location,
                        artworksCount = 0,
                        followersCount = 0,
                        followingCount = 0
                    )
                }
            )
        }
    }

    /**
     * Carga el perfil de un artista desde Firebase (método original mantenido por compatibilidad)
     */
    fun loadArtistProfile(artistId: String) {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading
            android.util.Log.d("UserProfileVM", "🎨 loadArtistProfile - ID: $artistId")

            val artist = artistRepository.getArtistById(artistId)
            android.util.Log.d("UserProfileVM", "👤 Artista obtenido: ${artist?.UserName}")

            if (artist == null) {
                android.util.Log.e("UserProfileVM", "❌ No se encontró el artista con ID: $artistId")
                _uiState.value = UserProfileUiState.Error("No se encontró el artista")
                return@launch
            }

            // Cargar estadísticas y obras
            val stats = artistRepository.getArtistStats(artistId)
            android.util.Log.d("UserProfileVM", "📊 Stats: obras=${stats.totalArtworks}, seguidores=${stats.followers}")

            artworkRepository.getArtworksByArtist(artistId).fold(
                onSuccess = { artworks ->
                    // Convertir Artwork a UserArtwork
                    val userArtworks = artworks.map { artwork ->
                        UserArtwork(
                            id = artwork.id_ArtWork,
                            title = artwork.Title,
                            artist = artist.UserName,
                            description = artwork.Description,
                            imageUri = null, // TODO: Cargar desde Firebase Storage
                            price = if (artwork.Price > 0) "$${artwork.Price}" else null,
                            isForSale = artwork.Status == "Available" && artwork.Price > 0
                        )
                    }

                    _userArtworks.value = userArtworks
                    _userProfile.value = UserProfileInfo(
                        name = artist.UserName,
                        bio = artist.Bio,
                        location = artist.Location,
                        profileImageUri = null
                    )

                    _uiState.value = UserProfileUiState.Success(
                        name = artist.UserName,
                        bio = artist.Bio,
                        location = artist.Location,
                        artworksCount = stats.totalArtworks,
                        followersCount = stats.followers,
                        followingCount = stats.following
                    )
                },
                onFailure = { error ->
                    _uiState.value = UserProfileUiState.Error(
                        error.message ?: "Error al cargar las obras"
                    )
                }
            )
        }
    }

    /**
     * Carga el perfil de un cliente con fallback entre ID y UID
     */
    private fun loadCustomerProfileWithFallback(customerId: String, uid: String) {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading
            android.util.Log.d("UserProfileVM", "👥 loadCustomerProfileWithFallback - ID: $customerId, UID: $uid")

            // DEBUG: Listar todos los customers para encontrar el problema
            val allCustomers = customerRepository.debugListAllCustomers()
            android.util.Log.d("UserProfileVM", "🐛 DEBUG - Customers en Firebase: ${allCustomers.keys}")

            // Intentar primero con el ID hasheado
            var customer = customerRepository.getCustomerById(customerId)
            android.util.Log.d("UserProfileVM", "🔍 Búsqueda por ID $customerId: ${if (customer != null) "ENCONTRADO" else "NO ENCONTRADO"}")

            // Si no se encuentra, intentar con el UID directo
            if (customer == null) {
                android.util.Log.d("UserProfileVM", "🔄 No encontrado por ID, intentando con UID...")
                customer = customerRepository.getCustomerByUID(uid)
                android.util.Log.d("UserProfileVM", "🔍 Búsqueda por UID $uid: ${if (customer != null) "ENCONTRADO" else "NO ENCONTRADO"}")
            }

            // Si aún no se encuentra, intentar buscar por email usando el método específico
            if (customer == null) {
                android.util.Log.d("UserProfileVM", "🔄 Buscando por email usando método específico...")
                val currentUserEmail = authRepository.getCurrentUser()?.email
                if (currentUserEmail != null) {
                    customer = customerRepository.getCustomerByEmail(currentUserEmail)
                    android.util.Log.d("UserProfileVM", "🔍 Búsqueda por email $currentUserEmail: ${if (customer != null) "ENCONTRADO" else "NO ENCONTRADO"}")
                }
            }

            // Último intento: buscar manualmente en la lista debug
            if (customer == null) {
                android.util.Log.d("UserProfileVM", "🔄 Último intento: buscando en la lista debug...")
                val currentUserEmail = authRepository.getCurrentUser()?.email
                customer = allCustomers.values.find { it.Email == currentUserEmail }
                android.util.Log.d("UserProfileVM", "🔍 Búsqueda manual por email $currentUserEmail: ${if (customer != null) "ENCONTRADO con key=${allCustomers.entries.find { it.value.Email == currentUserEmail }?.key}" else "NO ENCONTRADO"}")
            }

            android.util.Log.d("UserProfileVM", "👤 Customer obtenido: ${customer?.UserName}")

            if (customer == null) {
                android.util.Log.e("UserProfileVM", "❌ No se encontró el customer con ID: $customerId ni UID: $uid")
                _uiState.value = UserProfileUiState.Empty
                loadDefaultProfile()
                return@launch
            }

            android.util.Log.d("UserProfileVM", "✅ Actualizando perfil con: ${customer.UserName}")
            _userProfile.value = UserProfileInfo(
                name = customer.UserName,
                bio = "",
                location = "",
                profileImageUri = null
            )

            _userArtworks.value = emptyList()

            _uiState.value = UserProfileUiState.Success(
                name = customer.UserName,
                bio = "",
                location = "",
                artworksCount = 0,
                followersCount = 0,
                followingCount = 0
            )
        }
    }

    /**
     * Carga el perfil de un cliente desde Firebase (método original mantenido por compatibilidad)
     */
    fun loadCustomerProfile(customerId: String) {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading
            android.util.Log.d("UserProfileVM", "👥 loadCustomerProfile - ID: $customerId")

            val customer = customerRepository.getCustomerById(customerId)
            android.util.Log.d("UserProfileVM", "👤 Customer obtenido: ${customer?.UserName}")

            if (customer == null) {
                android.util.Log.e("UserProfileVM", "❌ No se encontró el customer con ID: $customerId")
                _uiState.value = UserProfileUiState.Empty
                loadDefaultProfile()
                return@launch
            }

            android.util.Log.d("UserProfileVM", "✅ Actualizando perfil con: ${customer.UserName}")
            _userProfile.value = UserProfileInfo(
                name = customer.UserName,
                bio = "",
                location = "",
                profileImageUri = null
            )

            _userArtworks.value = emptyList()

            _uiState.value = UserProfileUiState.Success(
                name = customer.UserName,
                bio = "",
                location = "",
                artworksCount = 0,
                followersCount = 0,
                followingCount = 0
            )
        }
    }

    /**
     * Agrega una nueva obra con imagen a Firebase
     */
    fun addArtwork(title: String, description: String, technique: String, imageUri: Uri?, isForSale: Boolean = false, price: String? = null) {
        viewModelScope.launch {
            try {
                if (imageUri == null) {
                    android.util.Log.e("UserProfileVM", "❌ No hay imagen seleccionada")
                    return@launch
                }

                val currentUserId = authRepository.getCurrentUserId()
                if (currentUserId == null) {
                    android.util.Log.e("UserProfileVM", "❌ No hay usuario autenticado")
                    return@launch
                }

                android.util.Log.d("UserProfileVM", "📤 Iniciando proceso de subida de obra...")
                android.util.Log.d("UserProfileVM", "📝 Título: $title")
                android.util.Log.d("UserProfileVM", "📝 Descripción: $description")
                android.util.Log.d("UserProfileVM", "💰 Precio: $price")
                android.util.Log.d("UserProfileVM", "🏷️ En venta: $isForSale")

                // 1. Subir imagen a almacenamiento local
                android.util.Log.d("UserProfileVM", "📤 Guardando imagen localmente...")
                val uploadResult = imageRepository.uploadArtworkImage(imageUri, currentUserId)

                if (uploadResult.isFailure) {
                    android.util.Log.e("UserProfileVM", "❌ Error al guardar imagen: ${uploadResult.exceptionOrNull()?.message}")
                    return@launch
                }

                val imagePath = uploadResult.getOrNull()!!
                android.util.Log.d("UserProfileVM", "✅ Imagen guardada en: $imagePath")

                // 2. Determinar precio y estado
                val priceValue = if (isForSale && !price.isNullOrBlank()) {
                    price.toDoubleOrNull() ?: 0.0
                } else {
                    0.0
                }

                val status = if (isForSale) "Available" else "Exhibition"

                android.util.Log.d("UserProfileVM", "💾 Guardando obra en Firebase Realtime Database...")

                // 3. Crear obra en Firebase Realtime Database
                val artworkResult = artworkRepository.createArtwork(
                    artistId = currentUserId,
                    title = title,
                    description = description,
                    imageUrl = imagePath,
                    price = priceValue,
                    technique = technique,
                    status = status
                )

                if (artworkResult.isSuccess) {
                    val artwork = artworkResult.getOrNull()!!
                    android.util.Log.d("UserProfileVM", "✅ Obra creada exitosamente en Firebase")
                    android.util.Log.d("UserProfileVM", "🆔 ID de obra: ${artwork.id_ArtWork}")

                    // 4. Convertir ruta guardada a Uri para mostrar
                    val savedImageUri = try {
                        val file = java.io.File(imagePath)
                        if (file.exists()) {
                            Uri.fromFile(file)
                        } else {
                            android.util.Log.e("UserProfileVM", "❌ Archivo no existe: $imagePath")
                            null
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("UserProfileVM", "❌ Error al convertir ruta a Uri: ${e.message}")
                        null
                    }

                    android.util.Log.d("UserProfileVM", "🖼️ Uri para mostrar: $savedImageUri")

                    // 5. Agregar a la lista local para mostrar inmediatamente
                    val newArtwork = UserArtwork(
                        id = artwork.id_ArtWork,
                        title = artwork.Title,
                        artist = _userProfile.value.name,
                        description = artwork.Description,
                        imageUri = savedImageUri,
                        isForSale = isForSale,
                        price = if (isForSale) price else null
                    )

                    _userArtworks.value = _userArtworks.value + newArtwork
                    android.util.Log.d("UserProfileVM", "📋 Obra agregada a la lista local")
                    android.util.Log.d("UserProfileVM", "📊 Total de obras: ${_userArtworks.value.size}")

                    // 6. Actualizar inmediatamente el contador en el UI
                    val currentState = _uiState.value
                    if (currentState is UserProfileUiState.Success) {
                        val updatedState = currentState.copy(
                            artworksCount = currentState.artworksCount + 1
                        )
                        _uiState.value = updatedState
                        android.util.Log.d("UserProfileVM", "✨ Contador actualizado en UI: ${updatedState.artworksCount}")
                    }

                    android.util.Log.d("UserProfileVM", "✅ Obra agregada exitosamente sin duplicación")
                } else {
                    android.util.Log.e("UserProfileVM", "❌ Error al crear obra en BD: ${artworkResult.exceptionOrNull()?.message}")
                }

            } catch (e: Exception) {
                android.util.Log.e("UserProfileVM", "❌ Excepción al agregar obra: ${e.message}", e)
            }
        }
    }

    /**
     * Elimina una obra
     */
    fun removeArtwork(artworkId: String) {
        _userArtworks.value = _userArtworks.value.filter { it.id != artworkId }
        // TODO: Eliminar de Firebase
    }

    /**
     * Cambia el estado de venta de una obra
     */
    fun toggleForSale(artworkId: String, price: String? = null) {
        _userArtworks.value = _userArtworks.value.map { artwork ->
            if (artwork.id == artworkId) {
                artwork.copy(isForSale = !artwork.isForSale, price = price)
            } else {
                artwork
            }
        }
        // TODO: Actualizar en Firebase
    }

    /**
     * Actualiza una obra existente
     */
    fun updateArtwork(artworkId: String, title: String, description: String, imageUri: Uri?, isForSale: Boolean, price: String? = null) {
        _userArtworks.value = _userArtworks.value.map { artwork ->
            if (artwork.id == artworkId) {
                artwork.copy(
                    title = title,
                    description = description,
                    imageUri = imageUri,
                    isForSale = isForSale,
                    price = if (isForSale) price else null
                )
            } else {
                artwork
            }
        }
        // TODO: Actualizar en Firebase
    }

    /**
     * Obtiene una obra por ID
     */
    fun getArtworkById(artworkId: String): UserArtwork? {
        return _userArtworks.value.find { it.id == artworkId }
    }

    /**
     * Actualiza el perfil del usuario
     */
    fun updateProfile(name: String, bio: String, location: String, profileImageUri: Uri?) {
        viewModelScope.launch {
            try {
                android.util.Log.d("UserProfileVM", "🔄 Actualizando perfil...")
                android.util.Log.d("UserProfileVM", "📝 Nombre: $name, Bio: $bio, Ubicación: $location")
                android.util.Log.d("UserProfileVM", "🖼️ Imagen URI: $profileImageUri")

                val currentUserEmail = authRepository.getCurrentUser()?.email
                val currentUserId = authRepository.getCurrentUser()?.uid
                if (currentUserEmail == null || currentUserId == null) {
                    android.util.Log.e("UserProfileVM", "❌ No hay usuario autenticado")
                    return@launch
                }

                // Determinar si es artista o customer buscando primero como artista
                val isArtist = artistRepository.getArtistByEmail(currentUserEmail) != null

                // Subir imagen si hay una nueva imagen
                var profileImageUrl: String? = null
                if (profileImageUri != null) {
                    android.util.Log.d("UserProfileVM", "📤 Guardando imagen de perfil...")
                    val uploadResult = imageRepository.uploadProfileImage(profileImageUri, currentUserId)
                    if (uploadResult.isSuccess) {
                        profileImageUrl = uploadResult.getOrNull()
                        android.util.Log.d("UserProfileVM", "✅ Imagen guardada correctamente: $profileImageUrl")
                    } else {
                        android.util.Log.e("UserProfileVM", "❌ Error al guardar imagen: ${uploadResult.exceptionOrNull()?.message}")
                    }
                }

                val updateResult = if (isArtist) {
                    android.util.Log.d("UserProfileVM", "🎨 Actualizando como artista...")
                    authRepository.updateArtistProfile(currentUserEmail, name, bio, location, profileImageUrl)
                } else {
                    android.util.Log.d("UserProfileVM", "👥 Actualizando como customer...")
                    authRepository.updateCustomerProfile(currentUserEmail, name, profileImageUrl)
                }

                if (updateResult.isSuccess) {
                    android.util.Log.d("UserProfileVM", "✅ Perfil actualizado correctamente en BD")

                    // Actualizar el estado local con la nueva imagen
                    if (profileImageUri != null) {
                        _userProfile.value = _userProfile.value.copy(profileImageUri = profileImageUri)
                    }

                    // Esperar un momento para que la BD se sincronice
                    kotlinx.coroutines.delay(500)

                    // Recargar el perfil desde la BD para asegurar que tenemos los datos más recientes
                    android.util.Log.d("UserProfileVM", "🔄 Recargando perfil desde BD...")
                    loadCurrentUserProfile()

                } else {
                    android.util.Log.e("UserProfileVM", "❌ Error al actualizar perfil: ${updateResult.exceptionOrNull()?.message}")
                    // Podrías mostrar un error al usuario aquí
                }

            } catch (e: Exception) {
                android.util.Log.e("UserProfileVM", "❌ Excepción al actualizar perfil: ${e.message}")
            }
        }
    }

    /**
     * Recarga el perfil actual
     */
    fun refresh() {
        loadCurrentUserProfile()
    }

    /**
     * Cierra la sesión del usuario actual
     */
    fun logout() {
        viewModelScope.launch {
            try {
                android.util.Log.d("UserProfileVM", "Cerrando sesión...")
                authRepository.logout()

                // Limpiar todos los datos del perfil
                clearProfileData()

                android.util.Log.d("UserProfileVM", "Sesión cerrada correctamente")
            } catch (e: Exception) {
                android.util.Log.e("UserProfileVM", "Error al cerrar sesión: ${e.message}")
            }
        }
    }
}
