package com.luis.artelyapp.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Gold = Color(0xFFD4AF37)
private val Dark1 = Color(0xFF1A1A1A)
private val Dark2 = Color(0xFF2D2D2D)
private val Muted = Color(0xFF666666)
private val DarkText = Color(0xFF333333)

@Composable
fun WelcomeScreen(
    onExplore: () -> Unit = {},
    onGetStarted: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo oscuro principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(Dark1, Dark2)))
        ) {}

        // Fondo dorado solo en la mitad inferior; ahora contendrá las features para evitar overlap
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
                .background(Gold)
                .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 25.dp, bottomEnd = 25.dp))
        ) {
            // Colocamos las features dentro del área dorada, centradas y con padding superior para separarlas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FeatureItem(title = "Descubre Arte", subtitle = "Explora obras de artistas locales y emergentes", icon = "🖼️")
                FeatureItem(title = "Contacto Directo", subtitle = "Chatea directamente con los artistas", icon = "💬")
                FeatureItem(title = "Notificaciones", subtitle = "Recibe alertas de nuevas obras", icon = "🔔")
            }
        }

        // Tarjeta oscura superior con título y botones (ahora ocupa la mitad superior)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(Dark1, Dark2)))
                .padding(start = 30.dp, end = 30.dp, top = 20.dp, bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top // poner en top y luego un spacer para desplazar hacia abajo
            ) {
                Spacer(modifier = Modifier.height(120.dp)) // baja el bloque hacia abajo sin salir de la tarjeta (ajustado a 110.dp)

                Text(
                    text = "Artely",
                    fontSize = 42.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(35.dp))

                Text(
                    text = "Conecta con artistas locales y descubre obras únicas",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(25.dp))
                            .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(25.dp))
                            .background(Gold)
                            .clickable { onExplore() }
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(text = "Explorar", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.padding(10.dp))

                    // Comenzar (black button with gold text)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color.Black)
                            .clickable { onGetStarted() }
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(text = "Comenzar", color = Gold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // (Las features están dentro del Box dorado y ya no overlap con la tarjeta oscura)
    }
}

@Composable
private fun FeatureItem(title: String, subtitle: String, icon: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(Color.Black.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            // Usamos emoji como icono para evitar dependencias adicionales
            Text(text = icon, fontSize = 20.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            color = DarkText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            color = Muted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePreview() {
    Surface(color = MaterialTheme.colorScheme.background) {
        WelcomeScreen()
    }
}
