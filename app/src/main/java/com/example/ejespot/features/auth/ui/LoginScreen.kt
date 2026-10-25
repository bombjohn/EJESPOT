package com.example.ejespot.features.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.ejespot.domain.model.User
import com.example.ejespot.ui.theme.*

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val c = ejeSpotColors()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val googleSoonMessage = stringResource(R.string.login_social_google_soon)
    val facebookSoonMessage = stringResource(R.string.login_social_facebook_soon)
    val appleSoonMessage = stringResource(R.string.login_social_apple_soon)

    LaunchedEffect(uiState.loginSuccess) {
        uiState.authenticatedUser?.let { user ->
            if (uiState.loginSuccess) onLoginSuccess(user)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = c.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

        // Logo Oficial EjeSpot
        Image(
            painter = painterResource(id = R.drawable.ic_logo_ejespot),
            contentDescription = stringResource(R.string.splash_logo_description),
            modifier = Modifier.size(130.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Título con Fraunces Serif
        Text(
            text = stringResource(R.string.login_welcome),
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            color = c.text,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtítulo con Manrope
        Text(
            text = stringResource(R.string.login_subtitle),
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = c.text,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Campo 1: Correo electrónico
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.login_email_label),
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = c.text
            )
            Spacer(modifier = Modifier.height(5.dp))
            CustomInputBox(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                placeholder = stringResource(R.string.forgot_email_placeholder),
                iconRes = R.drawable.ic_mail_icon,
                keyboardType = KeyboardType.Email
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Campo 2: Contraseña (sin icono de ojo a la derecha, tal como el mockup original)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.login_password_label),
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = c.text
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
                text = stringResource(R.string.login_forgot_password),
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = EjeSpotPrimary,
                modifier = Modifier
                    .clickable { onNavigateToForgotPassword() }
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
                    text = stringResource(R.string.login_button),
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
            HorizontalDivider(modifier = Modifier.weight(1f), color = c.outline)
            Text(
                text = "  ${stringResource(R.string.login_or_continue_with)}  ",
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = c.textMuted
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = c.outline)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Botón Google (.social-btn)
        SocialLoginButton(
            text = stringResource(R.string.login_google),
            iconRes = R.drawable.ic_google_logo
        ) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(googleSoonMessage)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Facebook (.social-btn)
        SocialLoginButton(
            text = stringResource(R.string.login_facebook),
            iconRes = R.drawable.ic_facebook_logo
        ) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(facebookSoonMessage)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Apple (.social-btn)
        SocialLoginButton(
            text = stringResource(R.string.login_apple),
            iconRes = R.drawable.ic_apple_logo
        ) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(appleSoonMessage)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Explorar como invitado (verde/marrón subrayado)
        Text(
            text = stringResource(R.string.login_guest),
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = c.textMuted,
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
                text = "${stringResource(R.string.login_no_account)} ",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.5.sp,
                color = c.textMuted
            )
            Text(
                text = stringResource(R.string.login_register),
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = EjeSpotPrimary,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
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
    val c = ejeSpotColors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp))
            .background(c.surface, RoundedCornerShape(14.dp))
            .border(1.dp, c.outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = c.textMuted,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.5.sp,
                    color = c.placeholder
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
                    color = c.text
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
    val c = ejeSpotColors()
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .shadow(elevation = 0.5.dp, shape = RoundedCornerShape(100.dp)),
        shape = RoundedCornerShape(100.dp),
        color = c.surface,
        border = BorderStroke(1.2.dp, c.outline)
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
                color = c.text
            )
        }
    }
}
