package com.example.ejespot.features.spots.create

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.R
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CreateSpotScreen(
    padding: PaddingValues = PaddingValues(),
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onSpotCreated: ((Spot) -> Unit)? = null,
    viewModel: CreateSpotViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Si ya fue enviado con éxito, mostrar Pantalla 5.2 (Éxito / Feedback de Envío)
    if (uiState.isSuccess) {
        CreateSpotSuccessScreen(
            spotName = uiState.submittedSpotName,
            category = uiState.submittedCategory,
            location = uiState.submittedLocation,
            padding = padding,
            onBackToHome = {
                viewModel.resetState()
                onNavigateBack()
            },
            onViewProposedSpots = {
                viewModel.resetState()
                onNavigateBack()
            }
        )
        return
    }

    // Pantalla 5.1: Formulario Agregar Punto de Interés ("Nuevo lugar")
    CreateSpotFormScreen(
        uiState = uiState,
        padding = padding,
        onNavigateBack = onNavigateBack,
        onNameChanged = viewModel::onNameChanged,
        onCategoryChanged = viewModel::onCategoryChanged,
        onLocationChanged = viewModel::onLocationChanged,
        onHoursChanged = viewModel::onHoursChanged,
        onPriceTypeChanged = viewModel::onPriceTypeChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onSubmit = {
            viewModel.onSubmit { name, cat, loc, hours, price, desc ->
                // Generar spot pendiente para el feed
                val newSpot = Spot(
                    id = "spot_${System.currentTimeMillis()}",
                    name = name,
                    category = cat,
                    location = loc,
                    rating = 5.0,
                    reviewCount = 0,
                    distance = "A 1.0 km",
                    isVerified = false,
                    priceBadge = price,
                    hours = hours,
                    priceRange = price,
                    description = desc,
                    addedBy = "@tú",
                    imageResId = R.drawable.spot_cascada_frailes,
                    isPending = true
                )
                onSpotCreated?.invoke(newSpot)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("¡Punto propuesto con éxito! (+50 XP)")
                }
            }
        }
    )
}

