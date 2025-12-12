package com.luis.artelyapp.view.ArtistProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.luis.artelyapp.model.Artist
import com.luis.artelyapp.model.ArtistStats
import com.luis.artelyapp.model.Artwork
import com.luis.artelyapp.model.ProfileTab
import java.io.File

private val DarkBackground = Color(0xFF1A1A1A)
private val SecondaryBackground = Color(0xFF2D2D2D)
private val CardBackground = Color(0xFF2A2A2A)
private val BorderColor = Color(0xFF333333)
private val AccentGold = Color(0xFFD4AF37)
private val TextSecondary = Color(0xFFAAAAAA)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextLight = Color(0xFFE0E0E0)

@Composable
fun ArtistProfileScreen(
    artistId: String? = null,
    artistName: String? = null,
    viewModel: com.luis.artelyapp.viewmodel.ArtistViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onArtworkClick: (String) -> Unit = {},
    onNavigateToGallery: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToUserProfile: () -> Unit = {}
) {
    // Estado local para las pestañas
    var selectedTab by remember { mutableStateOf(ProfileTab.GALLERY) }

    // Observar el estado del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Cargar datos cuando se monta el componente
    LaunchedEffect(artistId, artistName) {
        when {
            artistId != null -> viewModel.loadArtistData(artistId)
            artistName != null -> viewModel.loadArtistByName(artistName)
            else -> viewModel.loadArtistData("1")
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            BottomNavigationBar(
                onNavigateToGallery = onNavigateToGallery,
                onNavigateToChat = onNavigateToChat,
                onNavigateToProfile = onNavigateToUserProfile
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is com.luis.artelyapp.viewmodel.ArtistUiState.Idle -> {
                // Estado inicial - no hacer nada o mostrar placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Selecciona un artista",
                        color = TextSecondary,
                        fontSize = 16.sp
                    )
                }
            }

            is com.luis.artelyapp.viewmodel.ArtistUiState.Loading -> {
                // Mostrar indicador de carga
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
                            text = "Cargando perfil...",
                            color = TextSecondary,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            is com.luis.artelyapp.viewmodel.ArtistUiState.Error -> {
                // Mostrar mensaje de error
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "❌",
                            fontSize = 48.sp
                        )
                        Text(
                            text = "Error al cargar el perfil",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = state.message,
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.refresh() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentGold
                            )
                        ) {
                            Text("Reintentar", color = DarkBackground)
                        }
                    }
                }
            }

            is com.luis.artelyapp.viewmodel.ArtistUiState.Success -> {
                // Mostrar contenido del perfil
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    item { TopBar(onBackClick = onBackClick) }
                    item {
                        ArtistHeader(
                            artist = state.artist,
                            stats = state.stats,
                            isOwnProfile = state.isOwnProfile,
                            isFollowing = state.isFollowing,
                            onFollowClick = { viewModel.toggleFollow(state.artist.id_User) }
                        )
                    }
                    item {
                        TabSection(
                            selectedTab = selectedTab,
                            onTabSelected = { tab -> selectedTab = tab }
                        )
                    }
                    item {
                        CollectionsSection(
                            artworks = when(selectedTab) {
                                ProfileTab.GALLERY -> viewModel.getGalleryArtworks()
                                ProfileTab.FOR_SALE -> viewModel.getForSaleArtworks()
                            },
                            onArtworkClick = onArtworkClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(81.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "←",
                color = TextPrimary,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "ArtelyGallery",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 1.sp,
            modifier = Modifier.weight(1f)
        )
    }

    HorizontalDivider(color = BorderColor, thickness = 1.dp)
}

@Composable
private fun ArtistHeader(
    artist: Artist,
    stats: ArtistStats,
    isOwnProfile: Boolean = false,
    isFollowing: Boolean = false,
    onFollowClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        DarkBackground.copy(alpha = 0.8f),
                        SecondaryBackground.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar - mostrar foto si existe, sino inicial
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            if (artist.profileImageUrl.isEmpty()) {
                                Brush.linearGradient(
                                    colors = listOf(AccentGold, Color(0xFFB8941F))
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(Color.Transparent, Color.Transparent)
                                )
                            },
                            CircleShape
                        )
                        .border(3.dp, AccentGold.copy(alpha = 0.3f), CircleShape),
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
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Mostrar nombre o placeholder
                    Text(
                        text = if (artist.UserName.isNotBlank()) artist.UserName else "Sin nombre",
                        color = if (artist.UserName.isNotBlank()) TextPrimary else TextSecondary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar bio o placeholder
                    Text(
                        text = if (artist.Bio.isNotBlank()) artist.Bio else "Sin biografía",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 19.6.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar ubicación o placeholder
                    Text(
                        text = if (artist.Location.isNotBlank()) "📍 ${artist.Location}" else "📍 Sin ubicación",
                        color = if (artist.Location.isNotBlank()) AccentGold else TextSecondary,
                        fontSize = 13.sp
                    )
                }

                // Botón Editar Perfil o Seguir
                if (isOwnProfile) {
                    Button(
                        onClick = { /* TODO: Navegar a editar perfil */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGold
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Editar Perfil",
                            color = DarkBackground,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Botón Seguir/Siguiendo para visitantes
                    Button(
                        onClick = onFollowClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) Color(0xFF333333) else AccentGold
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = if (isFollowing) "Siguiendo" else "Seguir",
                            color = if (isFollowing) TextSecondary else DarkBackground,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Card - mostrar estadísticas reales
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(stats.totalArtworks.toString(), "OBRAS")
                    StatItem(stats.followers.toString(), "SEGUIDORES")
                    StatItem(stats.following.toString(), "SIGUIENDO")
                }
            }
        }
    }

    HorizontalDivider(color = BorderColor, thickness = 1.dp)
}

