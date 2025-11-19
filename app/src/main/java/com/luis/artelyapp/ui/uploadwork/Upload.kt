package com.luis.artelyapp.ui.uploadwork

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Upload(
    onBackClick: () -> Unit = {},
    onPublishWork: (title: String, description: String, imageUri: Uri?) -> Unit = { _, _, _ -> }
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Launcher para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val goldenColor = Color(0xFFD4AF37)
    val darkGray = Color(0xFF1A1A1A)
    val mediumGray = Color(0xFF333333)
    val lightGray = Color(0xFF666666)

    Box(
        modifier = Modifier
            .width(430.dp)
            .height(886.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D)),
                    start = Offset(0f, 0f),
                    end = Offset(1f, 1f)
                )
            )
            .border(12.dp, Color.Black, RoundedCornerShape(40.dp))
            .shadow(30.dp, RoundedCornerShape(40.dp))
            .padding(12.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .shadow(30.dp, RoundedCornerShape(40.dp)),
            shape = RoundedCornerShape(40.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242424))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(59.dp)
                        .background(darkGray)
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(end = 15.dp)
                    ) {
                        Text(
                            text = "←",
                            color = goldenColor.copy(alpha = 0.83f),
                            fontSize = 24.sp
                        )
                    }

                    Text(
                        text = "Subir Obra",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Image Upload Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        // Image Preview Area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(215.dp)
                                .background(mediumGray, RoundedCornerShape(8.dp))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                // Mostrar imagen seleccionada
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(selectedImageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Imagen seleccionada",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Mostrar placeholder
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = "Add image",
                                        tint = lightGray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Toca para añadir imagen",
                                        color = lightGray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        // Select Image Button
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .border(1.dp, goldenColor, RoundedCornerShape(6.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = mediumGray
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (selectedImageUri != null) "Cambiar imagen" else "Seleccionar imagen",
                                color = goldenColor,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Title Input
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "TÍTULO DE LA OBRA",
                            color = goldenColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = {
                                Text(
                                    text = "Ej: Retrato de mujer",
                                    color = lightGray,
                                    fontSize = 15.sp
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = mediumGray,
                                unfocusedBorderColor = mediumGray,
                                focusedContainerColor = darkGray,
                                unfocusedContainerColor = darkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(6.dp)
                        )
                    }

                    // Description Input
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "DESCRIPCIÓN",
                            color = goldenColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = {
                                Text(
                                    text = "Describe tu obra, técnica utilizada, inspiración...",
                                    color = lightGray,
                                    fontSize = 15.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = mediumGray,
                                unfocusedBorderColor = mediumGray,
                                focusedContainerColor = darkGray,
                                unfocusedContainerColor = darkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(6.dp),
                            maxLines = 5
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel Button
                        Button(
                            onClick = onBackClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = mediumGray
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Cancelar",
                                color = Color.White,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Publish Button (Yellow/Golden)
                        Button(
                            onClick = {
                                if (title.isNotBlank() && description.isNotBlank() && selectedImageUri != null) {
                                    onPublishWork(title, description, selectedImageUri)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = goldenColor
                            ),
                            shape = RoundedCornerShape(6.dp),
                            enabled = title.isNotBlank() && description.isNotBlank() && selectedImageUri != null
                        ) {
                            Text(
                                text = "Publicar obra",
                                color = darkGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}