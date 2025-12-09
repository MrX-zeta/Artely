package com.luis.artelyapp.view.messageView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.CircularProgressIndicator
import com.luis.artelyapp.model.Message
import com.luis.artelyapp.viewmodel.MessageViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@Composable
fun MessageView(
    chatId: String = "",
    onBackClick: () -> Unit = {}
) {
    val viewModel: MessageViewModel = viewModel()
    val messages by viewModel.messages.collectAsState()
    val currentMessage by viewModel.currentMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val otherUser by viewModel.otherUser.collectAsState()
    val isOtherUserOnline by viewModel.isOtherUserOnline.collectAsState()

    // Cargar mensajes cuando cambie el chatId
    LaunchedEffect(chatId) {
        viewModel.loadMessagesForChat(chatId)
    }

    Scaffold(
        containerColor = Color(0xFF1A1A1A),
        topBar = {
            MessageHeader(
                otherUser = otherUser,
                isOnline = isOtherUserOnline,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            MessageInput(
                currentMessage = currentMessage,
                onMessageChange = { viewModel.updateCurrentMessage(it) },
                onSendMessage = { viewModel.sendMessage() }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF1A1A1A))
        ) {
            if (isLoading && messages.isEmpty()) {
                // Mostrar indicador de carga si está cargando y no hay mensajes
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFD4AF37)
                )
            } else if (messages.isEmpty()) {
                // Mostrar mensaje cuando no hay mensajes
                Text(
                    text = "Aún no hay mensajes. ¡Saluda!",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFAAAAAA),
                    textAlign = TextAlign.Center
                )
            } else {
                // Lista de mensajes
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true // Para mostrar los mensajes desde abajo
                ) {
                    items(messages.reversed()) { message ->
                        MessageBubble(
                            message = message,
                            isFromCurrentUser = message.senderId == viewModel.currentUserId
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageHeader(
    otherUser: com.luis.artelyapp.viewmodel.OtherUser? = null,
    isOnline: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    Surface(
        color = Color(0xFF2A2A2A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flecha de regreso
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Text(
                    text = "←",
                    color = Color(0xFFD4AF37),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Avatar - Mostrar foto de perfil o inicial
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (otherUser == null || otherUser.profileImageUrl.isEmpty())
                            Color(0xFFD4AF37)
                        else
                            Color.Transparent
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (otherUser != null && otherUser.profileImageUrl.isNotEmpty()) {
                    // Mostrar foto de perfil
                    AsyncImage(
                        model = android.net.Uri.parse(otherUser.profileImageUrl),
                        contentDescription = "Foto de perfil de ${otherUser.userName}",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Mostrar inicial del nombre
                    val initial = otherUser?.userName?.trim()?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
                    Text(
                        text = initial,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = otherUser?.userName ?: "Cargando...",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isOnline) "En línea" else "Desconectado",
                    color = if (isOnline) Color(0xFF4CAF50) else Color(0xFF888888),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isFromCurrentUser: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isFromCurrentUser) 16.dp else 4.dp,
                        bottomEnd = if (isFromCurrentUser) 4.dp else 16.dp
                    )
                )
                .background(
                    // Color amarillo (dorado) para mensajes propios del usuario
                    // Color gris oscuro para mensajes del otro usuario
                    if (isFromCurrentUser) Color(0xFFD4AF37) else Color(0xFF2A2A2A)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.content,
                color = if (isFromCurrentUser) Color.Black else Color.White,
                fontSize = 14.sp
            )
        }

        // Indicador de leído
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Ahora",
                color = Color(0xFF888888),
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            if (isFromCurrentUser) {
                Text(
                    text = if (message.isRead) "✓✓" else "✓",
                    color = if (message.isRead) Color(0xFF34B7F1) else Color(0xFF888888), // Azul si está leído, gris si no
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MessageInput(
    currentMessage: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Surface(
        color = Color(0xFF1A1A1A),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = currentMessage,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .background(Color(0xFF2C2C2C), shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 16.sp),
                decorationBox = { innerTextField ->
                    if (currentMessage.isEmpty()) {
                        Text("Escribir...", color = Color(0xFF888888))
                    }
                    innerTextField()
                }
            )
            IconButton(
                onClick = onSendMessage,
                enabled = currentMessage.isNotBlank(),
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (currentMessage.isNotBlank()) Color(0xFFD4AF37) else Color(0xFF4A4A4A))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar mensaje",
                    tint = if (currentMessage.isNotBlank()) Color.Black else Color(0xFF888888)
                )
            }
        }
    }
}

@Composable
fun MessagePreview() {
    MessageView()
}
