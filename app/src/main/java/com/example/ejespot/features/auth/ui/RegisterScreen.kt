package com.example.ejespot.features.auth.ui

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.R
import com.example.ejespot.ui.theme.*

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var departmentExpanded by remember { mutableStateOf(false) }

    val departments = listOf(
        "Risaralda (Pereira)",
        "Quindío (Armenia)",
        "Caldas (Manizales)",
        "Valle del Cauca",
        "Antioquia (Medellín)",
        "Bogotá D.C.",
        "Otro departamento"
    )

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            snackbarHostState.showSnackbar("¡Cuenta creada con éxito! Bienvenido a EjeSpot")
            onRegisterSuccess()
            viewModel.resetSuccess()
        }
    }

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

            // Barra superior con botón volver y badge "👢 Nuevo Aventurero"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón atrás redondo
                Surface(
                    onClick = onNavigateBack,
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

                // Badge "👢 Nuevo Aventurero"
                Text(
                    text = "👢 Nuevo Aventurero",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = EjeSpotPrimary
                )

                // Espaciador para centrar el badge
                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título "Crea tu cuenta" en Fraunces Serif
            Text(
                text = "Crea tu cuenta",
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF1F1A15),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtítulo
            Text(
                text = "Únete a la comunidad turística del Eje\nCafetero y gana XP explorando",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                color = Color(0xFF2E241C),
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo 1: Nombre completo
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nombre completo",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1F1A15)
                )
                Spacer(modifier = Modifier.height(5.dp))
                RegisterInputBox(
                    value = uiState.fullName,
                    onValueChange = { viewModel.onFullNameChanged(it) },
                    placeholder = "Renata Castaño",
                    iconRes = R.drawable.ic_person_icon
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Campo 2: Correo electrónico
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Correo electrónico",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1F1A15)
                )
                Spacer(modifier = Modifier.height(5.dp))
                RegisterInputBox(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    placeholder = "renata@turismoeje.com",
                    iconRes = R.drawable.ic_mail_icon,
                    keyboardType = KeyboardType.Email
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Campo 3: Departamento de origen / residencia (Selector desplegable)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Departamento de origen / residencia",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1F1A15)
                )
                Spacer(modifier = Modifier.height(5.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp))
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .border(1.dp, EjeSpotOutline, RoundedCornerShape(14.dp))
                            .clickable { departmentExpanded = true }
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location_icon),
                            contentDescription = null,
                            tint = Color(0xFF1F1A15),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${uiState.department} ▾",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color(0xFF1F1A15)
                        )
                    }

                    DropdownMenu(
                        expanded = departmentExpanded,
                        onDismissRequest = { departmentExpanded = false }
                    ) {
                        departments.forEach { dept ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = dept,
                                        fontFamily = ManropeFontFamily,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    viewModel.onDepartmentChanged(dept)
                                    departmentExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Campo 4: Contraseña
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Contraseña",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1F1A15)
                )
                Spacer(modifier = Modifier.height(5.dp))
                RegisterInputBox(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    placeholder = "••••••••••••",
                    iconRes = R.drawable.ic_lock_icon,
                    isPassword = true,
                    keyboardType = KeyboardType.Password
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Checkbox: Términos del servicio y política de privacidad
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onToggleTerms(!uiState.acceptedTerms) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.acceptedTerms,
                    onCheckedChange = { viewModel.onToggleTerms(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = EjeSpotPrimary,
                        uncheckedColor = EjeSpotOutline
                    ),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Acepto los ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1F1A15))) {
                            append("Términos del Servicio")
                        }
                        append(" y la ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1F1A15))) {
                            append("Política de Privacidad de Eje Spot")
                        }
                        append(".")
                    },
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.5.sp,
                    color = Color(0xFF2E241C),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón "Crear cuenta gratis"
            Button(
                onClick = { viewModel.onRegister() },
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
                        text = "Crear cuenta gratis",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer: "¿Ya tienes cuenta? Inicia sesión"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E241C)
                )
                Text(
                    text = "Inicia sesión",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = EjeSpotPrimary,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Composable
private fun RegisterInputBox(
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
            tint = Color(0xFF1F1A15),
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
                visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
