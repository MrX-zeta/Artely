package com.luis.artelyapp.ui.ArtistProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

// Clases de datos locales (ya que eliminaste el archivo view)
data class Artist(
    val id: Int,
    val email: String,
    val bio: String,
    val location: String,
    val role: String = "Artist"
)

data class ArtistStats(
    val totalArtworks: Int,
    val followers: Int,
    val following: Int = 0,
    val likes: Int = 0
)

data class Artwork(
    val id: Int,
    val title: String,
    val artist: String,
    val price: String? = null,
    val imageUrl: String = "",
    val isForSale: Boolean = false
)

enum class ProfileTab {
    GALLERY,
    FOR_SALE
}

// Colores de la paleta
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
    artistName: String = "Aristote",
    onBackClick: () -> Unit = {}
) {
    // Estado local para las pestañas
    var selectedTab by remember { mutableStateOf(ProfileTab.GALLERY) }

    // Datos de muestra usando las clases locales, adaptados al artista
    val sampleArtist = remember(artistName) {
        when (artistName) {
            "Vincent van Gogh" -> Artist(
                id = 2,
                email = "vincent@artely.com",
                bio = "Pintor postimpresionista neerlandés que figura entre las figuras más famosas e influyentes de la historia del arte occidental.",
                location = "Países Bajos"
            )
            "Leonardo da Vinci" -> Artist(
                id = 3,
                email = "leonardo@artely.com",
                bio = "Polímata del Renacimiento italiano. Fue a la vez pintor, anatomista, arquitecto, paleontólogo, botánico, escritor, escultor, filósofo, ingeniero, inventor, músico, poeta y urbanista.",
                location = "Italia"
            )
            "Miguel Ángel" -> Artist(
                id = 4,
                email = "michelangelo@artely.com",
                bio = "Arquitecto, escultor y pintor italiano renacentista, considerado uno de los más grandes artistas de la historia.",
                location = "Italia"
            )
            "Claude Monet" -> Artist(
                id = 5,
                email = "claude@artely.com",
                bio = "Pintor francés, uno de los creadores del impresionismo. El término impresionismo deriva del título de su obra Impresión, sol naciente.",
                location = "Francia"
            )
            "Wassily Kandinsky" -> Artist(
                id = 6,
                email = "wassily@artely.com",
                bio = "Pintor ruso, precursor de la abstracción en pintura y teórico del arte. Se considera que con él comienza la abstracción lírica.",
                location = "Rusia"
            )
            else -> Artist(
                id = 1,
                email = "aristote@artely.com",
                bio = "Artista visual especializado en retratos contemporáneos y arte figurativo",
                location = "París, Francia"
            )
        }
    }

    val sampleStats = remember(artistName) {
        when (artistName) {
            "Vincent van Gogh" -> ArtistStats(
                totalArtworks = 2100,
                followers = 15600,
                following = 23,
                likes = 45200
            )
            "Leonardo da Vinci" -> ArtistStats(
                totalArtworks = 156,
                followers = 25400,
                following = 5,
                likes = 78900
            )
            "Miguel Ángel" -> ArtistStats(
                totalArtworks = 89,
                followers = 18700,
                following = 12,
                likes = 56800
            )
            "Claude Monet" -> ArtistStats(
                totalArtworks = 892,
                followers = 12300,
                following = 45,
                likes = 34500
            )
            "Wassily Kandinsky" -> ArtistStats(
                totalArtworks = 456,
                followers = 8900,
                following = 67,
                likes = 23400
            )
            else -> ArtistStats(
                totalArtworks = 127,
                followers = 2300,
                following = 156,
                likes = 8945
            )
        }
    }

    val sampleArtworksGallery = remember(artistName) {
        when (artistName) {
            "Vincent van Gogh" -> listOf(
                Artwork(id = 1, title = "Noche Estrellada", artist = artistName, isForSale = false),
                Artwork(id = 2, title = "Los Girasoles", artist = artistName, isForSale = false),
                Artwork(id = 3, title = "Autorretrato", artist = artistName, isForSale = false)
            )
            "Leonardo da Vinci" -> listOf(
                Artwork(id = 1, title = "La Gioconda", artist = artistName, isForSale = false),
                Artwork(id = 2, title = "La Última Cena", artist = artistName, isForSale = false)
            )
            "Miguel Ángel" -> listOf(
                Artwork(id = 1, title = "David", artist = artistName, isForSale = false),
                Artwork(id = 2, title = "La Piedad", artist = artistName, isForSale = false)
            )
            "Claude Monet" -> listOf(
                Artwork(id = 1, title = "Impresión, sol naciente", artist = artistName, isForSale = false),
                Artwork(id = 2, title = "Nenúfares", artist = artistName, isForSale = false)
            )
            "Wassily Kandinsky" -> listOf(
                Artwork(id = 1, title = "Composición VIII", artist = artistName, isForSale = false),
                Artwork(id = 2, title = "Improvisación 28", artist = artistName, isForSale = false)
            )
            else -> listOf(
                Artwork(id = 1, title = "La Gioconda", artist = artistName, isForSale = false),
                Artwork(id = 2, title = "Retrato Contemporáneo", artist = artistName, isForSale = false)
            )
        }
    }

    val sampleArtworksForSale = remember(artistName) {
        listOf(
            Artwork(id = 3, title = "Obra Especial", artist = artistName, price = "$3,500", isForSale = true),
            Artwork(id = 4, title = "Edición Limitada", artist = artistName, price = "$1,200", isForSale = true)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(430.dp)
                .height(886.dp)
                .shadow(
                    elevation = 30.dp,
                    shape = RoundedCornerShape(40.dp),
                    ambientColor = Color.Black.copy(alpha = 0.5f),
                    spotColor = Color.Black.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(40.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(DarkBackground, DarkBackground),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(1f, 1f)
                    )
                )
                .border(
                    width = 12.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(40.dp)
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { TopBar(onBackClick = onBackClick) }
                item { ArtistHeader(sampleArtist, sampleStats) }
                item {
                    TabSection(
                        selectedTab = selectedTab,
                        onTabSelected = { tab -> selectedTab = tab }
                    )
                }
                item {
                    CollectionsSection(
                        artworks = when(selectedTab) {
                            ProfileTab.GALLERY -> sampleArtworksGallery
                            ProfileTab.FOR_SALE -> sampleArtworksForSale
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }

            BottomNavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Gradient overlay
        Box(
            modifier = Modifier
                .width(430.dp)
                .height(886.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AccentGold.copy(alpha = 0.03f),
                            Color.Transparent,
                            AccentGold.copy(alpha = 0.03f),
                            Color.Transparent
                        )
                    )
                )
        )
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

        Spacer(modifier = Modifier.width(16.dp))

        // Search Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔍",
                color = TextPrimary,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Heart Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "❤️",
                fontSize = 18.sp
            )
        }
    }

    HorizontalDivider(color = BorderColor, thickness = 1.dp)
}

