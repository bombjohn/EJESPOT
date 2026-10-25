package com.example.ejespot.features.spots.create

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.R
import com.example.ejespot.core.component.ImagePickerBottomSheet
import com.example.ejespot.core.util.createTempImageUri
import com.example.ejespot.core.util.toImageBitmap
import com.example.ejespot.domain.model.Location
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

    // URI de la foto elegida para el lugar (galería o cámara).
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // Mensaje de éxito tomado de strings.xml.
    val successMessage = stringResource(R.string.create_success)

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
        imageUri = imageUri,
        onImagePicked = { imageUri = it },
        onSubmit = {
            viewModel.onSubmit { name, cat, loc, hours, price, desc ->
                // Generar spot pendiente para el feed.
                // Las coordenadas se derivan del municipio elegido para que el
                // marcador aparezca en el lugar correcto del mapa real.
                val (lat, lng) = coordinatesFor(loc)
                val newSpot = Spot(
                    id = "spot_${System.currentTimeMillis()}",
                    name = name,
                    category = cat,
                    municipality = loc,
                    rating = 5.0,
                    reviewCount = 0,
                    distance = "1.0 km",
                    isVerified = false,
                    priceBadge = price,
                    hours = hours,
                    priceRange = price,
                    description = desc,
                    addedBy = "@tú",
                    imageResId = R.drawable.spot_cascada_frailes,
                    location = Location(lat, lng),
                    isPending = true
                )
                onSpotCreated?.invoke(newSpot)
                imageUri = null
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(successMessage)
                }
            }
        }
    )
}

/**
 * Coordenadas aproximadas por municipio del Eje Cafetero.
 *
 * Se usan para ubicar en el mapa real el lugar que propone la comunidad,
 * ya que la demo no tiene geocodificación ni permisos de ubicación reales.
 */
