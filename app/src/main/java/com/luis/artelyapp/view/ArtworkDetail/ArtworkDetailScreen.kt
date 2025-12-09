package com.luis.artelyapp.view.ArtworkDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.foundation.layout.statusBarsPadding
import com.luis.artelyapp.repository.AuthRepository
import com.luis.artelyapp.repository.ChatRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

private val DarkBackground = Color(0xFF1A1A1A)
private val CardBackground = Color(0xFF2A2A2A)
private val BorderColor = Color(0xFF333333)
private val AccentGold = Color(0xFFD4AF37)
private val TextSecondary = Color(0xFFAAAAAA)
private val TextPrimary = Color(0xFFFFFFFF)

@Composable
fun ArtworkDetailScreen(
    artworkId: String,
    artistId: String,
    viewModel: com.luis.artelyapp.viewmodel.ArtistViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onNavigateToArtistProfile: (String) -> Unit = {},
    onNavigateToOwnProfile: () -> Unit = {},
    onNavigateToChatWithArtist: (String) -> Unit = {}
) {
    // Verificar si el usuario actual es el artista de la obra
    val authRepository = remember { AuthRepository() }
    val currentUserId = remember { authRepository.getCurrentUserId() }
    val isOwnArtwork = remember(currentUserId, artistId) {
        currentUserId == artistId
    }

    val coroutineScope = rememberCoroutineScope()
    val chatRepository = remember { ChatRepository() }

    // Cargar datos del artista si no están cargados
    LaunchedEffect(artistId) {
        viewModel.loadArtistData(artistId)
    }

    // Buscar la obra en el estado del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    val artwork = remember(uiState, artworkId) {
        when (val state = uiState) {
            is com.luis.artelyapp.viewmodel.ArtistUiState.Success -> {
                state.artworks.find { it.id_ArtWork == artworkId }
            }
            else -> null
        }
    }

    val artist = remember(uiState) {
        when (val state = uiState) {
            is com.luis.artelyapp.viewmodel.ArtistUiState.Success -> {
                state.artist
            }
            else -> null
        }
    }

    Scaffold(
        containerColor = DarkBackground
    ) { innerPadding ->
        if (artwork != null && artist != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // TopBar - Arriba de la imagen (no flotante)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .background(DarkBackground)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        // Back button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF333333))
                                .clickable { onBackClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "←",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Detalle de obra",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Línea divisoria
                    HorizontalDivider(color = BorderColor, thickness = 1.dp)

                // Imagen principal
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color.Black)
                ) {
                        // Imagen de la obra
                        if (artwork.ImageUrl.isNotEmpty()) {
                            val file = File(artwork.ImageUrl)
                            if (file.exists()) {
                                AsyncImage(
                                    model = file,
                                    contentDescription = artwork.Title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                // Placeholder si el archivo no existe
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Imagen no disponible",
                                        color = TextSecondary,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sin imagen",
                                    color = TextSecondary,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                // Información de la obra
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Título de la obra
                    Text(
                        text = artwork.Title,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Técnica
                    if (artwork.Technique.isNotEmpty()) {
                        Text(
                            text = artwork.Technique,
                            color = AccentGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Precio si está en venta
                    if (artwork.Status == "Available" && artwork.Price > 0) {
                        Text(
                            text = "$${String.format(java.util.Locale.US, "%.2f", artwork.Price)}",
                            color = AccentGold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Descripción
                    Text(
                        text = artwork.Description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = BorderColor, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sección del artista
                    Text(
                        text = "Acerca del artista",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Card del artista
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = CardBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar del artista - foto si existe, sino inicial
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(if (artist.profileImageUrl.isEmpty()) AccentGold else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                if (artist.profileImageUrl.isNotEmpty()) {
                                    // Mostrar foto de perfil
                                    AsyncImage(
                                        model = android.net.Uri.parse(artist.profileImageUrl),
                                        contentDescription = "Foto de perfil de ${artist.UserName}",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Mostrar la inicial del nombre del artista
                                    val initial = artist.UserName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
                                    Text(
                                        text = initial,
                                        color = Color.Black,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = artist.UserName,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                if (artist.Location.isNotEmpty()) {
                                    Text(
                                        text = "📍 ${artist.Location}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Botones de acción (solo si no es el propio perfil)
                            if (!isOwnArtwork) {
                                val isFollowing = when (val state = uiState) {
                                    is com.luis.artelyapp.viewmodel.ArtistUiState.Success -> state.isFollowing
                                    else -> false
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Botón Seguir/Siguiendo
                                    Button(
                                        onClick = { viewModel.toggleFollow(artist.id_User) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isFollowing) Color(0xFF333333) else AccentGold
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (isFollowing) "Siguiendo" else "Seguir",
                                            color = if (isFollowing) TextSecondary else Color.Black,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Botón Chatear (solo si está siguiendo)
                                    if (isFollowing) {
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    // Buscar si existe un chat entre el usuario actual y el artista
                                                    val result = chatRepository.findChatBetweenUsers(
                                                        artistId = artist.id_User,
                                                        customerId = currentUserId ?: ""
                                                    )

                                                    result.fold(
                                                        onSuccess = { existingChat ->
                                                            if (existingChat != null) {
                                                                // Si existe el chat, navegar a él
                                                                onNavigateToChatWithArtist(existingChat.id_Chat)
                                                            } else {
                                                                // Si no existe, crear uno nuevo
                                                                val createResult = chatRepository.createChat(
                                                                    artistId = artist.id_User,
                                                                    customerId = currentUserId ?: ""
                                                                )
                                                                createResult.fold(
                                                                    onSuccess = { newChat ->
                                                                        onNavigateToChatWithArtist(newChat.id_Chat)
                                                                    },
                                                                    onFailure = { error ->
                                                                        android.util.Log.e("ArtworkDetail", "Error al crear chat: ${error.message}")
                                                                    }
                                                                )
                                                            }
                                                        },
                                                        onFailure = { error ->
                                                            android.util.Log.e("ArtworkDetail", "Error al buscar chat: ${error.message}")
                                                        }
                                                    )
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF4CAF50)
                                            ),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "💬",
                                                    fontSize = 12.sp
                                                )
                                                Text(
                                                    text = "Chat",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón para visitar el perfil completo
                    Button(
                        onClick = {
                            if (isOwnArtwork) {
                                // Si es el propio artista, ir a UserProfile
                                onNavigateToOwnProfile()
                            } else {
                                // Si es otro artista, ir a ArtistProfile
                                onNavigateToArtistProfile(artist.id_User)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGold
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isOwnArtwork) "Ver mi perfil" else "Ver perfil del artista",
                            color = DarkBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón "Chatear con el artista" (solo si no es propio perfil y está siguiendo)
                    if (!isOwnArtwork) {
                        val isFollowing = when (val state = uiState) {
                            is com.luis.artelyapp.viewmodel.ArtistUiState.Success -> state.isFollowing
                            else -> false
                        }

                        if (isFollowing) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        // Buscar si existe un chat entre el usuario actual y el artista
                                        val result = chatRepository.findChatBetweenUsers(
                                            artistId = artist.id_User,
                                            customerId = currentUserId ?: ""
                                        )

                                        result.fold(
                                            onSuccess = { existingChat ->
                                                if (existingChat != null) {
                                                    // Si existe el chat, navegar a él
                                                    onNavigateToChatWithArtist(existingChat.id_Chat)
                                                } else {
                                                    // Si no existe, crear uno nuevo
                                                    val createResult = chatRepository.createChat(
                                                        artistId = artist.id_User,
                                                        customerId = currentUserId ?: ""
                                                    )
                                                    createResult.fold(
                                                        onSuccess = { newChat ->
                                                            onNavigateToChatWithArtist(newChat.id_Chat)
                                                        },
                                                        onFailure = { error ->
                                                            android.util.Log.e("ArtworkDetail", "Error al crear chat: ${error.message}")
                                                        }
                                                    )
                                                }
                                            },
                                            onFailure = { error ->
                                                android.util.Log.e("ArtworkDetail", "Error al buscar chat: ${error.message}")
                                            }
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "💬",
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "Chatear con el artista",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        } else {
            // Estado de carga o error
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = AccentGold,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Cargando obra...",
                        color = TextSecondary,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


