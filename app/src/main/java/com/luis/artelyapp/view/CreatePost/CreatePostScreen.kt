package com.luis.artelyapp.view.CreatePost

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun CreatePostScreen(
    onBackClick: () -> Unit = {},
    onPublishPost: (String, String) -> Unit = { _, _ -> }
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Arte Digital") }

    val categories = listOf("Arte Digital", "Pintura", "Escultura", "Fotografía", "Dibujo", "Otros")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(DarkBackground, SecondaryBackground)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Bar
            TopBar(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Crear Nueva Publicación",
                color = AccentGold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Image Upload Area
            ImageUploadArea()

            Spacer(modifier = Modifier.height(24.dp))

            // Title Input
            Text(
                text = "Título de la obra",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BasicTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextPrimary,
                    fontSize = 16.sp
                ),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(
                            text = "Escribe el título de tu obra...",
                            color = TextSecondary,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Category Selection
            Text(
                text = "Categoría",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            CategorySelector(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Description Input
            Text(
                text = "Descripción",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BasicTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(CardBackground, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextPrimary,
                    fontSize = 14.sp
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (description.isEmpty()) {
                            Text(
                                text = "Describe tu obra de arte, técnicas utilizadas, inspiración...",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Publish Button
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        onPublishPost(title, description)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(25.dp),
                enabled = title.isNotEmpty()
            ) {
                Text(
                    text = "Publicar Obra",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable { onBackClick() }
                .background(CardBackground),
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
            text = "Nuevo Post",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ImageUploadArea() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(CardBackground, RoundedCornerShape(12.dp))
            .border(2.dp, BorderColor, RoundedCornerShape(12.dp))
            .clickable { /* Handle image upload */ },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📷",
                fontSize = 48.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Toca para subir imagen",
                color = AccentGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "JPG, PNG hasta 10MB",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun CategorySelector(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories.size) { index ->
            val category = categories[index]
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
                    .height(40.dp)
                    .background(
                        if (isSelected) AccentGold else CardBackground,
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        1.dp,
                        if (isSelected) AccentGold else BorderColor,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onCategorySelected(category) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.Black else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePostPreview() {
    CreatePostScreen()
}