private fun coordinatesFor(location: String): Pair<Double, Double> = when {
    location.startsWith("Pereira") -> 4.8143 to -75.6946
    location.startsWith("Salento") -> 4.6370 to -75.5700
    location.startsWith("Manizales") -> 5.0683 to -75.5177
    location.startsWith("Filandia") -> 4.6750 to -75.6380
    location.startsWith("Santa Rosa") -> 4.8650 to -75.6100
    location.startsWith("Armenia") -> 4.5340 to -75.6810
    else -> 4.8087 to -75.6903
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
    imageUri: Uri?,
    onImagePicked: (Uri?) -> Unit,
    onSubmit: () -> Unit
) {
    val c = ejeSpotColors()
    val context = LocalContext.current
    var showLocationSelector by remember { mutableStateOf(false) }

    // --- Selector de imagen (guía 15) ---
    // `GetContent` abre la galería sin necesidad de permisos: el usuario elige
    // el archivo y la app recibe acceso solo a esa imagen.
    var showImagePicker by remember { mutableStateOf(false) }

    // URI temporal donde la cámara escribirá la foto (se recuerda si hay rotación).
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) onImagePicked(uri)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) onImagePicked(tempCameraUri)
    }

    // La cámara sí requiere permiso explícito en tiempo de ejecución.
    val cameraPermissionMessage = stringResource(R.string.camera_permission_required)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createTempImageUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            android.widget.Toast.makeText(
                context,
                cameraPermissionMessage,
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    val availableLocations = listOf(
        "Pereira, Risaralda",
        "Salento, Quindío",
        "Manizales, Caldas",
        "Filandia, Quindío",
        "Santa Rosa de Cabal, Risaralda",
        "Armenia, Quindío"
    )

    val categories = com.example.ejespot.domain.model.SpotCategories.all
        .map { it to com.example.ejespot.domain.model.SpotCategories.emoji(it) }
    val priceTypes = listOf(
        stringResource(R.string.create_price_free),
        stringResource(R.string.create_price_cheap),
        stringResource(R.string.create_price_moderate),
        stringResource(R.string.create_price_expensive)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.background)
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
                color = c.surfaceVariant,
                border = BorderStroke(1.dp, c.outline)
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

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = stringResource(R.string.create_title),
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = c.text
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Caja de Fotografía: muestra la imagen elegida o el placeholder.
        Surface(
            onClick = { showImagePicker = true },
            shape = RoundedCornerShape(16.dp),
            color = c.surfaceRaised,
            border = BorderStroke(1.2.dp, c.outline),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            if (imageUri != null) {
                // Se decodifica el URI a Bitmap para poder mostrarlo con Image().
                val bitmap = remember(imageUri) { imageUri.toImageBitmap(context) }
                Box(modifier = Modifier.fillMaxSize()) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.create_photo_label),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    // Texto para indicar que se puede cambiar la foto.
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Black.copy(alpha = 0.55f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.change_photo),
                            fontFamily = ManropeFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = c.textMuted,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.create_add_photos),
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textSoft
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // NOMBRE DEL LUGAR
        FieldLabel(stringResource(R.string.create_name_label))
        Spacer(modifier = Modifier.height(6.dp))
        InputBox(
            value = uiState.name,
            onValueChange = onNameChanged,
            placeholder = stringResource(R.string.create_name_placeholder)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CATEGORÍA
        FieldLabel(stringResource(R.string.create_category_label))
        Spacer(modifier = Modifier.height(8.dp))
        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (cat, emoji) ->
                val isSelected = uiState.category == cat
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isSelected) c.primary else c.chipIdle,
                    border = BorderStroke(1.dp, if (isSelected) c.primary else c.outline),
                    modifier = Modifier.clickable { onCategoryChanged(cat) }
                ) {
                    Text(
                        text = "$emoji ${stringResource(
                            com.example.ejespot.domain.model.SpotCategories.labelRes(cat)
                        )}",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else c.text,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // RANGO DE PRECIO ESTIMADO
        FieldLabel(stringResource(R.string.create_price_label))
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            priceTypes.forEach { price ->
                val isSelected = uiState.priceType == price
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isSelected) c.secondary else c.chipIdle,
                    border = BorderStroke(1.dp, if (isSelected) c.secondary else c.outline),
                    modifier = Modifier.clickable { onPriceTypeChanged(price) }
                ) {
                    Text(
                        text = price,
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else c.text,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // HORARIO DE ATENCIÓN SUGERIDO
        FieldLabel(stringResource(R.string.create_hours_label))
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(c.surface, RoundedCornerShape(14.dp))
                .border(1.dp, c.outlineSoft, RoundedCornerShape(14.dp))
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
                            text = stringResource(R.string.create_hours_placeholder),
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            color = c.placeholder
                        )
                    }
                    BasicTextField(
                        value = uiState.hours,
                        onValueChange = onHoursChanged,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            color = c.text
                        ),
                        cursorBrush = SolidColor(c.primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = c.placeholder,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // UBICACIÓN
        FieldLabel(stringResource(R.string.create_location_label))
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            onClick = { showLocationSelector = true },
            shape = RoundedCornerShape(14.dp),
            color = c.successBg,
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
                    tint = c.successText,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.create_coordinates_ready, uiState.location),
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = c.successText
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta Informativa / Estado Pendiente
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = c.surfaceVariant,
            border = BorderStroke(1.dp, c.outlineSoft),
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
                    tint = c.successText,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = buildAnnotatedString {
                        val prefix = stringResource(R.string.create_pending_prefix)
                        val suffix = stringResource(R.string.create_pending_suffix)
                        append(prefix)
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = c.text)) {
                            append(stringResource(R.string.create_pending_bold))
                        }
                        append(suffix)
                    },
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.sp,
                    color = c.textSoft,
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
                text = stringResource(R.string.create_submit),
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
                    text = stringResource(R.string.create_select_municipality),
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
                            color = if (loc == uiState.location) c.primary else c.text,
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

    // Selector de imagen (cámara o galería).
    if (showImagePicker) {
        ImagePickerBottomSheet(
            onCameraClick = {
                showImagePicker = false
                // La cámara necesita permiso; la galería (GetContent) no.
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    val uri = createTempImageUri(context)
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            onGalleryClick = {
                showImagePicker = false
                galleryLauncher.launch("image/*")
            },
            onDismiss = { showImagePicker = false }
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
    val c = ejeSpotColors()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.background)
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
                .background(c.successBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.create_success_cd),
                tint = c.successText,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Badge XP
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = c.successBg
        ) {
            Text(
                text = stringResource(R.string.create_success_xp_badge),
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = c.successText,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Título de Éxito
        Text(
            text = stringResource(R.string.create_success_title),
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = c.text,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mensaje Descriptivo
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.create_success_desc_prefix))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = c.text)) {
                    append(stringResource(R.string.create_success_desc_bold1))
                }
                append(stringResource(R.string.create_success_desc_mid))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = c.text)) {
                    append(stringResource(R.string.create_success_desc_bold2))
                }
                append(".")
            },
            fontFamily = ManropeFontFamily,
            fontSize = 13.5.sp,
            color = c.textSoft,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Tarjeta resumen del spot enviado
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = c.surface,
            border = BorderStroke(1.dp, c.outlineSoft),
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
                        .background(c.successBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Landscape,
                        contentDescription = null,
                        tint = c.successText,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = spotName.ifBlank { stringResource(R.string.create_placeholder_name) },
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = c.text
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$location · $category",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        color = c.textSoft
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
                text = stringResource(R.string.create_back_home),
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
            color = c.surfaceRaised,
            border = BorderStroke(1.dp, c.outline)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.create_view_proposals),
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = c.text
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FieldLabel(text: String) {
    val c = ejeSpotColors()
    Text(
        text = text,
        fontFamily = ManropeFontFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        color = c.placeholder,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun InputBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    val c = ejeSpotColors()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(c.surface, RoundedCornerShape(14.dp))
            .border(1.dp, c.outlineSoft, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = c.placeholder
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = ManropeFontFamily,
                fontSize = 13.5.sp,
                color = c.text
            ),
            cursorBrush = SolidColor(c.primary),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
