package com.luis.artelyapp.view.auth

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luis.artelyapp.viewmodel.auth.RegisterViewModel
import com.luis.artelyapp.viewmodel.auth.RegisterUiState

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegistered: () -> Unit = {},
    onBack: () -> Unit = {},
    onLogin: () -> Unit = {}
) {
    val username by viewModel.username.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirm by viewModel.confirmPassword.collectAsState()
    val userType by viewModel.userType.collectAsState()
    val showPassword by viewModel.showPassword.collectAsState()
    val showConfirm by viewModel.showConfirmPassword.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var expanded by remember { mutableStateOf(false) }
    val userTypes = listOf("Comprador", "Artista")

    // Limpiar estado cuando se muestra la pantalla
    LaunchedEffect(Unit) {
        android.util.Log.d("RegisterScreen", "🔄 RegisterScreen mostrada - limpiando estado anterior")
        viewModel.clearSuccess()
        viewModel.resetState()
        viewModel.clearFields() // Limpiar también los campos del formulario
        android.util.Log.d("RegisterScreen", "✅ Estado y campos limpiados completamente")
    }

    // Mostrar errores en Snackbar
    LaunchedEffect(uiState) {
        when (uiState) {
            is RegisterUiState.Error -> {
                android.util.Log.d("RegisterScreen", "❌ Error: ${(uiState as RegisterUiState.Error).message}")
                snackbarHostState.showSnackbar((uiState as RegisterUiState.Error).message)
            }
            else -> {
                android.util.Log.d("RegisterScreen", "📋 Estado actual: ${uiState.javaClass.simpleName}")
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
        // Top Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Titles with back arrow
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "←",
                    color = Color(0xFFD4AF37),
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onBack() }
                )
                Text(
                    text = "Crear Cuenta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Únete a nuestra comunidad de arte", fontSize = 13.sp, color = Color(0xFFAAAAAA))

            Spacer(modifier = Modifier.height(10.dp))

            // Username
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Nombre de usuario", color = Color(0xFFE0E0E0), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ingresa tu nombre de usuario", color = Color(0xFF777777), fontSize = 13.sp) },
                    singleLine = true,
                    enabled = uiState !is RegisterUiState.Loading,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedContainerColor = Color(0xFF2A2A2A),
                        disabledContainerColor = Color(0xFF2A2A2A),
                        focusedIndicatorColor = Color(0xFFD4AF37),
                        unfocusedIndicatorColor = Color(0xFF555555),
                        cursorColor = Color(0xFFD4AF37)
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Email
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Correo electrónico", color = Color(0xFFE0E0E0), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("tu@email.com", color = Color(0xFF777777), fontSize = 13.sp) },
                    singleLine = true,
                    enabled = uiState !is RegisterUiState.Loading,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedContainerColor = Color(0xFF2A2A2A),
                        disabledContainerColor = Color(0xFF2A2A2A),
                        focusedIndicatorColor = Color(0xFFD4AF37),
                        unfocusedIndicatorColor = Color(0xFF555555),
                        cursorColor = Color(0xFFD4AF37)
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Menú desplegable para tipo de usuario
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Tipo de usuario", color = Color(0xFFE0E0E0), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = uiState !is RegisterUiState.Loading) {
                            expanded = !expanded
                        }
                ) {
                    OutlinedTextField(
                        value = userType,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Selecciona tipo de usuario", color = Color(0xFF777777), fontSize = 13.sp) },
                        singleLine = true,
                        readOnly = true,
                        enabled = false, // Deshabilitado para que el clic lo maneje el Box padre
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color(0xFFD4AF37)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = Color.White,
                            disabledContainerColor = Color(0xFF2A2A2A),
                            disabledIndicatorColor = Color(0xFF555555),
                            disabledPlaceholderColor = Color(0xFF777777),
                            disabledTrailingIconColor = Color(0xFFD4AF37)
                        )
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF3A3A3A))
                    ) {
                        userTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type, color = Color.White, fontSize = 14.sp) },
                                onClick = {
                                    viewModel.onUserTypeChange(type)
                                    expanded = false
                                },
                                modifier = Modifier.background(
                                    if (type == userType) Color(0xFF4A4A4A) else Color(0xFF3A3A3A)
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Password
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Contraseña", color = Color(0xFFE0E0E0), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Crea una contraseña segura", color = Color(0xFF777777), fontSize = 13.sp) },
                    singleLine = true,
                    enabled = uiState !is RegisterUiState.Loading,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (showPassword) "🙈" else "👁️",
                            color = Color(0xFFD4AF37),
                            modifier = Modifier.clickable { viewModel.togglePasswordVisibility() }
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedContainerColor = Color(0xFF2A2A2A),
                        disabledContainerColor = Color(0xFF2A2A2A),
                        focusedIndicatorColor = Color(0xFFD4AF37),
                        unfocusedIndicatorColor = Color(0xFF555555),
                        cursorColor = Color(0xFFD4AF37)
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Confirm password
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Confirmar contraseña", color = Color(0xFFE0E0E0), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Repite tu contraseña", color = Color(0xFF777777), fontSize = 13.sp) },
                    singleLine = true,
                    enabled = uiState !is RegisterUiState.Loading,
                    visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (showConfirm) "🙈" else "👁️",
                            color = Color(0xFFD4AF37),
                            modifier = Modifier.clickable { viewModel.toggleConfirmPasswordVisibility() }
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedContainerColor = Color(0xFF2A2A2A),
                        disabledContainerColor = Color(0xFF2A2A2A),
                        focusedIndicatorColor = Color(0xFFD4AF37),
                        unfocusedIndicatorColor = Color(0xFF555555),
                        cursorColor = Color(0xFFD4AF37)
                    )
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Register button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush = Brush.linearGradient(colors = listOf(Color(0xFFD4AF37), Color(0xFFB8941F))))
                    .clickable(enabled = uiState !is RegisterUiState.Loading) {
                        viewModel.register(onSuccess = onRegistered)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState is RegisterUiState.Loading) {
                    CircularProgressIndicator(color = Color(0xFF1A1A1A), strokeWidth = 2.dp)
                } else {
                    Text(text = "Crear Cuenta", color = Color(0xFF1A1A1A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divisor y texto de login
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF444444))
                Text(text = "  o  ", color = Color(0xFF777777), fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF444444))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "¿Ya tienes una cuenta? ", color = Color(0xFFAAAAAA), fontSize = 16.sp)
                Text(text = "Iniciar Sesión", color = Color(0xFFD4AF37), fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onLogin() })
            }
        }
        }

        // overlay dorado sutil que cubre hasta el fondo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.65f to Color.Transparent,
                            0.85f to Color(0x10D4AF37),
                            1.0f to Color(0x22D4AF37)
                        )
                    )
                )
        )

        // Snackbar para errores
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}
