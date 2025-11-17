package com.luis.artelyapp.ui.view

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
import androidx.compose.material3.Divider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luis.artelyapp.model.Chat
import com.luis.artelyapp.ui.view.viewmodel.ChatViewModel

@Composable
fun ChatView(
    onNavigateToMessage: (Int) -> Unit = {} // Recibe el id del chat
) {
    val viewModel: ChatViewModel = viewModel()
    val chats by viewModel.chats.collectAsState()

    Scaffold(
        topBar = { ChatListHeader() }
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(chats) { chat ->
                    ChatItem(
                        chat = chat,
                        onChatClick = { onNavigateToMessage(chat.id_Chat) }
                    )
                    Divider(color = Color(0xFF2A2A2A), thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun ChatListHeader() {
    Surface(color = Color(0xFF2A2A2A)) {
        Text(
            text = "Artely",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun ChatItem(
    chat: Chat,
    onChatClick: () -> Unit
) {
    val lastMessage = chat.messages.lastOrNull()
    val lastMessageText = lastMessage?.content ?: "No hay mensajes"
    val time = "Hoy" // Podrías calcular esto del timestamp del último mensaje

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChatClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFD4AF37)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = chat.userName.take(2).uppercase(),
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = chat.userName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = time,
                    color = Color(0xFF888888),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = lastMessageText,
                color = Color(0xFFAAAAAA),
                fontSize = 14.sp,
                maxLines = 2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    ChatView()
}