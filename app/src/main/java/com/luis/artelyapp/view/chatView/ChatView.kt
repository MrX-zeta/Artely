package com.luis.artelyapp.view.chatView

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luis.artelyapp.viewmodel.ChatViewModel
import com.luis.artelyapp.viewmodel.ChatDisplay
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.luis.artelyapp.viewmodel.ChatUiState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatView(
    viewModel: ChatViewModel = viewModel(),
    onNavigateToMessage: (String) -> Unit = {}, // Recibe el id del chat
    onNavigateToGallery: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Actualizar la lista de chats cada vez que se muestra esta pantalla
    // Esto asegura que el contador de mensajes no leídos se actualice
    androidx.compose.runtime.DisposableEffect(Unit) {
        viewModel.refresh()
        onDispose { }
    }

    Scaffold(
        containerColor = Color(0xFF1A1A1A),
        topBar = { ChatListHeader() },
        bottomBar = {
            BottomNavigationBar(
                onNavigateToGallery = onNavigateToGallery,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF1A1A1A))
        ) {
            Text(
                text = "Mensajes",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            when (val state = uiState) {
                is ChatUiState.Loading -> {
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
                                text = "Cargando chats...",
                                color = Color(0xFFAAAAAA),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                is ChatUiState.Empty -> {
                    // No hay chats
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
                                text = "💬",
                                fontSize = 64.sp
                            )
                            Text(
                                text = "No tienes conversaciones",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Text(
                                text = "Inicia una conversación con un artista para comenzar",
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

                is ChatUiState.Error -> {
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
                                text = "Error al cargar los chats",
                                color = Color.White,
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

                is ChatUiState.Success -> {
                    // Mostrar lista de chats
                    val chats = state.chats

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(chats) { chat ->
                            ChatItem(
                                chat = chat,
                                onChatClick = { onNavigateToMessage(chat.chat.id_Chat) }
                            )
                            HorizontalDivider(color = Color(0xFF2A2A2A), thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListHeader() {
    Surface(color = Color(0xFF2A2A2A)) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Spacer para status bar
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Artely",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun ChatItem(
    chat: ChatDisplay,
    onChatClick: () -> Unit
) {
    val lastMessage = chat.messages.lastOrNull()
    val lastMessageText = lastMessage?.content ?: "No hay mensajes"
    val time = "Hoy" // Aquí podrías formatear chat.lastMessageTime

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChatClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar - Mostrar foto de perfil o inicial
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    if (chat.profileImageUrl.isEmpty())
                        Color(0xFFD4AF37)
                    else
                        Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            if (chat.profileImageUrl.isNotEmpty()) {
                // Mostrar foto de perfil
                AsyncImage(
                    model = android.net.Uri.parse(chat.profileImageUrl),
                    contentDescription = "Foto de perfil de ${chat.userName}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Mostrar inicial del nombre
                Text(
                    text = chat.userName.take(2).uppercase(),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Columna Central: Nombre y Mensaje
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = chat.userName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = lastMessageText,
                color = if (chat.unreadCount > 0) Color.White else Color(0xFFAAAAAA), // Mensaje en blanco si no se ha leído
                fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal, // Negrita si no se ha leído
                fontSize = 14.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Columna Derecha: Hora y Badge
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = time,
                color = if (chat.unreadCount > 0) Color(0xFFD4AF37) else Color(0xFF888888),
                fontSize = 12.sp,
                fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Badge de mensajes no leídos
            if (chat.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD4AF37)), // Color dorado/amarillo de la app
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (chat.unreadCount > 9) "+9" else chat.unreadCount.toString(),
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onNavigateToGallery: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
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
        val items = listOf("Inicio" to "🏠", "Chat" to "💬", "Perfil" to "👤")
        val callbacks = listOf(onNavigateToGallery, {}, onNavigateToProfile)

        items.forEachIndexed { index, pair ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { callbacks[index]() }
            ) {
                Text(text = pair.second, fontSize = 18.sp)
                Text(
                    text = pair.first,
                    fontSize = 12.sp,
                    color = if (index == 1) Color(0xFFD4AF37) else Color(0xFF888888)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    ChatView()
}
