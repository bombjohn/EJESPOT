package com.example.ejespot.features.auth.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
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
import com.example.ejespot.core.component.ConfirmAlertDialog
import com.example.ejespot.domain.model.User
import com.example.ejespot.ui.theme.*

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (User) -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val c = ejeSpotColors()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var departmentExpanded by remember { mutableStateOf(false) }
    val registerSuccessMessage = stringResource(R.string.register_success)

    // Intercepta el botón "atrás" del dispositivo (guía 10): mientras el
    // diálogo de salida no esté visible, se pide confirmar antes de perder
    // los datos escritos en el formulario.
    BackHandler(enabled = !uiState.showExitDialog) {
        viewModel.onBackClick()
    }

    val departments = listOf(
        "Risaralda (Pereira)",
        "Quindío (Armenia)",
        "Caldas (Manizales)",
        "Valle del Cauca",
        "Antioquia (Medellín)",
        "Bogotá D.C.",
        stringResource(R.string.register_department_other)
    )

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            snackbarHostState.showSnackbar(registerSuccessMessage)
            // Se entrega la cuenta creada para que la app inicie su sesión.
            uiState.registeredUser?.let { onRegisterSuccess(it) }
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
                    color = c.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = c.text,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Badge "👢 Nuevo Aventurero"
                Text(
                    text = stringResource(R.string.register_badge),
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
                text = stringResource(R.string.register_title),
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = c.text,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtítulo
            Text(
                text = stringResource(R.string.register_subtitle),
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                color = c.textMuted,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo 1: Nombre completo
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.register_name_label),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = c.text
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
                    text = stringResource(R.string.register_email_label),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = c.text
                )
                Spacer(modifier = Modifier.height(5.dp))
                RegisterInputBox(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    placeholder = "ejemplo@gmail.com",
                    iconRes = R.drawable.ic_mail_icon,
                    keyboardType = KeyboardType.Email
                )
                if (uiState.email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Formato de correo no válido (ej. usuario@gmail.com)",
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Campo 3: Departamento de origen / residencia (Selector desplegable)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.register_department_label),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = c.text
                )
                Spacer(modifier = Modifier.height(5.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp))
                            .background(c.surface, RoundedCornerShape(14.dp))
                            .border(1.dp, c.outline, RoundedCornerShape(14.dp))
                            .clickable { departmentExpanded = true }
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location_icon),
                            contentDescription = null,
                            tint = c.text,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${uiState.department} ▾",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = c.text
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
                    text = stringResource(R.string.register_password_label),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = c.text
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (uiState.password.isEmpty()) {
                        "Mínimo 8 caracteres"
                    } else if (uiState.password.length < 8) {
                        "Mínimo 8 caracteres (${uiState.password.length}/8)"
                    } else {
                        "✓ Contraseña válida (${uiState.password.length} caracteres)"
                    },
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.sp,
                    color = if (uiState.password.isEmpty()) {
                        c.textMuted
                    } else if (uiState.password.length < 8) {
                        MaterialTheme.colorScheme.error
                    } else {
                        Color(0xFF2C5640)
                    }
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
                        uncheckedColor = c.outline
                    ),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.register_terms_prefix))
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = c.text)) {
                            append(stringResource(R.string.register_terms_service))
                        }
                        append(stringResource(R.string.register_terms_mid))
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = c.text)) {
                            append(stringResource(R.string.register_terms_privacy))
                        }
                        append(stringResource(R.string.register_terms_suffix))
                    },
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.5.sp,
                    color = c.textMuted,
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
                        text = stringResource(R.string.register_button),
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
                    text = "${stringResource(R.string.register_have_account)} ",
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = c.textMuted
                )
                Text(
                    text = stringResource(R.string.register_login),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = EjeSpotPrimary,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }

    // Diálogo de confirmación de salida (guía 10).
    if (uiState.showExitDialog) {
        ConfirmAlertDialog(
            title = stringResource(R.string.register_exit_title),
            message = stringResource(R.string.register_exit_message),
            onConfirm = {
                // Se limpia el formulario y luego se navega hacia atrás.
                viewModel.onConfirmExit()
                onNavigateBack()
            },
            onDismiss = viewModel::onDismissExitDialog,
            confirmText = stringResource(R.string.register_exit_confirm)
        )
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
            tint = c.text,
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
                visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
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
