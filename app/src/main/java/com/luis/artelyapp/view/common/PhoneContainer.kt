package com.luis.artelyapp.view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PhoneContainer(content: @Composable () -> Unit) {
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1A1A1A), // Gris oscuro
            Color(0xFF2D2D2D)  // Gris medio oscuro
        )
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundBrush)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

