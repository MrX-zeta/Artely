package com.luis.artelyapp.view.UserProfile

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding

// Colores de la paleta
private val DarkBackground = Color(0xFF1A1A1A)
private val SecondaryBackground = Color(0xFF2D2D2D)
private val CardBackground = Color(0xFF2A2A2A)
private val BorderColor = Color(0xFF333333)
private val AccentGold = Color(0xFFD4AF37)
private val TextSecondary = Color(0xFFAAAAAA)
private val TextPrimary = Color(0xFFFFFFFF)

// Data classes
data class UserProfileData(
    val name: String,
    val bio: String,
    val location: String,
    val avatarLetter: String,
    val artworksCount: Int,
    val followersCount: Int,
    val followingCount: Int
)

data class UserArtwork(
    val id: String,
    val title: String,
    val artist: String,
    val description: String,
    val imageUri: android.net.Uri?,
    val isForSale: Boolean = false,
    val price: String? = null
)

enum class UserProfileTab {
    GALLERY,
    FOR_SALE
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun UserProfileScreen(
    onBackClick: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onNavigateToUpload: () -> Unit = {},
    onEditArtwork: (String) -> Unit = {},
    onNavigateToGallery: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: UserProfileViewModel = viewModel()
) {
    // Solo cargar el perfil si no hay datos (primera carga)
    val uiState by viewModel.uiState.collectAsState()
    val userArtworks by viewModel.userArtworks.collectAsState()

    LaunchedEffect(viewModel) {
        // Solo recargar si el estado es Idle Y no hay obras cargadas
        if (uiState is UserProfileUiState.Idle || (uiState is UserProfileUiState.Empty && userArtworks.isEmpty())) {
            android.util.Log.d("UserProfileScreen", "🔄 Primera carga del perfil del usuario")
            viewModel.reloadProfile()
        } else {
            android.util.Log.d("UserProfileScreen", "✅ Perfil ya cargado (${userArtworks.size} obras), no se recarga")
        }
    }

    var selectedTab by remember { mutableStateOf(UserProfileTab.GALLERY) }
    val userProfileInfo by viewModel.userProfile.collectAsState()
    val isArtist by viewModel.isArtist.collectAsState()

    // Obtener estadísticas desde uiState (Firebase)
    val stats = when (val state = uiState) {
        is UserProfileUiState.Success -> Triple(state.artworksCount, state.followersCount, state.followingCount)
        else -> Triple(0, 0, 0)
    }

    // Datos del usuario desde el ViewModel
    val userName = userProfileInfo.name.trim()
    val initial = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    android.util.Log.d("UserProfileScreen", "👤 Nombre del usuario: '$userName'")
    android.util.Log.d("UserProfileScreen", "🔤 Inicial generada: '$initial'")

    val userProfile = UserProfileData(
        name = userName,
        bio = userProfileInfo.bio.trim(),
        location = userProfileInfo.location.trim(),
        avatarLetter = initial,
        artworksCount = stats.first,  // Usar datos de Firebase
        followersCount = stats.second,
        followingCount = stats.third
    )

    val galleryArtworks = userArtworks.filter { !it.isForSale }
    val forSaleArtworks = userArtworks.filter { it.isForSale }

    // Usar Scaffold para que el contenido ocupe todo el espacio y el bottomBar quede fijo
    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            BottomNavigationBar(
                onNavigateToGallery = onNavigateToGallery,
                onNavigateToChat = onNavigateToChat,
                onNavigateToProfile = { /* Ya estamos en perfil */ }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            item { TopBar(onBackClick = onBackClick) }
            item {
                UserHeader(
                    profile = userProfile,
                    profileImageUri = userProfileInfo.profileImageUri,
                    onEditProfile = onEditProfile,
                    isArtist = isArtist,
                    onLogout = {
                        viewModel.logout()
                        onLogout()
                    }
                )
            }

            // Solo mostrar tabs y colecciones si es artista
            if (isArtist) {
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
                        },
                        selectedTab = selectedTab,
                        onNavigateToUpload = onNavigateToUpload,
                        onEditArtwork = onEditArtwork
                    )
                }
            } else {
                // Para customers, mostrar mensaje informativo
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🎨",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Explora obras de arte",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Navega por la galería para descubrir\namazantes obras de arte",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
            // No spacer necesario; Scaffold gestiona el espacio con bottomBar
        }
    }
}

@Composable
@Suppress("UNUSED_PARAMETER")
private fun TopBar(
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
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
        }

        HorizontalDivider(color = BorderColor, thickness = 1.dp)
    }
}

@Composable
private fun UserHeader(
    profile: UserProfileData,
    profileImageUri: android.net.Uri? = null,
    onEditProfile: () -> Unit = {},
    isArtist: Boolean = true,
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
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
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                // Profile Avatar - mostrar foto si existe, sino la inicial
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(if (profileImageUri == null) AccentGold else Color.Transparent)
                        .border(3.dp, AccentGold.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = profile.avatarLetter,
                            color = Color.Black,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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

                    // Solo mostrar bio y ubicación si es artista
                    if (isArtist) {
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
                }

                // Columna de botones
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // Edit Profile Button
                    Button(
                        onClick = onEditProfile,
                        modifier = Modifier
                            .width(110.dp)
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

                    // Logout Button
                    Button(
                        onClick = onLogout,
                        modifier = Modifier
                            .width(110.dp)
                            .height(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B0000), // Rojo oscuro
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Text(
                            text = "Cerrar Sesión",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Solo mostrar estadísticas si es artista
            if (isArtist) {
                Spacer(modifier = Modifier.height(24.dp))

                // Stats Card - mostrar 0 cuando no hay datos
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
                        StatItem(profile.artworksCount.toString(), "OBRAS")
                        StatItem(profile.followersCount.toString(), "SEGUIDORES")
                        StatItem(profile.followingCount.toString(), "SIGUIENDO")
                    }
                }
            }
        }
        }

        HorizontalDivider(color = BorderColor, thickness = 1.dp)
    }
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
    artworks: List<UserArtwork>,
    selectedTab: UserProfileTab,
    onNavigateToUpload: () -> Unit = {},
    onEditArtwork: (String) -> Unit = {}
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

            // Add Post Button (ahora funcional)
            Button(
                onClick = onNavigateToUpload,
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
                        text = if (selectedTab == UserProfileTab.FOR_SALE) "💰" else "🎨",
                        fontSize = 48.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = when (selectedTab) {
                            UserProfileTab.FOR_SALE -> "No hay arte en venta"
                            UserProfileTab.GALLERY -> "Aún no tienes obras en galería"
                        },
                        color = TextSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (selectedTab) {
                            UserProfileTab.FOR_SALE -> "Marca tus obras como 'en venta' para que aparezcan aquí"
                            UserProfileTab.GALLERY -> "Comparte tu primera obra de arte"
                        },
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
                            UserArtworkCard(
                                artwork = artwork,
                                onEditClick = { onEditArtwork(artwork.id) }
                            )
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
private fun UserArtworkCard(
    artwork: UserArtwork,
    onEditClick: () -> Unit = {}
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Image area
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
                if (artwork.imageUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(artwork.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = artwork.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "Retrato",
                        color = Color(0xFF777777),
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    )
                }

                // Ícono de editar (lápiz) en la esquina superior derecha
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                    ) {
                        Text(
                            text = "✏️",
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Card content
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título de la obra
                Text(
                    text = artwork.title,
                    color = AccentGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
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
fun UserProfilePreview() {
    UserProfileScreen()
}