@Composable
private fun StatItem(number: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun TabSection(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(DarkBackground)
    ) {
        // Gallery Tab
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(DarkBackground)
                .clickable { onTabSelected(ProfileTab.GALLERY) },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Galería",
                    color = if (selectedTab == ProfileTab.GALLERY) AccentGold else TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                if (selectedTab == ProfileTab.GALLERY) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(2.dp)
                            .background(AccentGold)
                    )
                }
            }
        }

        // Art for Sale Tab
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onTabSelected(ProfileTab.FOR_SALE) },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Arte en venta",
                    color = if (selectedTab == ProfileTab.FOR_SALE) AccentGold else TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                if (selectedTab == ProfileTab.FOR_SALE) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(2.dp)
                            .background(AccentGold)
                    )
                }
            }
        }
    }

    HorizontalDivider(color = BorderColor, thickness = 1.dp)
}

@Composable
private fun CollectionsSection(
    artworks: List<Artwork>,
    onArtworkClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(DarkBackground, SecondaryBackground)
                )
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp)
                    .background(AccentGold, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "COLECCIONES",
                color = AccentGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // LazyColumn vertical con scroll para mostrar las obras de arte
        if (artworks.isEmpty()) {
            // Mensaje cuando no hay obras
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "🎨",
                        fontSize = 64.sp
                    )

                    Text(
                        text = "Aún no tienes obras en galería",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Comparte tu primera obra de arte con la comunidad",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // LazyColumn vertical con scroll para las obras
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp), // Altura fija para permitir scroll
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(artworks) { artwork ->
                    // Centrar cada tarjeta en el ancho disponible
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ArtPieceCard(
                            artwork = artwork,
                            onClick = { onArtworkClick(artwork.id_ArtWork) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ArtPieceCard(
    artwork: Artwork,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(280.dp) // Tamaño original restaurado
            .height(200.dp) // Altura mantenida para acomodar más texto
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Image - mostrar la imagen real si existe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(BorderColor, Color(0xFF555555))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (artwork.ImageUrl.isNotEmpty()) {
                    val file = File(artwork.ImageUrl)
                    if (file.exists()) {
                        AsyncImage(
                            model = file,
                            contentDescription = artwork.Title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Placeholder si el archivo no existe
                        Text(
                            text = artwork.Title,
                            color = Color(0xFF777777),
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Placeholder si no hay URL
                    Text(
                        text = artwork.Title,
                        color = Color(0xFF777777),
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Card content
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = artwork.Title,
                    color = TextLight,
                    fontSize = 14.sp,
                    maxLines = 2,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (artwork.Status == "Available" && artwork.Price > 0) {
                        "$${String.format(java.util.Locale.US, "%.2f", artwork.Price)}"
                    } else {
                        artwork.Description
                    },
                    color = AccentGold,
                    fontSize = 12.sp,
                    maxLines = 2,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onNavigateToGallery: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(DarkBackground)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            icon = "🏠",
            label = "Inicio",
            isSelected = false,
            onClick = onNavigateToGallery
        )
        BottomNavItem(
            icon = "💬",
            label = "Chat",
            isSelected = false,
            onClick = onNavigateToChat
        )
        BottomNavItem(
            icon = "👤",
            label = "Perfil",
            isSelected = true,
            onClick = onNavigateToProfile
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit = {}
) {
    val textColor = if (isSelected) AccentGold else TextSecondary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = icon,
            fontSize = 18.sp
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ArtistProfilePreview() {
    ArtistProfileScreen()
}