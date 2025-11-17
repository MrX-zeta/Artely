package com.luis.artelyapp.ui.UserProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

// Colores de la paleta
private val DarkBackground = Color(0xFF1A1A1A)
private val SecondaryBackground = Color(0xFF2D2D2D)
private val CardBackground = Color(0xFF2A2A2A)
private val BorderColor = Color(0xFF333333)
private val AccentGold = Color(0xFFD4AF37)
private val TextSecondary = Color(0xFFAAAAAA)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextLight = Color(0xFFE0E0E0)

// Data classes
data class UserProfileData(
    val name: String,
    val bio: String,
    val location: String,
    val avatarLetter: String,
    val artworksCount: Int,
    val followersCount: String,
    val followingCount: Int
)

data class UserArtwork(
    val id: Int,
    val title: String,
    val artist: String,
    val price: String? = null,
    val isForSale: Boolean = false
)

enum class UserProfileTab {
    GALLERY,
    FOR_SALE
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun UserProfileScreen(
    onBackClick: () -> Unit = {},
    onEditProfile: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(UserProfileTab.GALLERY) }

    // Datos de ejemplo del usuario
    val userProfile = UserProfileData(
        name = "Aristote",
        bio = "Artista visual especializado en retratos\ncontemporáneos y arte figurativo",
        location = "París, Francia",
        avatarLetter = "A",
        artworksCount = 127,
        followersCount = "2.3K",
        followingCount = 38
    )

    val galleryArtworks = listOf<UserArtwork>() // Temporalmente vacío para probar
    // val galleryArtworks = listOf(
    //     UserArtwork(1, "La Gioconda", "Leonardo da Vinci"),
    //     UserArtwork(2, "Retrato Contemporáneo", "Aristote"),
    //     UserArtwork(3, "Noche Estrellada", "Vincent van Gogh"),
    //     UserArtwork(4, "Las Meninas", "Diego Velázquez"),
    //     UserArtwork(5, "La Persistencia de la Memoria", "Salvador Dalí")
    // )

    val forSaleArtworks = listOf<UserArtwork>() // Temporalmente vacío para probar
    // val forSaleArtworks = listOf(
    //     UserArtwork(6, "Reflejos", "Aristote", "$2,100", true),
    //     UserArtwork(7, "Autorretrato Moderno", "Pablo Picasso", "$3,500", true),
    //     UserArtwork(8, "Composición en Azul", "Wassily Kandinsky", "$1,800", true)
    // )

    // Usar Scaffold para que el contenido ocupe todo el espacio y el bottomBar quede fijo
    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            BottomNavigationBar()
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item { TopBar(onBackClick = onBackClick) }
            item {
                UserHeader(
                    profile = userProfile,
                    onEditProfile = onEditProfile
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
                        UserProfileTab.GALLERY -> galleryArtworks
                        UserProfileTab.FOR_SALE -> forSaleArtworks
                    }
                )
            }
            // No spacer necesario; Scaffold gestiona el espacio con bottomBar
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
private fun UserHeader(
    profile: UserProfileData,
    onEditProfile: () -> Unit = {}
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
                        .border(3.dp, AccentGold.copy(alpha = 0.3f), CircleShape)
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.avatarLetter,
                        color = DarkBackground,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = profile.name,
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = profile.bio,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 19.6.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "📍 ${profile.location}",
                        color = AccentGold,
                        fontSize = 13.sp
                    )
                }

                // Edit Profile Button
                Button(
                    onClick = onEditProfile,
                    modifier = Modifier
                        .width(100.dp)
                        .height(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text(
                        text = "Editar Perfil",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
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
                    StatItem("${profile.artworksCount}", "OBRAS")
                    StatItem(profile.followersCount, "SEGUIDORES")
                    StatItem("${profile.followingCount}", "SIGUIENDO")
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
    selectedTab: UserProfileTab,
    onTabSelected: (UserProfileTab) -> Unit
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
                .clickable { onTabSelected(UserProfileTab.GALLERY) },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Galería",
                    color = if (selectedTab == UserProfileTab.GALLERY) AccentGold else TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                if (selectedTab == UserProfileTab.GALLERY) {
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
                .clickable { onTabSelected(UserProfileTab.FOR_SALE) },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Arte en venta",
                    color = if (selectedTab == UserProfileTab.FOR_SALE) AccentGold else TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                if (selectedTab == UserProfileTab.FOR_SALE) {
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
    artworks: List<UserArtwork>
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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

            // Add Post Button (sin funcionalidad pero con colores originales)
            Button(
                onClick = { /* No hace nada */ },
                modifier = Modifier
                    .size(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGold, // Color original
                    contentColor = Color.Black   // Color original
                ),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "➕",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar mensaje si no hay artworks o mostrar grid vertical
        if (artworks.isEmpty()) {
            // Mensaje cuando no hay colecciones
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🎨",
                        fontSize = 48.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Aún no tienes colecciones",
                        color = TextSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Comparte tu primera obra de arte",
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Grid vertical de obras de arte
            val chunkedArtworks = artworks.chunked(2)

            chunkedArtworks.forEach { rowArtworks ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowArtworks.forEach { artwork ->
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            UserArtworkCard(artwork)
                        }
                    }

                    // Si solo hay un elemento en la fila, agregar espaciador
                    if (rowArtworks.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun UserArtworkCard(artwork: UserArtwork) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp), // Mantener altura para acomodar más texto
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
                    text = "Retrato",
                    color = Color(0xFF777777),
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic
                )
            }

            // Card content
            Column(
                modifier = Modifier
                    .padding(12.dp) // Aumentar padding
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp) // Más espacio entre elementos
            ) {
                Text(
                    text = artwork.title,
                    color = TextLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2, // Permitir 2 líneas para el título
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                if (artwork.isForSale && artwork.price != null) {
                    Text(
                        text = artwork.price,
                        color = AccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = artwork.artist,
                        color = AccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 2, // Permitir 2 líneas para nombres largos
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
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
            BottomNavItem("🏠", "Inicio", isSelected = false)
            BottomNavItem("🔍", "Buscar", isSelected = false)
            BottomNavItem("🎨", "Crear", isSelected = false)
            BottomNavItem("👤", "Perfil", isSelected = true)
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
fun UserProfilePreview() {
    UserProfileScreen()
}
