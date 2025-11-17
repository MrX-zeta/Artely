package com.luis.artelyapp.ui.view

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luis.artelyapp.model.Message
import com.luis.artelyapp.ui.view.viewmodel.MessageViewModel

@Composable
fun MessageView(
    chatId: Int = 0
) {
    val viewModel: MessageViewModel = viewModel()
    val messages by viewModel.messages.collectAsState()
    val currentMessage by viewModel.currentMessage.collectAsState()

    Scaffold(
        topBar = { MessageHeader() },
        bottomBar = {
            MessageInput(
                currentMessage = currentMessage,
                onMessageChange = { viewModel.updateCurrentMessage(it) },
                onSendMessage = { viewModel.sendMessage() }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF1A1A1A)),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(
                    message = message,
                    isFromCurrentUser = viewModel.isMessageFromCurrentUser(message)
                )
            }
        }
    }
}

@Composable
fun MessageHeader() {
    Surface(color = Color(0xFF2A2A2A)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD4AF37)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FL",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Francisco Lopez",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "En línea",
                    color = Color(0xFF4CAF50),
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

        Text(
            text = "Ahora",
            color = Color(0xFF888888),
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun MessageInput(
    currentMessage: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Surface(color = Color(0xFF2A2A2A)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = currentMessage,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFF1A1A1A), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                decorationBox = { innerTextField ->
                    if (currentMessage.isEmpty()) {
                        Text(
                            text = "Escribir...",
                            color = Color(0xFF888888),
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSendMessage,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFD4AF37), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar mensaje",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun MessagePreview() {
    MessageView()
}