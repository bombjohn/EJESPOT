package com.example.ejespot.features.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.R
import com.example.ejespot.ui.theme.*

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
        Spacer(modifier = Modifier.height(48.dp))

        // Logo Oficial EjeSpot
        Image(
            painter = painterResource(id = R.drawable.ic_logo_ejespot),
            contentDescription = "Logo EjeSpot",
            modifier = Modifier.size(110.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Título con tipografía Fraunces Serif
        Text(
            text = "Bienvenido a Eje Spot",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = EjeSpotText
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtítulo con Manrope
        Text(
            text = "Inicia sesión para descubrir y compartir\nlugares",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = ManropeFontFamily,
                color = EjeSpotTextMuted,
                lineHeight = 20.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Campo Correo electrónico
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Correo electrónico",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = EjeSpotText
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                placeholder = {
                    Text(
                        "tú@correo.com",
                        fontFamily = ManropeFontFamily,
                        color = EjeSpotOutline
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Mail,
                        contentDescription = null,
                        tint = EjeSpotTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = EjeSpotOutline,
                    focusedBorderColor = EjeSpotPrimary
                ),
                textStyle = LocalTextStyle.current.copy(fontFamily = ManropeFontFamily),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Contraseña",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = EjeSpotText
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                placeholder = {
                    Text(
                        "••••••••••••",
                        fontFamily = ManropeFontFamily,
                        color = EjeSpotOutline
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = EjeSpotTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.onTogglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible)
                                Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (uiState.isPasswordVisible)
                                "Ocultar contraseña" else "Mostrar contraseña",
                            tint = EjeSpotTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (uiState.isPasswordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = EjeSpotOutline,
                    focusedBorderColor = EjeSpotPrimary
                ),
                textStyle = LocalTextStyle.current.copy(fontFamily = ManropeFontFamily),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ¿Olvidaste tu contraseña?
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(
                onClick = { /* TODO */ },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = EjeSpotPrimary,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }

        // Mensaje de error
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Botón Iniciar sesión (Verde Cafetero #3D6B4F con bordes redondeados)
        Button(
            onClick = { viewModel.onLogin() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = "Iniciar sesión",
                    fontFamily = ManropeFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Separador con texto "o continúa con"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = EjeSpotOutline)
            Text(
                text = "  o continúa con  ",
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = EjeSpotTextMuted
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = EjeSpotOutline)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Google Oficial
        SocialLoginButton(
            text = "Continuar con Google",
            iconDrawable = R.drawable.ic_google_logo
        ) { }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón Facebook Oficial
        SocialLoginButton(
            text = "Continuar con Facebook",
            iconDrawable = R.drawable.ic_facebook_logo
        ) { }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón Apple Oficial
        SocialLoginButton(
            text = "Continuar con Apple",
            iconDrawable = R.drawable.ic_apple_logo
        ) { }

        Spacer(modifier = Modifier.height(20.dp))

        // Explorar como invitado
        Text(
            text = "Explorar como invitado",
            color = EjeSpotPrimary,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable { viewModel.onLoginAsGuest() }
                .padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Footer: ¿No tienes cuenta? Regístrate como Turista
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "¿No tienes cuenta? ",
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                color = EjeSpotTextMuted
            )
            Text(
                text = "Regístrate como Turista",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = EjeSpotPrimary,
                modifier = Modifier.clickable { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SocialLoginButton(
    text: String,
    iconDrawable: Int,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
        border = BorderStroke(1.dp, EjeSpotOutline)
    ) {
        Icon(
            painter = painterResource(id = iconDrawable),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontFamily = ManropeFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = EjeSpotText
        )
    }
}
