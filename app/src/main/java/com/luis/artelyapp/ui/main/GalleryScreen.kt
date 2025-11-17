package com.luis.artelyapp.ui.main

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GalleryScreen() {
    // Datos de ejemplo locales (temporal hasta conectar ViewModel/Repositorio)
    val artworksSample = listOf(
        Artwork(1, "Noche Estrellada", "Vincent van Gogh", "Una de las obras más reconocidas de Van Gogh, pintada en 1889."),
        Artwork(2, "La Gioconda", "Leonardo da Vinci"),
        Artwork(3, "David", "Miguel Ángel"),
        Artwork(4, "Impresión, sol naciente", "Claude Monet"),
        Artwork(5, "Composición VIII", "Wassily Kandinsky"),
        Artwork(6, "Retrato Desconocido", "Artista Anónimo")
    )

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar()
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Header()

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
                    FeaturedArtworkCard(artworksSample.first())
                }

                item {
                    Text(
                        text = "Colecciones",
                        color = Color(0xFFE0E0E0),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                val gridItems = artworksSample.drop(1).chunked(2)
                gridItems.forEach { row ->
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (art in row) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ArtworkCard(art)
                                }
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
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
fun FeaturedArtworkCard(artwork: Artwork) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
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
                Text(text = "\"${artwork.title}\" - ${artwork.artist}", color = Color(0xFFAAAAAA))
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = artwork.title, color = Color(0xFFE0E0E0), fontSize = 18.sp)
                Text(text = artwork.artist, color = Color(0xFFD4AF37), fontSize = 14.sp)
                if (!artwork.description.isNullOrBlank()) {
                    Text(text = artwork.description, color = Color(0xFFAAAAAA), fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

@Composable
fun ArtworkCard(art: Artwork) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable { /* navegar a detalle */ },
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
                Text(text = art.title, color = Color(0xFF777777), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = art.title, color = Color(0xFFE0E0E0), fontSize = 14.sp)
                Text(text = art.artist, color = Color(0xFFD4AF37), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFF1A1A1A))
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cambiamos la etiqueta inferior de 'Buscar' a 'Chat' y mantenemos el icono de chat
        val items = listOf("Inicio" to "🏠", "Chat" to "💬", "Agregar" to "➕", "Perfil" to "👤")
        items.forEachIndexed { index, pair ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = pair.second, fontSize = 18.sp)
                Text(text = pair.first, fontSize = 12.sp, color = if (index == 0) Color(0xFFD4AF37) else Color(0xFF888888))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GalleryPreview() {
    GalleryScreen()
}
