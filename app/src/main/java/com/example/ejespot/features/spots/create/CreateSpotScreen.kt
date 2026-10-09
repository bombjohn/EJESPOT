package com.example.ejespot.features.spots.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.ui.theme.*

@Composable
fun CreateSpotScreen(
    padding: PaddingValues = PaddingValues(),
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    viewModel: CreateSpotViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = listOf("Naturaleza", "Café", "Gastronomía", "Historia", "Cultura")
    val priceTypes = listOf("Gratuito", "Económico", "Moderado")

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("¡Spot enviado para verificación! (+100 XP)")
            viewModel.resetState()
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(bottom = padding.calculateBottomPadding())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(padding.calculateTopPadding() + 12.dp))

        // Barra superior con botón volver y título
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Proponer nuevo Spot",
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF1F1A15)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Cuadro de imagen de la publicación (como indica la guía, temporal con placeholder aleatorio)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, EjeSpotOutline),
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = EjeSpotPrimary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Adjuntar fotografía del lugar",
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1A15)
                )
                Text(
                    text = "Toca para seleccionar de tu galería (JPG, PNG)",
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF887D70)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Nombre del Spot
        FormField(label = "Nombre del lugar / Spot") {
            CustomInput(
                value = uiState.name,
                onValueChange = { viewModel.onNameChanged(it) },
                placeholder = "Ej: Mirador Colina Iluminada"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Categoría (Selector de chips)
        FormField(label = "Categoría") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(3).forEach { cat ->
                    val isSelected = uiState.category == cat
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) EjeSpotPrimary else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) EjeSpotPrimary else EjeSpotOutline),
                        modifier = Modifier.clickable { viewModel.onCategoryChanged(cat) }
                    ) {
                        Text(
                            text = cat,
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF2E241C),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horarios
        FormField(label = "Horario de atención") {
            CustomInput(
                value = uiState.hours,
                onValueChange = { viewModel.onHoursChanged(it) },
                placeholder = "Ej: 8:00 AM - 6:00 PM"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tipo de precio
        FormField(label = "Precio de entrada") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                priceTypes.forEach { price ->
                    val isSelected = uiState.priceType == price
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) EjeSpotSecondary else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) EjeSpotSecondary else EjeSpotOutline),
                        modifier = Modifier.clickable { viewModel.onPriceTypeChanged(price) }
                    ) {
                        Text(
                            text = price,
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF2E241C),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Descripción detallada
        FormField(label = "Descripción y recomendaciones") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.White, RoundedCornerShape(14.dp))
                    .border(1.dp, EjeSpotOutline, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                if (uiState.description.isEmpty()) {
                    Text(
                        text = "Describe qué hace especial este lugar, cómo llegar y qué probar...",
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = EjeSpotOutline
                    )
                }
                BasicTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onDescriptionChanged(it) },
                    textStyle = TextStyle(
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF1F1A15)
                    ),
                    cursorBrush = SolidColor(EjeSpotPrimary),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Enviar Publicación
        Button(
            onClick = { viewModel.onSubmit() },
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
                    text = "Publicar Spot (+100 XP)",
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun FormField(
    label: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp,
            color = Color(0xFF1F1A15)
        )
        Spacer(modifier = Modifier.height(5.dp))
        content()
    }
}

@Composable
private fun CustomInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, EjeSpotOutline, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = EjeSpotOutline
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = Color(0xFF1F1A15)
            ),
            cursorBrush = SolidColor(EjeSpotPrimary),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