@Composable
private fun CreateSpotFormScreen(
    uiState: CreateSpotUiState,
    padding: PaddingValues,
    onNavigateBack: () -> Unit,
    onNameChanged: (String) -> Unit,
    onCategoryChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
    onHoursChanged: (String) -> Unit,
    onPriceTypeChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var showLocationSelector by remember { mutableStateOf(false) }
    val availableLocations = listOf(
        "Pereira, Risaralda",
        "Salento, Quindío",
        "Manizales, Caldas",
        "Filandia, Quindío",
        "Santa Rosa de Cabal, Risaralda",
        "Armenia, Quindío"
    )

    val categories = listOf(
        "Gastronomía" to "🍕",
        "Naturaleza" to "🏞️",
        "Cultura" to "🎭",
        "Ocio" to "⏹️",
        "Historia" to "🏛️"
    )

    val priceTypes = listOf("Gratuito", "Económico", "Moderado", "Costoso")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(bottom = padding.calculateBottomPadding())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(padding.calculateTopPadding() + 12.dp))

        // Encabezado: Botón Volver y Título
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onNavigateBack,
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFFF0E7D8),
                border = BorderStroke(1.dp, Color(0xFFDFD4C2))
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
                text = "Nuevo lugar",
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF1F1A15)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Caja de Fotografía
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFFAF6EE),
            border = BorderStroke(1.2.dp, Color(0xFFDCCFBD)),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = Color(0xFF4A3E31),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Añadir fotografías del lugar",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5A4833)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // NOMBRE DEL LUGAR
        FieldLabel("NOMBRE DEL LUGAR")
        Spacer(modifier = Modifier.height(6.dp))
        InputBox(
            value = uiState.name,
            onValueChange = onNameChanged,
            placeholder = "Ej. Cascada El Bosque"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CATEGORÍA
        FieldLabel("CATEGORÍA")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.take(2).forEach { (cat, emoji) ->
                val isSelected = uiState.category == cat
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isSelected) EjeSpotPrimary else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) EjeSpotPrimary else Color(0xFFDECFC0)),
                    modifier = Modifier.clickable { onCategoryChanged(cat) }
                ) {
                    Text(
                        text = "$emoji $cat",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF1F1A15),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.drop(2).forEach { (cat, emoji) ->
                val isSelected = uiState.category == cat
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isSelected) EjeSpotPrimary else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) EjeSpotPrimary else Color(0xFFDECFC0)),
                    modifier = Modifier.clickable { onCategoryChanged(cat) }
                ) {
                    Text(
                        text = "$emoji $cat",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF1F1A15),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // RANGO DE PRECIO ESTIMADO
        FieldLabel("RANGO DE PRECIO ESTIMADO")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            priceTypes.forEach { price ->
                val isSelected = uiState.priceType == price
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isSelected) Color(0xFFB5652F) else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFFB5652F) else Color(0xFFDECFC0)),
                    modifier = Modifier.clickable { onPriceTypeChanged(price) }
                ) {
                    Text(
                        text = price,
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF1F1A15),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // HORARIO DE ATENCIÓN SUGERIDO
        FieldLabel("HORARIO DE ATENCIÓN SUGERIDO")
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color.White, RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFFE5DDD0), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (uiState.hours.isEmpty()) {
                        Text(
                            text = "7:00 a.m. - 5:00 p.m.",
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFFA59787)
                        )
                    }
                    BasicTextField(
                        value = uiState.hours,
                        onValueChange = onHoursChanged,
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
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Color(0xFF887D70),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // UBICACIÓN
        FieldLabel("UBICACIÓN")
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            onClick = { showLocationSelector = true },
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFDCEAD9),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF2C5640),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${uiState.location} · Coordenadas listas",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C5640)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta Informativa / Estado Pendiente
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF3ECE0),
            border = BorderStroke(1.dp, Color(0xFFE5DDD0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Color(0xFF3D6B4F),
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Este punto de interés quedará como ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1F1A15))) {
                            append("pendiente")
                        }
                        append(" hasta que un moderador confirme que existe y que los datos son precisos.")
                    },
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.sp,
                    color = Color(0xFF5A4833),
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón: Enviar para revisión
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(100.dp)),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary)
        ) {
            Text(
                text = "Enviar para revisión",
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Diálogo de Selección de Ubicación
    if (showLocationSelector) {
        AlertDialog(
            onDismissRequest = { showLocationSelector = false },
            title = {
                Text(
                    text = "Seleccionar Municipio",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    availableLocations.forEach { loc ->
                        Text(
                            text = loc,
                            fontFamily = ManropeFontFamily,
                            fontWeight = if (loc == uiState.location) FontWeight.Bold else FontWeight.Normal,
                            color = if (loc == uiState.location) EjeSpotPrimary else Color(0xFF1F1A15),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLocationChanged(loc)
                                    showLocationSelector = false
                                }
                                .padding(vertical = 10.dp)
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun CreateSpotSuccessScreen(
    spotName: String,
    category: String,
    location: String,
    padding: PaddingValues,
    onBackToHome: () -> Unit,
    onViewProposedSpots: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(bottom = padding.calculateBottomPadding())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(padding.calculateTopPadding() + 40.dp))

        // Icono Circular Verde Grande
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFD6EAD8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Éxito",
                tint = Color(0xFF2C5640),
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Badge XP
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = Color(0xFFCFE8D6)
        ) {
            Text(
                text = "+50 XP Ganados · En revisión",
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2C5640),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Título de Éxito
        Text(
            text = "¡Punto enviado para\nrevisión!",
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Color(0xFF1F1A15),
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mensaje Descriptivo
        Text(
            text = buildAnnotatedString {
                append("Gracias por enriquecer el turismo del Eje Cafetero. Un ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1F1A15))) {
                    append("Embajador Local")
                }
                append(" verificará que los horarios y la ubicación sean exactos en menos de ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1F1A15))) {
                    append("24 horas")
                }
                append(".")
            },
            fontFamily = ManropeFontFamily,
            fontSize = 13.5.sp,
            color = Color(0xFF6E6252),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Tarjeta resumen del spot enviado
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE5DDD0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Miniatura visual
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFDCEAD9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Landscape,
                        contentDescription = null,
                        tint = Color(0xFF2C5640),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = spotName.ifBlank { "Cascada El Bosque" },
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = Color(0xFF1F1A15)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$location · $category",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF6E6252)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(34.dp))

        // Botón 1: Volver al Inicio
        Button(
            onClick = onBackToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary)
        ) {
            Text(
                text = "Volver al Inicio",
                fontFamily = ManropeFontFamily,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón 2: Ver en mis puntos propuestos
        Surface(
            onClick = onViewProposedSpots,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(100.dp),
            color = Color(0xFFF7F2E9),
            border = BorderStroke(1.dp, Color(0xFFDECFC0))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ver en mis puntos propuestos",
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1A15)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontFamily = ManropeFontFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF887D70),
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun InputBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE5DDD0), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = Color(0xFFA59787)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
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
