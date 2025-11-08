package com.luis.artelyapp.ui.auth

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    vm: RegisterViewModel = viewModel(),
    onRegistered: () -> Unit = {},
    onBack: () -> Unit = {},
    onLogin: () -> Unit = {}
) {
    val username by vm.username.collectAsState()
    val email by vm.email.collectAsState()
    val password by vm.password.collectAsState()
    val confirm by vm.confirmPassword.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    // Llamar al callback cuando success cambie a true
    LaunchedEffect(success) {
        if (success == true) {
            onRegistered()
            vm.clearSuccess()
        }
    }

    val scope = rememberCoroutineScope()

    // Estados locales para mostrar/ocultar contraseñas
    var showPassword by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Transparent)
        .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .verticalScroll(rememberScrollState())
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            // Header (back + centered title)
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "<-",
                    color = Color(0xFFD4AF37),
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onBack() }
                        .padding(start = 4.dp)
                )

                Text(
                    text = "Artely",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFD4AF37),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Titles
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Crear Cuenta", fontSize = 28.sp, fontWeight = FontWeight.Normal, color = Color(0xFFE0E0E0))
                Text(text = "Únete a nuestra comunidad de arte", fontSize = 15.sp, color = Color(0xFFAAAAAA))
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!error.isNullOrEmpty()) {
                Text(text = error!!, color = Color(0xFFFF6B6B), modifier = Modifier.padding(vertical = 8.dp))
            }

            // Helper to render labeled input with styled background
            @Composable
            fun LabeledInput(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String = "", visualTransformation: VisualTransformation = VisualTransformation.None) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = label, color = Color(0xFFE0E0E0), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth(),
                        placeholder = { Text(placeholder, color = Color(0xFF777777)) },
                        singleLine = true,
                        visualTransformation = visualTransformation,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            @Composable
            fun LabeledPasswordInput(label: String, value: String, onValueChange: (String) -> Unit, isVisible: Boolean, onToggle: () -> Unit, placeholder: String = "") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = label, color = Color(0xFFE0E0E0), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth(),
                        placeholder = { Text(placeholder, color = Color(0xFF777777)) },
                        singleLine = true,
                        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            Text(
                                text = if (isVisible) "🙈" else "👁️",
                                color = Color(0xFFD4AF37),
                                modifier = Modifier.clickable { onToggle() }
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Username
            LabeledInput(label = "Nombre de usuario", value = username, onValueChange = { vm.onUsernameChange(it) }, placeholder = "Ingresa tu nombre de usuario")
            Spacer(modifier = Modifier.height(20.dp))

            // Email
            LabeledInput(label = "Correo electrónico", value = email, onValueChange = { vm.onEmailChange(it) }, placeholder = "tu@email.com")
            Spacer(modifier = Modifier.height(20.dp))

            // Password
            LabeledPasswordInput(label = "Contraseña", value = password, onValueChange = { vm.onPasswordChange(it) }, isVisible = showPassword, onToggle = { showPassword = !showPassword }, placeholder = "Crea una contraseña segura")
            Spacer(modifier = Modifier.height(20.dp))

            // Confirm password
            LabeledPasswordInput(label = "Confirmar contraseña", value = confirm, onValueChange = { vm.onConfirmPasswordChange(it) }, isVisible = showConfirm, onToggle = { showConfirm = !showConfirm }, placeholder = "Repite tu contraseña")
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush = Brush.linearGradient(colors = listOf(Color(0xFFD4AF37), Color(0xFFB8941F))))
                    .clickable { scope.launch { vm.submit() } },
                contentAlignment = Alignment.Center
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color(0xFF1A1A1A), strokeWidth = 2.dp)
                } else {
                    Text(text = "Crear Cuenta", color = Color(0xFF1A1A1A), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF444444))
                Text(text = "  o  ", color = Color(0xFF777777))
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF444444))
            }

            Spacer(modifier = Modifier.height(28.dp)) // ajustado para centrar mejor

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "¿Ya tienes una cuenta? ", color = Color(0xFFAAAAAA))
                Text(text = "Iniciar Sesión", color = Color(0xFFD4AF37), modifier = Modifier.clickable { onLogin() })
            }

            if (success == true) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Registro exitoso", color = Color(0xFF8AFFA0))
            }
        }
    }
}
