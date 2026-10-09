package com.example.ejespot.features.auth.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.R
import com.example.ejespot.ui.theme.*

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = EjeSpotBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Barra superior: botón volver y título si está en "Correo enviado"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = {
                        if (uiState.isSuccess) {
                            viewModel.resetState()
                        } else {
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFFEFE6D6)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF1F1A15),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = if (uiState.isSuccess) "Correo enviado" else "Recuperar acceso",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1F1A15)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Círculo verde pastel con ícono central
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(EjeSpotPrimaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (uiState.isSuccess) Icons.Default.Mail else Icons.Default.Lock,
                    contentDescription = null,
                    tint = EjeSpotPrimary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (!uiState.isSuccess) {
                // ================= PANTALLA 2.4: RECUPERAR CONTRASEÑA =================
                Text(
                    text = "¿Olvidaste tu\ncontraseña?",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color(0xFF1F1A15),
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Escribe el correo que usaste al registrarte y te enviaremos un enlace para restablecer tu contraseña.",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    color = Color(0xFF2E241C),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Campo Correo electrónico
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CORREO ELECTRÓNICO",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF1F1A15)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

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
                            painter = painterResource(id = R.drawable.ic_mail_icon),
                            contentDescription = null,
                            tint = Color(0xFF1F1A15),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (uiState.email.isEmpty()) {
                                Text(
                                    text = "tú@correo.com",
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 13.5.sp,
                                    color = EjeSpotOutline
                                )
                            }
                            BasicTextField(
                                value = uiState.email,
                                onValueChange = { viewModel.onEmailChanged(it) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                textStyle = TextStyle(
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF1F1A15)
                                ),
                                cursorBrush = SolidColor(EjeSpotPrimary),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón "Enviar enlace de recuperación"
                Button(
                    onClick = { viewModel.onSendRecoveryLink() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(100.dp)),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(
                            text = "Enviar enlace de recuperación",
                            fontFamily = ManropeFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Volver al inicio de sesión
                Text(
                    text = "Volver al inicio de sesión",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = EjeSpotPrimary,
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .padding(vertical = 8.dp)
                )

            } else {
                // ================= PANTALLA 2.5: CORREO ENVIADO =================
                Text(
                    text = "¡Revisa tu correo!",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color(0xFF1F1A15),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Enviamos un enlace de recuperación a:",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    color = Color(0xFF2E241C),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Correo destacado en verde
                Text(
                    text = uiState.email,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = EjeSpotPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = buildAnnotatedString {
                        append("El enlace expira en ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1F1A15))) {
                            append("30 minutos")
                        }
                        append(". Si no lo ves en tu bandeja, revisa la carpeta de spam.")
                    },
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.sp,
                    color = Color(0xFF2E241C),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Botón "Abrir aplicación de correo"
                Button(
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_APP_EMAIL)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        try {
                            context.startActivity(emailIntent)
                        } catch (e: Exception) {
                            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"))
                            context.startActivity(fallbackIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(100.dp)),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary)
                ) {
                    Text(
                        text = "Abrir aplicación de correo",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botón "Reenviar correo" (Outline claro)
                OutlinedButton(
                    onClick = {
                        viewModel.onResendEmail()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFBF6EE)),
                    border = BorderStroke(1.2.dp, EjeSpotOutline)
                ) {
                    Text(
                        text = "Reenviar correo",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1A15)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Enlace Volver al inicio de sesión
                Text(
                    text = "Volver al inicio de sesión",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = EjeSpotPrimary,
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
