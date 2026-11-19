package com.example.ejespot.features.spots.create

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.ejespot.R
import com.example.ejespot.ui.theme.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/** Centro del Eje Cafetero cuando aún no hay una ubicación elegida. */
private val EJE_CENTER = GeoPoint(4.8087, -75.6903)
private const val PICKER_USER_AGENT =
    "EjeSpot/1.0 (Android; com.example.ejespot; selector de ubicación)"

/** Mapa base claro tipo Voyager (más legible y bonito que el Mapnik clásico). */
internal val CartoVoyager = XYTileSource(
    "CartoVoyager", 0, 19, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://b.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://c.basemaps.cartocdn.com/rastertiles/voyager/"
    ),
    "© OpenStreetMap contributors © CARTO"
)

internal val CartoDark = XYTileSource(
    "CartoDark", 0, 19, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/dark_all/",
        "https://b.basemaps.cartocdn.com/dark_all/",
        "https://c.basemaps.cartocdn.com/dark_all/"
    ),
    "© OpenStreetMap contributors © CARTO"
)

/**
 * Selector de ubicación real: el usuario toca el mapa y coloca el marcador
 * exacto del nuevo spot, en vez de elegir una ciudad genérica.
 */
@Composable
fun LocationPickerDialog(
    initialLatitude: Double? = null,
    initialLongitude: Double? = null,
    onDismiss: () -> Unit,
    onConfirm: (latitude: Double, longitude: Double) -> Unit
) {
    val c = ejeSpotColors()
    val context = LocalContext.current
    var picked by remember {
        mutableStateOf(
            if (initialLatitude != null && initialLongitude != null)
                GeoPoint(initialLatitude, initialLongitude)
            else null
        )
    }

    val mapView = remember {
        Configuration.getInstance().userAgentValue = PICKER_USER_AGENT
        Configuration.getInstance().osmdroidBasePath = java.io.File(context.cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidTileCache =
            java.io.File(context.cacheDir, "osmdroid/tiles")
        MapView(context).apply {
            setTileSource(if (c.isDark) CartoDark else CartoVoyager)
            setMultiTouchControls(true)
            controller.setZoom(if (picked != null) 15.0 else 11.0)
            controller.setCenter(picked ?: EJE_CENTER)
            minZoomLevel = 5.0
            maxZoomLevel = 19.0
        }
    }

    val marker = remember {
        Marker(mapView).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = context.getString(R.string.location_picker_marker_title)
            icon = context.pickerMarker()
        }
    }

    // El marcador solo se añade cuando hay una posición elegida.
    LaunchedEffect(picked) {
        mapView.overlays.removeAll { it is Marker }
        picked?.let { point ->
            marker.position = point
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose { mapView.onPause() }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = c.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 460.dp, max = 620.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Cabecera
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.location_picker_title),
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = c.text,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = c.textSoft)
                    }
                }

                Text(
                    text = stringResource(R.string.location_picker_hint),
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.sp,
                    color = c.textSoft,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(c.surfaceVariant)
                ) {
                    AndroidView(
                        factory = { mapView },
                        modifier = Modifier.fillMaxSize()
                    )
                    // Un toque corto sobre el mapa coloca el marcador en esa
                    // coordenada real (osmdroid 6.x no expone onMapClick).
                    LaunchedEffect(mapView) {
                        mapView.setOnTouchListener { _, event ->
                            if (event.action == android.view.MotionEvent.ACTION_UP &&
                                event.eventTime - event.downTime < 400
                            ) {
                                val tapped = mapView.projection.fromPixels(
                                    event.x.toInt(),
                                    event.y.toInt()
                                ) as? GeoPoint
                                tapped?.let { picked = GeoPoint(it.latitude, it.longitude) }
                            }
                            false
                        }
                    }
                }

                // Coordenadas seleccionadas
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = c.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = c.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = picked?.let {
                                stringResource(
                                    R.string.location_picker_coordinates,
                                    String.format("%.6f", it.latitude),
                                    String.format("%.6f", it.longitude)
                                )
                            } ?: stringResource(R.string.location_picker_no_selection),
                            fontFamily = ManropeFontFamily,
                            fontSize = 11.5.sp,
                            color = c.text
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { picked = EJE_CENTER },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(100.dp),
                        border = BorderStroke(1.dp, c.outline)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.map_center_region), fontSize = 12.sp)
                    }
                    Button(
                        onClick = { picked?.let { onConfirm(it.latitude, it.longitude) } },
                        enabled = picked != null,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(44.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = c.primary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.location_picker_confirm), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/** Marcador de color de marca para el selector. */
private fun Context.pickerMarker(): Drawable =
    androidx.core.content.ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.marker_default)
        ?: android.graphics.drawable.ColorDrawable(0xFF3D6B4F.toInt())
