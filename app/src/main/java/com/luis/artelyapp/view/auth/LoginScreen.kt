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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luis.artelyapp.viewmodel.auth.LoginViewModel
import com.luis.artelyapp.viewmodel.auth.LoginUiState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLogin: () -> Unit = {},
    onBack: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val showPassword by viewModel.showPassword.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Limpiar campos del formulario cuando se muestra la pantalla de login
    // NOTA: NO hacer logout aquí, el logout ya se hizo desde UserProfileViewModel
    LaunchedEffect(Unit) {
        android.util.Log.d("LoginScreen", "🔄 LoginScreen mostrada - limpiando campos del formulario")
        viewModel.clearFields()
    }

    // Mostrar errores en Snackbar
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Error) {
            snackbarHostState.showSnackbar((uiState as LoginUiState.Error).message)
        }
    }

    val gold = Color(0xFFD4AF37)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D))))
        ,
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = gold)
                }
                Text(
                    text = "Artely",
                    color = gold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF333333)))

            Spacer(modifier = Modifier.height(60.dp))

            Text(text = "Inicio de Sesión", fontSize = 28.sp, fontWeight = FontWeight.Light, color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bienvenido de nuevo a nuestra comunidad de arte",
                fontSize = 15.sp,
                color = Color(0xFFAAAAAA),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Email
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Correo electrónico", color = Color(0xFFE0E0E0), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ingresa tu correo", color = Color(0xFF777777)) },
                    singleLine = true,
                    enabled = uiState !is LoginUiState.Loading,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Contraseña", color = Color(0xFFE0E0E0), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ingresa tu contraseña", color = Color(0xFF777777)) },
                    singleLine = true,
                    enabled = uiState !is LoginUiState.Loading,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (showPassword) "🙈" else "👁️",
                            color = gold,
                            modifier = Modifier.clickable { viewModel.togglePasswordVisibility() }
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush = Brush.linearGradient(listOf(Color(0xFFD4AF37), Color(0xFFB8941F))))
                    .clickable(enabled = uiState !is LoginUiState.Loading) {
                        viewModel.login(onSuccess = onLogin)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator(color = Color(0xFF1A1A1A), modifier = Modifier.height(24.dp))
                } else {
                    Text(text = "Iniciar Sesión", color = Color(0xFF1A1A1A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFF444444)))
                Text(text = "  o  ", color = Color(0xFF777777))
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFF444444)))
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "¿Aún no tienes una cuenta? ", color = Color(0xFFAAAAAA))
                Text(text = "Regístrate", color = Color(0xFFD4AF37), modifier = Modifier.clickable { onRegister() })
            }

            Spacer(modifier = Modifier.height(80.dp))
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

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Surface(color = MaterialTheme.colorScheme.background) {
        LoginScreen()
    }
}
