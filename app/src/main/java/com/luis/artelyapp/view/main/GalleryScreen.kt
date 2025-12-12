package com.luis.artelyapp.view.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.File

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToChat: () -> Unit = {},
    onArtistClick: (String) -> Unit = {},
    onArtworkClick: (String, String) -> Unit = { _, _ -> },
    onNavigateToProfile: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoritesCount by viewModel.favoritesCount.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                favoritesCount = favoritesCount,
                onTabSelected = { index ->
                    when (index) {
                        1 -> onNavigateToChat()
                        2 -> if (favoritesCount > 0) onNavigateToFavorites()
                        3 -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Header()

            when (val state = uiState) {
                is GalleryUiState.Loading -> {
                    // Estado de carga
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = Color(0xFFD4AF37)
                            )
                            Text(
                                text = "Cargando obras...",
                                color = Color(0xFFAAAAAA),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                is GalleryUiState.Empty -> {
                    // No hay obras disponibles
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
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
                                text = "No hay obras disponibles",
                                color = Color(0xFFE0E0E0),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Text(
                                text = "Sé el primero en compartir tu arte con la comunidad",
                                color = Color(0xFFAAAAAA),
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            androidx.compose.material3.Button(
                                onClick = { viewModel.refresh() },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD4AF37)
                                )
                            ) {
                                Text("Actualizar", color = Color(0xFF1A1A1A))
                            }
                        }
                    }
                }

                is GalleryUiState.Error -> {
                    // Error al cargar
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 48.sp
                            )
                            Text(
                                text = "Error al cargar las obras",
                                color = Color(0xFFE0E0E0),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Text(
                                text = state.message,
                                color = Color(0xFFAAAAAA),
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            androidx.compose.material3.Button(
                                onClick = { viewModel.refresh() },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD4AF37)
                                )
                            ) {
                                Text("Reintentar", color = Color(0xFF1A1A1A))
                            }
                        }
                    }
                }

                is GalleryUiState.Success -> {
                    // Mostrar obras
                    val artworks = state.artworks

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Obra Destacada",
                                color = Color(0xFFE0E0E0),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Light,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            FeaturedArtworkCard(artworks.first(), onArtworkClick = onArtworkClick)
                        }

                        if (artworks.size > 1) {
                            item {
                                Text(
                                    text = "Colecciones",
                                    color = Color(0xFFE0E0E0),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Light,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            val gridItems = artworks.drop(1).chunked(2)
                            gridItems.forEach { row ->
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        for (art in row) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                ArtworkCard(art, onArtworkClick = onArtworkClick)
                                            }
                                        }
                                        if (row.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ArtelyGalery",
            fontSize = 24.sp,
            fontWeight = FontWeight.Light,
            color = Color(0xFFD4AF37),
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun FeaturedArtworkCard(artwork: com.luis.artelyapp.model.Artwork, onArtworkClick: (String, String) -> Unit = { _, _ -> }) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onArtworkClick(artwork.id_ArtWork, artwork.id_Artist) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.background(
            Brush.linearGradient(listOf(Color(0xFF2A2A2A), Color(0xFF1E1E1E)))
        )) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFF333333)),
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
                        Text(text = "\"${artwork.Title}\"", color = Color(0xFFAAAAAA))
                    }
                } else {
                    Text(text = "\"${artwork.Title}\"", color = Color(0xFFAAAAAA))
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = artwork.Title, color = Color(0xFFE0E0E0), fontSize = 18.sp)
                Text(text = artwork.Technique, color = Color(0xFFD4AF37), fontSize = 14.sp)
                if (artwork.Description.isNotEmpty()) {
                    Text(
                        text = artwork.Description,
                        color = Color(0xFFAAAAAA),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 2
                    )
                }
                if (artwork.Price > 0) {
                    Text(
                        text = "$${String.format("%.2f", artwork.Price)}",
                        color = Color(0xFF4CAF50),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ArtworkCard(art: com.luis.artelyapp.model.Artwork, onArtworkClick: (String, String) -> Unit = { _, _ -> }) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable { onArtworkClick(art.id_ArtWork, art.id_Artist) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.background(Color(0xFF2A2A2A))) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFF333333)),
                contentAlignment = Alignment.Center
            ) {
                if (art.ImageUrl.isNotEmpty()) {
                    val file = File(art.ImageUrl)
                    if (file.exists()) {
                        AsyncImage(
                            model = file,
                            contentDescription = art.Title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = art.Title,
                            color = Color(0xFF777777),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                } else {
                    Text(
                        text = art.Title,
                        color = Color(0xFF777777),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = art.Title,
                    color = Color(0xFFE0E0E0),
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Text(
                    text = art.Technique,
                    color = Color(0xFFD4AF37),
                    fontSize = 12.sp,
                    maxLines = 1
                )
                if (art.Price > 0) {
                    Text(
                        text = "$${String.format("%.2f", art.Price)}",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    favoritesCount: Int = 0,
    onTabSelected: (Int) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFF1A1A1A))
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Crear lista dinámica de items según si hay favoritos
        val items = if (favoritesCount > 0) {
            listOf(
                "Inicio" to "🏠",
                "Chat" to "💬",
                "Favoritos" to "❤️",
                "Perfil" to "👤"
            )
        } else {
            listOf(
                "Inicio" to "🏠",
                "Chat" to "💬",
                "Perfil" to "👤"
            )
        }

        items.forEachIndexed { index, pair ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onTabSelected(index) }
            ) {
                // Solo mostrar el emoji sin badge
                Text(text = pair.second, fontSize = 18.sp)


                Text(
                    text = pair.first,
                    fontSize = 10.sp,
                    color = if (index == 0) Color(0xFFD4AF37) else Color(0xFF888888)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GalleryPreview() {
    GalleryScreen()
}
