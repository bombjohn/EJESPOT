package com.example.ejespot.features.spots.map

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ejespot.R
import com.example.ejespot.core.data.SpotStore
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.core.ui.SpotImage
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotCategories
import com.example.ejespot.features.spots.list.SpotListViewModel
import com.example.ejespot.ui.theme.*
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/** Centro aproximado del Eje Cafetero (Pereira–Manizales–Armenia). */
private val EJE_CAFETERO_CENTER = GeoPoint(4.8087, -75.6903)
private const val DEFAULT_ZOOM = 9.0

/**
 * User agent para los mosaicos de OpenStreetMap.
 * OSM exige identificar la app; sin esto los servidores responden
 * "Access blocked" y el mapa sale en blanco.
 */
private const val USER_AGENT = "EjeSpot/1.0 (Android; com.example.ejespot; proyecto universitario EjeSpot)"

/** Abre Google Maps con la ruta hacia el lugar. */
private fun openDirections(context: Context, spot: Spot) {
    val query = "https://www.google.com/maps/dir/?api=1&destination=" +
        Uri.encode("${spot.location.latitude},${spot.location.longitude}")
    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(query))
    runCatching { context.startActivity(intent) }
}

@Composable
fun MapScreen(
    padding: PaddingValues = PaddingValues(),
    snackbarHostState: SnackbarHostState? = null,
    onNavigateToSpotDetail: (String) -> Unit,
    viewModel: SpotListViewModel
) {
    val c = ejeSpotColors()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // --- Permiso de ubicación en tiempo de ejecución (guía 15) ---
    // Observa si el permiso ya está concedido; el botón "mi ubicación" lo
    // solicita con RequestPermission y, si el usuario lo niega, el mapa
    // sigue funcionando con normalidad (solo se deshabilita esa función).
    var hasLocationPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    // Textos resueltos en el contexto @Composable para poder usarlos dentro
    // de los callbacks, donde `stringResource` no es válido.
    val locationPermissionMessage = stringResource(R.string.location_permission_required)
    val centeredLocationMessage = stringResource(R.string.map_centered_location)
    val centeredRegionMessage = stringResource(R.string.map_centered_region)

    // Etiqueta traducida de cada categoría, para los marcadores del mapa.
    val categoryLabels: Map<String, String> = SpotCategories.all.associateWith { category ->
        stringResource(SpotCategories.labelRes(category))
    }

    val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (!isGranted) {
            coroutineScope.launch {
                snackbarHostState?.showSnackbar(locationPermissionMessage)
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val visitedSpotIds by viewModel.visitedSpotIds.collectAsState()
    val savedSpotIds by viewModel.savedSpotIds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(SpotCategories.ALL) }

    val allSpots = uiState.spots + uiState.pendingSpots

    val visibleSpots = remember(allSpots, selectedCategory, searchQuery) {
        allSpots.filter { spot ->
            val matchCategory = selectedCategory == SpotCategories.ALL ||
                spot.category == selectedCategory
            val matchSearch = searchQuery.isBlank() ||
                spot.name.contains(searchQuery, ignoreCase = true) ||
                spot.municipality.contains(searchQuery, ignoreCase = true)
            matchCategory && matchSearch
        }
    }

    var selectedSpotId by remember { mutableStateOf(allSpots.firstOrNull()?.id ?: "") }
    val selectedSpot = allSpots.firstOrNull { it.id == selectedSpotId } ?: visibleSpots.firstOrNull()

    // Si el lugar seleccionado se oculta por el filtro, saltamos al primero visible.
    LaunchedEffect(visibleSpots) {
        if (visibleSpots.isNotEmpty() && visibleSpots.none { it.id == selectedSpotId }) {
            selectedSpotId = visibleSpots.first().id
        }
    }

    // --- Mapa osmdroid (OpenStreetMap) ---
    val mapView = rememberMapViewWithLifecycle(context, darkTheme = c.isDark)
    var mapReady by remember { mutableStateOf(false) }

    val markerIcon = remember { context.defaultMarker() }
    val visitedIcon = remember { context.circleMarker(0xFF3D6B4F) }
    val savedIcon = remember { context.circleMarker(0xFFB5652F) }

    // Redibuja los marcadores cada vez que cambian los filtros o los estados del usuario.
    LaunchedEffect(mapView, visibleSpots, visitedSpotIds, savedSpotIds, selectedSpotId, mapReady) {
        if (!mapReady) return@LaunchedEffect
        mapView.overlays.removeAll { it is Marker }

        visibleSpots.forEach { spot ->
            val isSelected = spot.id == selectedSpotId
            val isVisited = spot.id in visitedSpotIds
            val isSaved = spot.id in savedSpotIds

            val marker = Marker(mapView).apply {
                position = GeoPoint(spot.location.latitude, spot.location.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = spot.name
                snippet = "${categoryLabels[spot.category] ?: spot.category} · " +
                    "${spot.rating} ★ · ${spot.municipality}"
                icon = when {
                    isSelected -> context.pinMarker(spot.accentColor, selected = true)
                    isVisited -> visitedIcon
                    isSaved -> savedIcon
                    else -> markerIcon
                }
                setOnMarkerClickListener { _, _ ->
                    selectedSpotId = spot.id
                    false
                }
            }
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    // Centra el mapa en el lugar seleccionado cuando cambia.
    LaunchedEffect(selectedSpotId, mapReady) {
        if (!mapReady) return@LaunchedEffect
        selectedSpot?.let { spot ->
            mapView.controller.animateTo(GeoPoint(spot.location.latitude, spot.location.longitude))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(c.background)
            .padding(padding)
    ) {
        // --- MAPA REAL ---
        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { mapReady = true }
        )

        // Degradado superior para que los controles se lean sobre el mapa.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(c.scrim.copy(alpha = 0.35f), Color.Transparent)
                    )
                )
        )

        // --- PARTE SUPERIOR: BUSCADOR Y FILTROS ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = c.surface,
                border = BorderStroke(1.dp, c.outline),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.common_search),
                        tint = c.textSoft,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = stringResource(R.string.map_search_placeholder),
                                fontFamily = ManropeFontFamily,
                                fontSize = 13.5.sp,
                                color = c.placeholder
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
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

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = c.primaryContainer
                    ) {
                        Text(
                            text = "OPENSTREETMAP",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            color = c.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SpotCategories.filterOptions) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) c.primary else c.surface,
                        border = BorderStroke(1.dp, if (isSelected) c.primary else c.outline),
                        shadowElevation = 1.dp,
                        modifier = Modifier.clickable { selectedCategory = category }
                    ) {
                        Text(
                            text = if (category == SpotCategories.ALL) {
                                stringResource(R.string.feed_category_all)
                            } else {
                                // Emoji + nombre traducido de la categoría.
                                "${SpotCategories.emoji(category)} " +
                                    stringResource(SpotCategories.labelRes(category))
                            },
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else c.text,
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // --- BOTONES FLOTANTES: MI UBICACIÓN Y CENTRAR EN EL EJE CAFETERO ---
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Mi ubicación real: primero pide el permiso si hace falta.
            Surface(
                onClick = {
                    if (hasLocationPermission) {
                        mapView.centerOnMyLocation()
                        coroutineScope.launch {
                            snackbarHostState?.showSnackbar(centeredLocationMessage)
                        }
                    } else {
                        locationPermissionLauncher.launch(
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }
                },
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = c.surface,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = stringResource(R.string.map_my_location),
                        tint = if (hasLocationPermission) c.primary else c.textSoft,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Centrar en el Eje Cafetero (no requiere permiso).
            Surface(
                onClick = {
                    selectedCategory = SpotCategories.ALL
                    searchQuery = ""
                    mapView.controller.animateTo(EJE_CAFETERO_CENTER, DEFAULT_ZOOM, 700L)
                    coroutineScope.launch {
                        snackbarHostState?.showSnackbar(centeredRegionMessage)
                    }
                },
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = c.surface,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(R.string.map_center_region),
                        tint = c.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // --- PARTE INFERIOR: TARJETA DEL PUNTO SELECCIONADO ---
        AnimatedVisibility(
            visible = selectedSpot != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedSpot?.let { spot ->
                // Textos resueltos aquí (contexto @Composable) para poder usarlos
                // dentro de los callbacks, donde `stringResource` no es válido.
                val visitedSnackbarText = stringResource(R.string.map_visited_snackbar, spot.name)
                val unvisitedSnackbarText = stringResource(R.string.map_unvisited_snackbar, spot.name)
                val savedSnackbarText = stringResource(R.string.map_saved_snackbar, spot.name)
                val unsavedSnackbarText = stringResource(R.string.map_unsaved_snackbar, spot.name)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.map_selected_label),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.5.sp,
                        letterSpacing = 0.8.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = c.surface,
                        border = BorderStroke(1.dp, c.outline),
                        shadowElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SpotImage(
                                    spot = spot,
                                    emojiSize = 28.dp,
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = spot.name,
                                        fontFamily = FrauncesFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = c.text
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = EjeSpotGold,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${spot.rating} · ${spot.municipality} (${stringResource(R.string.spot_distance, spot.distance)})",
                                            fontFamily = ManropeFontFamily,
                                            fontSize = 11.5.sp,
                                            color = c.textSoft
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = c.secondaryContainer
                                        ) {
                                            Text(
                                                text = "${SpotCategories.emoji(spot.category)} " +
                                                    stringResource(SpotCategories.labelRes(spot.category)),
                                                fontFamily = ManropeFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.5.sp,
                                                color = c.secondary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = c.successBg
                                        ) {
                                            Text(
                                                text = spot.priceBadgeRes?.let { stringResource(it) } ?: spot.priceBadge,
                                                fontFamily = ManropeFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.5.sp,
                                                color = c.successText,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { openDirections(context, spot) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    shape = RoundedCornerShape(100.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = c.primary),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.map_get_directions),
                                        fontFamily = ManropeFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = Color.White
                                    )
                                }

                                OutlinedButton(
                                    onClick = { onNavigateToSpotDetail(spot.id) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    shape = RoundedCornerShape(100.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = c.surface),
                                    border = BorderStroke(1.2.dp, c.outline),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.map_view_details),
                                        fontFamily = ManropeFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = c.text
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val isVisited = spot.id in visitedSpotIds
                                val isSaved = spot.id in savedSpotIds

                                OutlinedButton(
                                    onClick = {
                                        val now = !isVisited
                                        viewModel.toggleVisitedSpot(spot.id)
                                        coroutineScope.launch {
                                            snackbarHostState?.showSnackbar(
                                                if (now) {
                                                    visitedSnackbarText
                                                } else {
                                                    unvisitedSnackbarText
                                                }
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(100.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isVisited) c.successBg else c.surface
                                    ),
                                    border = BorderStroke(
                                        1.2.dp,
                                        if (isVisited) c.primary else c.outline
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = if (isVisited) {
                                            stringResource(R.string.common_visited_badge)
                                        } else {
                                            stringResource(R.string.map_mark_visited)
                                        },
                                        fontFamily = ManropeFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = if (isVisited) c.successText else c.text
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        val now = !isSaved
                                        viewModel.toggleSaveSpot(spot.id)
                                        coroutineScope.launch {
                                            snackbarHostState?.showSnackbar(
                                                if (now) {
                                                    savedSnackbarText
                                                } else {
                                                    unsavedSnackbarText
                                                }
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(100.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSaved) c.dangerBg else c.surface
                                    ),
                                    border = BorderStroke(
                                        1.2.dp,
                                        if (isSaved) c.dangerText else c.outline
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = if (isSaved) {
                                            stringResource(R.string.common_favorite_badge)
                                        } else {
                                            stringResource(R.string.map_save)
                                        },
                                        fontFamily = ManropeFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = if (isSaved) c.dangerText else c.text
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Crea y configura una instancia de [MapView] de osmdroid acoplada al ciclo de vida
 * de la Activity. Se recuerda con [remember] para no recrear el mapa en cada recomposición.
 */
@Composable
private fun rememberMapViewWithLifecycle(context: Context, darkTheme: Boolean): MapView {
    val mapView = remember {
        // User agent: OpenStreetMap exige identificar la app para evitar
        // bloqueos. osmdroid usa "osmdroid" por defecto y los servidores lo
        // rechazan ("Access blocked"), por eso se define uno propio.
        Configuration.getInstance().userAgentValue = USER_AGENT
        Configuration.getInstance().osmdroidBasePath =
            java.io.File(context.cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidTileCache =
            java.io.File(context.cacheDir, "osmdroid/tiles")

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(DEFAULT_ZOOM)
            controller.setCenter(EJE_CAFETERO_CENTER)
            minZoomLevel = 4.0
            maxZoomLevel = 19.0
            // Controles nativos: brújula y capa de ubicación (se activa al conceder el permiso).
            overlays.add(CompassOverlay(context, this))
            // La capa de ubicación queda creada pero inactiva: el botón "mi ubicación"
            // de MapScreen la activa solo después de conceder el permiso en tiempo de ejecución.
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
            overlays.add(locationOverlay)
        }
    }

    // Aplica el filtro de color oscuro al mapa cuando el tema está en modo oscuro.
    LaunchedEffect(darkTheme) {
        // osmdroid no tiene tema oscuro nativo: invertimos los colores del mapa.
        @Suppress("DEPRECATION")
        mapView.overlayManager.tilesOverlay.setColorFilter(
            if (darkTheme) {
                android.graphics.ColorMatrixColorFilter(
                    android.graphics.ColorMatrix(
                        floatArrayOf(
                            -0.9f, 0f, 0f, 0f, 255f,
                            0f, -0.9f, 0f, 0f, 255f,
                            0f, 0f, -0.9f, 0f, 255f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            } else {
                null
            }
        )
        mapView.invalidate()
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose { mapView.onPause() }
    }

    return mapView
}

/** Ícono de marcador por defecto de osmdroid. */
private fun Context.defaultMarker(): Drawable =
    androidx.core.content.ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.marker_default)
        ?: android.graphics.drawable.ColorDrawable(0xFF3D6B4F.toInt())

/**
 * Activa la capa de ubicación (si aún no está activa) y centra el mapa
 * en la posición real del usuario. Requiere que el permiso de ubicación
 * ya haya sido concedido.
 */
private fun MapView.centerOnMyLocation() {
    val overlay = overlays.filterIsInstance<MyLocationNewOverlay>().firstOrNull()
        ?: return
    if (!overlay.isMyLocationEnabled) {
        overlay.enableMyLocation()
    }
    overlay.enableFollowLocation()
    overlay.myLocation?.let { controller.animateTo(it, 15.0, 800L) }
    invalidate()
}

/** Círculo de color para marcadores de estado (visitado / guardado). */
private fun Context.circleMarker(color: Long): Drawable {
    val size = (44 * resources.displayMetrics.density).toInt().coerceAtLeast(24)
    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color.toInt()
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, paint)
    paint.style = android.graphics.Paint.Style.STROKE
    paint.strokeWidth = 4f
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, paint)
    return android.graphics.drawable.BitmapDrawable(resources, bitmap)
}

/** Pin con forma de gota y el color del lugar (destaca el lugar seleccionado). */
private fun Context.pinMarker(color: Long, selected: Boolean): Drawable {
    val size = ((if (selected) 56 else 44) * resources.displayMetrics.density).toInt().coerceAtLeast(24)
    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color.toInt()
        style = android.graphics.Paint.Style.FILL
    }
    val r = size / 2f - 4f
    canvas.drawCircle(size / 2f, size / 2f, r, paint)
    paint.style = android.graphics.Paint.Style.STROKE
    paint.strokeWidth = if (selected) 8f else 5f
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(size / 2f, size / 2f, r, paint)
    return android.graphics.drawable.BitmapDrawable(resources, bitmap)
}
