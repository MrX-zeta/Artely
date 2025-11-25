package com.luis.artelyapp.view.EditArtwork

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
fun EditArtworkScreen(
    artworkId: Int,
    currentTitle: String,
    currentDescription: String,
    currentImageUri: Uri?,
    currentIsForSale: Boolean,
    currentPrice: String? = null,
    onBackClick: () -> Unit = {},
    onSaveChanges: (artworkId: Int, title: String, description: String, imageUri: Uri?, isForSale: Boolean, price: String?) -> Unit = { _, _, _, _, _, _ -> }
) {
    var title by remember { mutableStateOf(currentTitle) }
    var description by remember { mutableStateOf(currentDescription) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(currentImageUri) }
    var isForSale by remember { mutableStateOf(currentIsForSale) }
    var price by remember { mutableStateOf(currentPrice ?: "") }

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
            .fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            shape = RoundedCornerShape(0.dp),
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
                        .height(80.dp)
                        .background(darkGray)
                        .padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 10.dp),
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
                        text = "Editar Obra",
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
                                    contentDescription = "Imagen de la obra",
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
                                text = "Cambiar imagen",
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

                    // Sale Status Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "ESTADO DE VENTA",
                            color = goldenColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        )

                        // Switch for sale status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "¿Está en venta?",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Text(
                                    text = if (isForSale) "Esta obra aparecerá en 'Arte en Venta'" else "Esta obra aparecerá en 'Galería'",
                                    color = lightGray,
                                    fontSize = 13.sp
                                )
                            }

                            Switch(
                                checked = isForSale,
                                onCheckedChange = {
                                    isForSale = it
                                    if (!it) price = ""
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = goldenColor,
                                    checkedTrackColor = goldenColor.copy(alpha = 0.3f),
                                    uncheckedThumbColor = lightGray,
                                    uncheckedTrackColor = mediumGray
                                )
                            )
                        }

                        // Price Input (solo visible cuando isForSale es true)
                        if (isForSale) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "PRECIO",
                                    color = goldenColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    letterSpacing = 0.5.sp
                                )

                                OutlinedTextField(
                                    value = price,
                                    onValueChange = { newPrice ->
                                        // Solo permitir números y punto decimal
                                        if (newPrice.isEmpty() || newPrice.matches(Regex("^\\d*\\.?\\d*$"))) {
                                            price = newPrice
                                        }
                                    },
                                    placeholder = {
                                        Text(
                                            text = "Ej: 150.00",
                                            color = lightGray,
                                            fontSize = 15.sp
                                        )
                                    },
                                    leadingIcon = {
                                        Text(
                                            text = "$",
                                            color = goldenColor,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(start = 12.dp)
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
                                    shape = RoundedCornerShape(6.dp),
                                    singleLine = true
                                )
                            }
                        }
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

                        // Save Button (Yellow/Golden)
                        Button(
                            onClick = {
                                val priceToSend = if (isForSale && price.isNotBlank()) price else null
                                val isValidForSale = !isForSale || price.isNotBlank()
                                if (title.isNotBlank() && description.isNotBlank() && selectedImageUri != null && isValidForSale) {
                                    onSaveChanges(artworkId, title, description, selectedImageUri, isForSale, priceToSend)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = goldenColor
                            ),
                            shape = RoundedCornerShape(6.dp),
                            enabled = title.isNotBlank() && description.isNotBlank() && selectedImageUri != null &&
                                      (!isForSale || price.isNotBlank())
                        ) {
                            Text(
                                text = "Guardar cambios",
                                color = darkGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }
}