@Composable
private fun ArtistHeader(
    artist: Artist,
    stats: ArtistStats
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AccentGold, Color(0xFFB8941F))
                            ),
                            CircleShape
                        )
                        .border(3.dp, AccentGold.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = artist.email.take(1).uppercase(),
                        color = DarkBackground,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = artist.email.split("@")[0].replaceFirstChar { it.uppercase() },
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = artist.bio,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 19.6.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "📍 ${artist.location}",
                        color = AccentGold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Card
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
                    StatItem("${stats.totalArtworks}", "OBRAS")
                    StatItem("${stats.followers}", "SEGUIDORES")
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
    artworks: List<Artwork>
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

        // LazyRow horizontal para mostrar las obras de arte
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(artworks) { artwork ->
                ArtPieceCard(artwork)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ArtPieceCard(artwork: Artwork) {
    Card(
        modifier = Modifier
            .width(280.dp) // Ancho fijo para que se vean completos
            .height(200.dp), // Aumentar altura para acomodar más texto
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Image placeholder
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
                Text(
                    text = artwork.title,
                    color = Color(0xFF777777),
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }

            // Card content
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = artwork.title,
                    color = TextLight,
                    fontSize = 14.sp,
                    maxLines = 2,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (artwork.isForSale && artwork.price != null) artwork.price else artwork.artist,
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(89.dp)
            .background(DarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem("🏠", "Inicio", isSelected = true)
            BottomNavItem("🔍", "Buscar", isSelected = false)
            BottomNavItem("🎨", "Crear", isSelected = false)
            BottomNavItem("👤", "Perfil", isSelected = false)
        }

        // Línea superior
        HorizontalDivider(
            color = BorderColor,
            thickness = 1.dp,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: String,
    label: String,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) AccentGold.copy(alpha = 0.1f) else Color.Transparent
    val textColor = if (isSelected) AccentGold else TextSecondary

    Column(
        modifier = Modifier
            .size(60.dp, 58.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = icon,
            color = textColor,
            fontSize = if (icon == "🔍" || icon == "🎨" || icon == "👤") 10.sp else 11.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ArtistProfilePreview() {
    ArtistProfileScreen()
}
