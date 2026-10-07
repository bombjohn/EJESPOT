package com.example.ejespot.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Colores de la marca EjeSpot
private val EjeSpotGreen = Color(0xFF2D5A27)
private val EjeSpotBackground = Color(0xFFF5F0E8)
private val EjeSpotBorder = Color(0xFFDDD8CC)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Logo (círculo con iniciales mientras no hay logo real)
        Surface(
            shape = RoundedCornerShape(50),
            color = EjeSpotGreen,
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "ES",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Título
        Text(
            text = "Bienvenido a Eje Spot",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtítulo
        Text(
            text = "Inicia sesión para descubrir y compartir\nlugares",
            fontSize = 14.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de correo
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Correo electrónico",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                placeholder = { Text("tú@correo.com", color = Color(0xFFAAAAAA)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = Color(0xFF888888)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = EjeSpotBorder,
                    focusedBorderColor = EjeSpotGreen
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Contraseña",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                placeholder = { Text("••••••••••••", color = Color(0xFFAAAAAA)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF888888)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.onTogglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible)
                                Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (uiState.isPasswordVisible)
                                "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Color(0xFF888888)
                        )
                    }
                },
                visualTransformation = if (uiState.isPasswordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = EjeSpotBorder,
                    focusedBorderColor = EjeSpotGreen
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ¿Olvidaste tu contraseña?
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = { /* TODO */ }) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = EjeSpotGreen,
                    fontSize = 13.sp
                )
            }
        }

        // Mensaje de error
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Botón Iniciar sesión
        Button(
            onClick = { viewModel.onLogin() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EjeSpotGreen),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = "Iniciar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Separador
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = EjeSpotBorder)
            Text(
                text = "  o continúa con  ",
                fontSize = 13.sp,
                color = Color(0xFF888888)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = EjeSpotBorder)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Google
        SocialLoginButton(text = "Continuar con Google", emoji = "G") { }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón Facebook
        SocialLoginButton(text = "Continuar con Facebook", emoji = "f") { }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón Apple
        SocialLoginButton(text = "Continuar con Apple", emoji = "") { }

        Spacer(modifier = Modifier.height(20.dp))

        // Explorar como invitado
        Text(
            text = "Explorar como invitado",
            color = EjeSpotGreen,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { viewModel.onLoginAsGuest() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ¿No tienes cuenta?
        Row {
            Text(
                text = "¿No tienes cuenta? ",
                fontSize = 14.sp,
                color = Color(0xFF555555)
            )
            Text(
                text = "Regístrate como Turista",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.clickable { /* TODO: Navegar a registro */ }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SocialLoginButton(
    text: String,
    emoji: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
    ) {
        Text(
            text = emoji,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color(0xFF333333)
        )
    }
}
