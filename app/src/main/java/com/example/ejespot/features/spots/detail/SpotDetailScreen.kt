package com.example.ejespot.features.spots.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.R
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import com.example.ejespot.ui.theme.*

@Composable
fun SpotDetailScreen(
    spot: Spot?,
    padding: PaddingValues = PaddingValues(),
    snackbarHostState: SnackbarHostState? = null,
    onNavigateBack: () -> Unit,
    onAddReview: ((SpotReview) -> Unit)? = null,
    viewModel: SpotDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(spot) {
        viewModel.setSpot(spot)
    }

    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let { msg ->
            snackbarHostState?.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    val currentSpot = uiState.spot ?: spot

    if (currentSpot == null) {
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
        // --- 1. HERO IMAGE CON BOTONES FLOTANTES ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(EjeSpotPrimaryContainer)
        ) {
            Image(
                painter = painterResource(id = currentSpot.imageResId),
                contentDescription = currentSpot.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Botón Volver
            Surface(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(start = 16.dp, top = 40.dp)
                    .size(42.dp)
                    .align(Alignment.TopStart),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.92f),
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color(0xFF1F1A15),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Botón Guardar / Favoritos
            Surface(
                onClick = { viewModel.toggleSaved() },
                modifier = Modifier
                    .padding(end = 16.dp, top = 40.dp)
                    .size(42.dp)
                    .align(Alignment.TopEnd),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.92f),
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (uiState.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Guardar",
                        tint = if (uiState.isSaved) EjeSpotSecondary else Color(0xFF1F1A15),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // --- 2. CONTENIDO PRINCIPAL EN HOJA SUPERPUESTA ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(EjeSpotBackground)
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            // Badges Categoría, Precio y Verificado
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color(0xFFF3ECE0),
                    border = BorderStroke(1.dp, Color(0xFFDECFC0))
                ) {
                    Text(
                        text = when (currentSpot.category) {
                            "Naturaleza" -> "🏕️ Naturaleza"
                            "Gastronomía" -> "☕ Gastronomía"
                            "Historia" -> "🏛️ Historia"
                            else -> "📍 ${currentSpot.category}"
                        },
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6E563B),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = EjeSpotPrimaryContainer
                ) {
                    Text(
                        text = currentSpot.priceBadge,
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = EjeSpotPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (currentSpot.isVerified) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = EjeSpotPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Verificado por Moderador",
                            fontFamily = ManropeFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EjeSpotPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Nombre del Spot
            Text(
                text = currentSpot.name,
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF1F1A15),
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Calificación, reseñas y ubicación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFF0C868),
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "${currentSpot.rating} (${currentSpot.reviewCount} reseñas)",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1A15)
                )
                Text(
                    text = "·",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    color = Color(0xFF887D70)
                )
                Text(
                    text = "${currentSpot.location} · ${currentSpot.distance}",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    color = Color(0xFF6E6252)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjetas Horario Sugerido y Rango de Precio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5DDD0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "HORARIO SUGERIDO",
                            fontFamily = ManropeFontFamily,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF887D70),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentSpot.hours,
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1A15)
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5DDD0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "RANGO DE PRECIO",
                            fontFamily = ManropeFontFamily,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF887D70),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentSpot.priceRange,
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1A15)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sección: Sobre este lugar
            Text(
                text = "Sobre este lugar",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1F1A15)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = currentSpot.description,
                fontFamily = ManropeFontFamily,
                fontSize = 13.5.sp,
                color = Color(0xFF3E352B),
                lineHeight = 21.sp
            )

            // --- 3. FICHA TÉCNICA / INFORMACIÓN ESPECIALIZADA ---
            if (currentSpot.technicalInfo != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5DDD0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentSpot.technicalTitle ?: "FICHA TÉCNICA",
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF887D70),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Grid 2x2
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentSpot.technicalInfo.item1Label,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1A15)
                                )
                                Text(
                                    text = currentSpot.technicalInfo.item1Value,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6E6252)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentSpot.technicalInfo.item2Label,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1A15)
                                )
                                Text(
                                    text = currentSpot.technicalInfo.item2Value,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6E6252)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentSpot.technicalInfo.item3Label,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1A15)
                                )
                                Text(
                                    text = currentSpot.technicalInfo.item3Value,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6E6252)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentSpot.technicalInfo.item4Label,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1A15)
                                )
                                Text(
                                    text = currentSpot.technicalInfo.item4Value,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6E6252)
                                )
                            }
                        }
                    }
                }
            }

            // --- 4. ESPECIALIDADES (GASTRONOMÍA) ---
            if (currentSpot.specialties.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = currentSpot.specialtiesTitle ?: "🍴 Especialidades de la Casa",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1F1A15)
                )
                Spacer(modifier = Modifier.height(10.dp))

                currentSpot.specialties.forEach { spec ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5DDD0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = spec.name,
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF1F1A15)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = spec.description,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF6E6252)
                                )
                            }
                            Text(
                                text = spec.price,
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = EjeSpotPrimary
                            )
                        }
                    }
                }
            }

            // --- 5. ATRACTIVOS DESTACADOS ---
            if (currentSpot.highlights.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = currentSpot.highlightsTitle ?: "🦅 Atractivos Destacados",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1F1A15)
                )
                Spacer(modifier = Modifier.height(10.dp))

                currentSpot.highlights.forEach { hl ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5DDD0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = hl.iconEmoji,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = hl.title,
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF1F1A15)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = hl.subtitle,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF6E6252)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 6. BOTONES DE ACCIÓN RÁPIDA (VISITAR Y COMPARTIR) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.toggleVisited() },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isVisited) Color(0xFF2B523A) else EjeSpotPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState.isVisited) "Visitado (+10 XP)" else "Marcar como visitado",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "¡Mira este increíble lugar en el Eje Cafetero! 🌿 ${currentSpot.name} (${currentSpot.location}). Descúbrelo en la app EjeSpot."
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Compartir lugar"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF3ECE0)),
                    border = BorderStroke(1.dp, Color(0xFFDECFC0))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color(0xFF6E563B),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Compartir",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color(0xFF6E563B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- 7. EXPERIENCIAS DE LA COMUNIDAD (RESEÑAS REALES) ---
            Text(
                text = "Experiencias de la comunidad",
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFF1F1A15)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (currentSpot.reviews.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5DDD0))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Aún no hay reseñas para este lugar.",
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFF887D70)
                        )
                        Text(
                            text = "¡Sé el primero en compartir tu experiencia y gana +25 XP!",
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EjeSpotPrimary
                        )
                    }
                }
            } else {
                currentSpot.reviews.forEach { review ->
                    ReviewCard(review = review)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botón: Escribir una reseña (+25 XP)
            Surface(
                onClick = { viewModel.setReviewDialogVisible(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(100.dp),
                color = Color(0xFFF7F2E9),
                border = BorderStroke(1.dp, Color(0xFFDCCFBD))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = null,
                        tint = Color(0xFF5A4833),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Escribir una reseña (+25 XP)",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF5A4833)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón GPS / Cómo llegar
            OutlinedButton(
                onClick = {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(currentSpot.name + ", " + currentSpot.location)}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        val webIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(currentSpot.name + ", " + currentSpot.location)}")
                        )
                        context.startActivity(webIntent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFDECFC0))
            ) {
                Text(
                    text = "🗺️ Cómo llegar (GPS)",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF2C5640)
                )
            }
        }
    }

    // --- DIÁLOGO MODAL: ESCRIBIR RESEÑA ---
    if (uiState.showReviewDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setReviewDialogVisible(false) },
            title = {
                Text(
                    text = "✍ Escribir Reseña",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = Color(0xFF1F1A15)
                )
            },
            text = {
                Column {
                    Text(
                        text = "¿Cómo fue tu experiencia en ${currentSpot.name}?",
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF6E6252)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Selector de estrellas interactivas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { starIndex ->
                            Icon(
                                imageVector = if (starIndex <= uiState.reviewRating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$starIndex estrellas",
                                tint = if (starIndex <= uiState.reviewRating) Color(0xFFF0C868) else Color(0xFFC7BCAB),
                                modifier = Modifier
                                    .size(34.dp)
                                    .clickable { viewModel.onReviewRatingChanged(starIndex) }
                                    .padding(2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = uiState.reviewAuthor,
                        onValueChange = { viewModel.onReviewAuthorChanged(it) },
                        label = { Text("Tu nombre / Apodo") },
                        placeholder = { Text("Ej. Juan Pérez") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.reviewComment,
                        onValueChange = { viewModel.onReviewCommentChanged(it) },
                        label = { Text("Tu opinión o recomendación") },
                        placeholder = { Text("Comparte tips de transporte, clima, comida...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitReview { newReview ->
                            onAddReview?.invoke(newReview)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary)
                ) {
                    Text(
                        text = "Publicar (+25 XP)",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setReviewDialogVisible(false) }) {
                    Text(
                        text = "Cancelar",
                        fontFamily = ManropeFontFamily,
                        color = Color(0xFF6E6252)
                    )
                }
            }
        )
    }
}

@Composable
private fun ReviewCard(review: SpotReview) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5DDD0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Fila de Encabezado: Avatar + Nombre + Insignia + Estrellas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circular con iniciales
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8DFD0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.avatarInitials,
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF634E35)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.authorName,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Color(0xFF1F1A15)
                    )
                    Text(
                        text = review.authorBadge,
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        color = Color(0xFF887D70)
                    )
                }

                // Estrellas
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (star <= review.rating) Color(0xFFF0C868) else Color(0xFFE0D8CB),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Comentario
            Text(
                text = review.comment,
                fontFamily = ManropeFontFamily,
                fontSize = 12.5.sp,
                color = Color(0xFF3E352B),
                lineHeight = 18.sp
            )
        }
    }
}
