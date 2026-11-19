package com.example.ejespot.core.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ejespot.core.util.toImageBitmap
import com.example.ejespot.domain.model.Spot

/**
 * Imagen de un punto de interés.
 *
 * Si el usuario cargó una foto (cámara o galería) o URL en la nube, se visualiza
 * directamente; si tiene foto empaquetada se muestra esa; de lo contrario,
 * se dibuja una cabecera ilustrada con degradado y el emoji de la categoría.
 */
@Composable
fun SpotImage(
    spot: Spot,
    modifier: Modifier = Modifier,
    emojiSize: Dp = 34.dp
) {
    val context = LocalContext.current
    val customBitmap = remember(spot.imageUri) {
        spot.imageUri?.let { uriStr ->
            runCatching { Uri.parse(uriStr).toImageBitmap(context) }.getOrNull()
        }
    }

    if (customBitmap != null) {
        Image(
            bitmap = customBitmap.asImageBitmap(),
            contentDescription = spot.name,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else if (!spot.imageUrl.isNullOrBlank() &&
        (spot.imageUrl.startsWith("http://") || spot.imageUrl.startsWith("https://"))
    ) {
        // Solo URL públicas: un content:// viejo no se puede cargar y dejaría
        // el hueco en blanco en vez de caer a la ilustración de respaldo.
        AsyncImage(
            model = spot.imageUrl,
            contentDescription = spot.name,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else if (spot.imageResId != null) {
        Image(
            painter = painterResource(id = spot.imageResId),
            contentDescription = spot.name,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        SpotIllustration(
            emoji = spot.emoji,
            accentColor = Color(spot.accentColor),
            modifier = modifier,
            emojiSize = emojiSize
        )
    }
}

/** Fondo decorativo con degradado + emoji, usado cuando no hay foto. */
@Composable
fun SpotIllustration(
    emoji: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    emojiSize: Dp = 34.dp
) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        accentColor,
                        accentColor.copy(alpha = 0.78f),
                        Color(0xFF1F1A15).copy(alpha = 0.72f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Halo suave para dar profundidad al emoji.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.22f), Color.Transparent)
                    )
                )
        )
        Text(
            text = emoji,
            fontSize = emojiSize.value.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Emoji asociado a la categoría del lugar. */
@Composable
fun SpotCategoryEmoji(spot: Spot) = Text(text = spot.emoji, fontSize = 16.sp, modifier = Modifier.size(20.dp))
