package com.example.ejespot.features.spots.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.R
import com.example.ejespot.core.ui.SpotImage
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
    onNavigateToReviews: ((String) -> Unit)? = null,
    onNavigateToReport: ((String) -> Unit)? = null,
    viewModel: SpotDetailViewModel = viewModel()
) {
    val c = ejeSpotColors()
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
                text = stringResource(R.string.detail_not_found),
                fontFamily = ManropeFontFamily,
                color = c.text
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
            SpotImage(
                spot = currentSpot,
                emojiSize = 78.dp,
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
                color = c.surface.copy(alpha = 0.92f),
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_back),
                        tint = c.text,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Botón Reportar flotante (Pantalla 4.6)
            Surface(
                onClick = { onNavigateToReport?.invoke(currentSpot.id) },
                modifier = Modifier
                    .padding(end = 66.dp, top = 40.dp)
                    .size(42.dp)
                    .align(Alignment.TopEnd),
                shape = CircleShape,
                color = c.surface.copy(alpha = 0.92f),
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = stringResource(R.string.detail_report),
                        tint = c.dangerText,
                        modifier = Modifier.size(19.dp)
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
                color = c.surface.copy(alpha = 0.92f),
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (uiState.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = stringResource(R.string.common_save),
                        tint = if (uiState.isSaved) EjeSpotSecondary else c.text,
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
                    color = c.surfaceVariant,
                    border = BorderStroke(1.dp, c.outline)
                ) {
                    Text(
                        text = "${com.example.ejespot.domain.model.SpotCategories.emoji(currentSpot.category)} " +
                            stringResource(
                                com.example.ejespot.domain.model.SpotCategories.labelRes(currentSpot.category)
                            ),
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textSoft,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = EjeSpotPrimaryContainer
                ) {
                    Text(
                        text = currentSpot.priceBadgeRes?.let { stringResource(it) } ?: currentSpot.priceBadge,
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
                            text = stringResource(R.string.detail_verified_by_moderator),
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
                color = c.text,
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
                    text = stringResource(
                        R.string.detail_rating_reviews,
                        "${currentSpot.rating}",
                        currentSpot.reviewCount
                    ),
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = c.text
                )
                Text(
                    text = "·",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    color = c.placeholder
                )
                Text(
                    text = "${currentSpot.municipality} · ${stringResource(R.string.spot_distance, currentSpot.distance)}",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    color = c.textSoft
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
                    color = c.surface,
                    border = BorderStroke(1.dp, c.outlineSoft)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.detail_hours_label),
                            fontFamily = ManropeFontFamily,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = c.placeholder,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentSpot.hoursRes?.let { stringResource(it) } ?: currentSpot.hours,
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = c.text
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = c.surface,
                    border = BorderStroke(1.dp, c.outlineSoft)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.detail_price_label),
                            fontFamily = ManropeFontFamily,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = c.placeholder,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentSpot.priceRangeRes?.let { stringResource(it) } ?: currentSpot.priceRange,
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = c.text
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sección: Sobre este lugar
            Text(
                text = stringResource(R.string.detail_about),
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = c.text
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = currentSpot.descriptionRes?.let { stringResource(it) } ?: currentSpot.description,
                fontFamily = ManropeFontFamily,
                fontSize = 13.5.sp,
                color = c.textMuted,
                lineHeight = 21.sp
            )

            // --- 3. FICHA TÉCNICA / INFORMACIÓN ESPECIALIZADA ---
            if (currentSpot.technicalInfo != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = c.surface,
                    border = BorderStroke(1.dp, c.outlineSoft)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentSpot.technicalTitleRes?.let { stringResource(it) }
                                ?: stringResource(R.string.detail_technical_default),
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = c.placeholder,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Grid 2x2
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(currentSpot.technicalInfo.item1Label),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = c.text
                                )
                                Text(
                                    text = stringResource(currentSpot.technicalInfo.item1Value),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 12.sp,
                                    color = c.textSoft
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(currentSpot.technicalInfo.item2Label),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = c.text
                                )
                                Text(
                                    text = stringResource(currentSpot.technicalInfo.item2Value),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 12.sp,
                                    color = c.textSoft
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(currentSpot.technicalInfo.item3Label),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = c.text
                                )
                                Text(
                                    text = stringResource(currentSpot.technicalInfo.item3Value),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 12.sp,
                                    color = c.textSoft
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(currentSpot.technicalInfo.item4Label),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = c.text
                                )
                                Text(
                                    text = stringResource(currentSpot.technicalInfo.item4Value),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 12.sp,
                                    color = c.textSoft
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
                    text = currentSpot.specialtiesTitleRes?.let { stringResource(it) }
                        ?: stringResource(R.string.detail_specialties_default),
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = c.text
                )
                Spacer(modifier = Modifier.height(10.dp))

                currentSpot.specialties.forEach { spec ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = c.surface,
                        border = BorderStroke(1.dp, c.outlineSoft)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(spec.nameRes),
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = c.text
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(spec.descriptionRes),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    color = c.textSoft
                                )
                            }
                            Text(
                                text = stringResource(spec.priceRes),
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
                    text = currentSpot.highlightsTitleRes?.let { stringResource(it) }
                        ?: stringResource(R.string.detail_highlights_default),
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = c.text
                )
                Spacer(modifier = Modifier.height(10.dp))

                currentSpot.highlights.forEach { hl ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = c.surface,
                        border = BorderStroke(1.dp, c.outlineSoft)
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
                                    text = stringResource(hl.titleRes),
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = c.text
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(hl.subtitleRes),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    color = c.textSoft
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
                        text = if (uiState.isVisited) stringResource(R.string.detail_visited_xp)
                        else stringResource(R.string.detail_mark_visited),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color.White
                    )
                }

                // Mensaje de compartir, resuelto aquí (contexto @Composable).
                val shareMessage = stringResource(
                    R.string.detail_share_message,
                    currentSpot.name,
                    currentSpot.municipality
                )
                val shareChooserTitle = stringResource(R.string.detail_share_chooser)

                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareMessage)
                            type = "text/plain"
                        }
                        context.startActivity(
                            Intent.createChooser(sendIntent, shareChooserTitle)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = c.surfaceVariant),
                    border = BorderStroke(1.dp, c.outline)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = c.textSoft,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.detail_share),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = c.textSoft
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- 7. EXPERIENCIAS DE LA COMUNIDAD (RESEÑAS REALES) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.detail_community_experiences),
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = c.text
                )

                Text(
                    text = stringResource(R.string.detail_see_all_count, currentSpot.reviewCount),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = EjeSpotPrimary,
                    modifier = Modifier.clickable { onNavigateToReviews?.invoke(currentSpot.id) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (currentSpot.reviews.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = c.surface,
                    border = BorderStroke(1.dp, c.outlineSoft)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.detail_no_reviews),
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            color = c.placeholder
                        )
                        Text(
                            text = stringResource(R.string.detail_first_review),
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
                color = c.surfaceRaised,
                border = BorderStroke(1.dp, c.outline)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = null,
                        tint = c.textSoft,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.detail_write_review_xp),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = c.textSoft
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón GPS / Cómo llegar
            OutlinedButton(
                onClick = {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(currentSpot.name + ", " + currentSpot.municipality)}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        val webIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(currentSpot.name + ", " + currentSpot.municipality)}")
                        )
                        context.startActivity(webIntent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = c.surface),
                border = BorderStroke(1.dp, c.outline)
            ) {
                Text(
                    text = stringResource(R.string.detail_directions_gps),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = c.successText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botón / Enlace: Reportar o denunciar contenido (Pantalla 4.6)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToReport?.invoke(currentSpot.id) }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = c.dangerText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.detail_report_info),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = c.dangerText
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
                    text = stringResource(R.string.detail_review_dialog_title),
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = c.text
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.detail_review_question, currentSpot.name),
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = c.textSoft
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
                                contentDescription = stringResource(R.string.detail_star_count, starIndex),
                                tint = if (starIndex <= uiState.reviewRating) Color(0xFFF0C868) else c.starIdle,
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
                        label = { Text(stringResource(R.string.detail_review_author_label)) },
                        placeholder = { Text(stringResource(R.string.detail_review_author_placeholder)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.reviewComment,
                        onValueChange = { viewModel.onReviewCommentChanged(it) },
                        label = { Text(stringResource(R.string.detail_review_comment_label)) },
                        placeholder = { Text(stringResource(R.string.detail_review_comment_placeholder)) },
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
                        text = stringResource(R.string.detail_review_publish),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setReviewDialogVisible(false) }) {
                    Text(
                        text = stringResource(R.string.common_cancel),
                        fontFamily = ManropeFontFamily,
                        color = c.textSoft
                    )
                }
            }
        )
    }
}

@Composable
private fun ReviewCard(review: SpotReview) {
    val c = ejeSpotColors()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = c.surface,
        border = BorderStroke(1.dp, c.outlineSoft)
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
                        .background(c.avatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.avatarInitials,
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = c.successText
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.authorName,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = c.text
                    )
                    Text(
                        text = review.authorBadgeRes?.let { stringResource(it) } ?: review.authorBadge,
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        color = c.placeholder
                    )
                }

                // Estrellas
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (star <= review.rating) Color(0xFFF0C868) else c.starIdle,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Comentario
            Text(
                text = review.commentRes?.let { stringResource(it) } ?: review.comment,
                fontFamily = ManropeFontFamily,
                fontSize = 12.5.sp,
                color = c.textMuted,
                lineHeight = 18.sp
            )
        }
    }
}
