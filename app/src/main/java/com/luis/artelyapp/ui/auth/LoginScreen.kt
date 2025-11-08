package com.luis.artelyapp.ui.auth

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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

@Composable
fun LoginScreen(
    onLogin: () -> Unit = {},
    onBack: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }

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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", tint = gold)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Artely", color = gold, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.weight(1f))
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

            // Username
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Nombre de usuario", color = Color(0xFFE0E0E0), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ingresa tu nombre de usuario", color = Color(0xFF777777)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Contraseña", color = Color(0xFFE0E0E0), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ingresa tu contraseña", color = Color(0xFF777777)) },
                    singleLine = true,
                    visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (showPassword.value) "🙈" else "👁️",
                            color = gold,
                            modifier = Modifier.clickable { showPassword.value = !showPassword.value }
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
                    .clickable { onLogin() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Iniciar Sesión", color = Color(0xFF1A1A1A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Surface(color = MaterialTheme.colorScheme.background) {
        LoginScreen()
    }
}
