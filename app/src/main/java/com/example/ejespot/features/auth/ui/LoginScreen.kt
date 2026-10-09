package com.example.ejespot.features.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
        Spacer(modifier = Modifier.height(36.dp))

        // Logo Oficial EjeSpot
        Image(
            painter = painterResource(id = R.drawable.ic_logo_ejespot),
            contentDescription = "Logo EjeSpot",
            modifier = Modifier.size(130.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Título con Fraunces Serif
        Text(
            text = "Bienvenido a Eje Spot",
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            color = EjeSpotText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtítulo con Manrope
        Text(
            text = "Inicia sesión para descubrir y compartir\nlugares",
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.5.sp,
            color = EjeSpotTextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Campo 1: Correo electrónico
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Correo electrónico",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = EjeSpotTextMuted
            )
            Spacer(modifier = Modifier.height(5.dp))
            CustomInputBox(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                placeholder = "tú@correo.com",
                iconRes = R.drawable.ic_mail_icon,
                keyboardType = KeyboardType.Email
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Campo 2: Contraseña (sin icono de ojo a la derecha, tal como el mockup original)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Contraseña",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = EjeSpotTextMuted
            )
            Spacer(modifier = Modifier.height(5.dp))
            CustomInputBox(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                placeholder = "••••••••••••",
                iconRes = R.drawable.ic_lock_icon,
                isPassword = true,
                keyboardType = KeyboardType.Password
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ¿Olvidaste tu contraseña? (derecha, verde oscuro)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = EjeSpotPrimary,
                modifier = Modifier
                    .clickable { /* TODO */ }
                    .padding(vertical = 4.dp)
            )
        }

        // Mensaje de error
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = uiState.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Botón Iniciar sesión (.btn-block del mockup)
        Button(
            onClick = { viewModel.onLogin() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(100.dp)),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary),
            contentPadding = PaddingValues(0.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = "Iniciar sesión",
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Separador "o continúa con"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = EjeSpotOutline)
            Text(
                text = "  o continúa con  ",
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = EjeSpotTextMuted
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = EjeSpotOutline)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Botón Google (.social-btn)
        SocialLoginButton(
            text = "Continuar con Google",
            iconRes = R.drawable.ic_google_logo
        ) { }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Facebook (.social-btn)
        SocialLoginButton(
            text = "Continuar con Facebook",
            iconRes = R.drawable.ic_facebook_logo
        ) { }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Apple (.social-btn)
        SocialLoginButton(
            text = "Continuar con Apple",
            iconRes = R.drawable.ic_apple_logo
        ) { }

        Spacer(modifier = Modifier.height(18.dp))

        // Explorar como invitado (verde/marrón subrayado)
        Text(
            text = "Explorar como invitado",
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = EjeSpotTextMuted,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable { viewModel.onLoginAsGuest() }
                .padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Footer "¿No tienes cuenta? Regístrate como Turista"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Text(
                text = "¿No tienes cuenta? ",
                fontFamily = ManropeFontFamily,
                fontSize = 12.5.sp,
                color = EjeSpotTextMuted
            )
            Text(
                text = "Regístrate como Turista",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = EjeSpotPrimary,
                modifier = Modifier.clickable { /* TODO */ }
            )
        }
    }
}

@Composable
private fun CustomInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    iconRes: Int,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp))
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, EjeSpotOutline, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = EjeSpotTextMuted,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.5.sp,
                    color = EjeSpotOutline
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                textStyle = TextStyle(
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.5.sp,
                    color = EjeSpotText
                ),
                cursorBrush = SolidColor(EjeSpotPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SocialLoginButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .shadow(elevation = 0.5.dp, shape = RoundedCornerShape(100.dp)),
        shape = RoundedCornerShape(100.dp),
        color = Color.White,
        border = BorderStroke(1.2.dp, EjeSpotOutline)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                fontFamily = ManropeFontFamily,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = EjeSpotText
            )
        }
    }
}
