package com.example.ejespot.features.spots.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.ui.theme.*

@Composable
fun SpotDetailScreen(
    spot: Spot?,
    padding: PaddingValues = PaddingValues(),
    onNavigateBack: () -> Unit
) {
    var isSaved by remember { mutableStateOf(false) }

    if (spot == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Punto de interés no encontrado",
                fontFamily = ManropeFontFamily,
                color = Color(0xFF1F1A15)
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(bottom = padding.calculateBottomPadding())
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Image con botones flotantes (Atrás y Guardar)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(EjeSpotPrimaryContainer)
        ) {
            // Fotografía real del spot
            androidx.compose.foundation.Image(
                painter = painterResource(id = spot.imageResId),
                contentDescription = spot.name,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Botón Volver (superior izquierda)
            Surface(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(start = 16.dp, top = 40.dp)
                    .size(40.dp)
                    .align(Alignment.TopStart),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 2.dp
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

            // Botón Guardar / Favorito (superior derecha)
            Surface(
                onClick = { isSaved = !isSaved },
                modifier = Modifier
                    .padding(end = 16.dp, top = 40.dp)
                    .size(40.dp)
                    .align(Alignment.TopEnd),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Guardar",
                        tint = if (isSaved) EjeSpotSecondary else Color(0xFF1F1A15),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Cuerpo de información del spot
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(EjeSpotBackground)
                .padding(20.dp)
        ) {
            // Badges Categoría y Precio
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = EjeSpotSecondaryContainer
                ) {
                    Text(
                        text = spot.category,
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EjeSpotSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = EjeSpotPrimaryContainer
                ) {
                    Text(
                        text = spot.priceBadge,
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EjeSpotPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (spot.isVerified) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = EjeSpotPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Verificado",
                            fontFamily = ManropeFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EjeSpotPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Título del Spot
            Text(
                text = spot.name,
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF1F1A15)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Ubicación y calificación
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location_icon),
                    contentDescription = null,
                    tint = Color(0xFF6E6252),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${spot.location} · ",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    color = Color(0xFF6E6252)
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFF0C868),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${spot.rating} (${spot.distance})",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1A15)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila de información clave (Horario, Autor)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EjeSpotOutline)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "HORARIOS",
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF887D70)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = spot.hours,
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1A15)
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EjeSpotOutline)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "APORTADO POR",
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF887D70)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = spot.addedBy,
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = EjeSpotPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Descripción
            Text(
                text = "Descripción",
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1F1A15)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = spot.description,
                fontFamily = ManropeFontFamily,
                fontSize = 13.5.sp,
                color = Color(0xFF2E241C),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Acciones: Cómo llegar y Dejar reseña
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { /* TODO: Abrir mapa */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cómo llegar",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = { /* TODO: Dejar reseña */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, EjeSpotOutline)
                ) {
                    Icon(Icons.Default.RateReview, contentDescription = null, tint = EjeSpotPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reseñar (+25 XP)",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF1F1A15)
                    )
                }
            }
        }
    }
}
