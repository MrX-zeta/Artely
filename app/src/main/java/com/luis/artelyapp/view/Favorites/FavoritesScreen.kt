package com.luis.artelyapp.view.Favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.luis.artelyapp.viewmodel.FavoriteUiState
import com.luis.artelyapp.viewmodel.FavoriteViewModel
import java.io.File

private val DarkBackground = Color(0xFF1A1A1A)
private val CardBackground = Color(0xFF2A2A2A)
private val BorderColor = Color(0xFF333333)
private val AccentGold = Color(0xFFD4AF37)
private val TextSecondary = Color(0xFFAAAAAA)
private val TextPrimary = Color(0xFFFFFFFF)

@Composable
fun FavoritesScreen(
    viewModel: FavoriteViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onArtworkClick: (String, String) -> Unit = { _, _ -> },
    onNavigateToGallery: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            FavoritesTopBar(
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            FavoritesBottomBar(
                onNavigateToGallery = onNavigateToGallery,
                onNavigateToChat = onNavigateToChat,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is FavoriteUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AccentGold
                    )
                }
                is FavoriteUiState.Success -> {
                    if (state.favorites.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.favorites) { (favorite, artwork) ->
                                if (artwork != null) {
                                    FavoriteArtworkCard(
                                        artwork = artwork,
                                        favoriteId = favorite.id_Favorite,
                                        onArtworkClick = {
                                            onArtworkClick(artwork.id_ArtWork, artwork.id_Artist)
                                        },
                                        onRemoveFavorite = {
                                            viewModel.removeFromFavorites(favorite.id_Favorite)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                is FavoriteUiState.Empty -> {
                    EmptyFavoritesMessage(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is FavoriteUiState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesTopBar(
    onBackClick: () -> Unit
) {
    Column {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Mis Favoritos",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp)
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
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DarkBackground
            )
        )

        // Línea divisoria
        HorizontalDivider(color = BorderColor, thickness = 1.dp)
    }
}

@Composable
private fun FavoritesBottomBar(
    onNavigateToGallery: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(DarkBackground)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val items = listOf(
            "Inicio" to "🏠",
            "Chat" to "💬",
            "Favoritos" to "❤️",
            "Perfil" to "👤"
        )

        items.forEachIndexed { index, pair ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    when (index) {
                        0 -> onNavigateToGallery()
                        1 -> onNavigateToChat()
                        2 -> { } // Ya estamos en favoritos
                        3 -> onNavigateToProfile()
                    }
                }
            ) {
                Text(text = pair.second, fontSize = 18.sp)

                Text(
                    text = pair.first,
                    fontSize = 10.sp,
                    color = if (index == 2) AccentGold else Color(0xFF888888)
                )
            }
        }
    }
}

@Composable
private fun FavoriteArtworkCard(
    artwork: com.luis.artelyapp.model.Artwork,
    favoriteId: String,
    onArtworkClick: () -> Unit,
    onRemoveFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable { onArtworkClick() },
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Imagen de la obra
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.Black)
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
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "📷",
                                    fontSize = 32.sp
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📷",
                                fontSize = 32.sp
                            )
                        }
                    }
                }

                // Información de la obra
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = artwork.Title,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (artwork.Status == "Available" && artwork.Price > 0) {
                        Text(
                            text = "$${String.format(java.util.Locale.US, "%.2f", artwork.Price)}",
                            color = AccentGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Botón de eliminar favorito
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xCC000000))
                    .clickable { onRemoveFavorite() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "❤️",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyFavoritesMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "💔",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "No tienes favoritos",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Explora la galería y agrega obras que te gusten",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ErrorMessage(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠️",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Error",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

