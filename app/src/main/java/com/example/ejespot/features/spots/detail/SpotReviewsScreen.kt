package com.example.ejespot.features.spots.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import com.example.ejespot.ui.theme.*

@Composable
fun SpotReviewsScreen(
    spot: Spot?,
    padding: PaddingValues = PaddingValues(),
    snackbarHostState: SnackbarHostState? = null,
    onNavigateBack: () -> Unit,
    onAddReview: ((SpotReview) -> Unit)? = null
) {
    val c = ejeSpotColors()
    if (spot == null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.detail_spot_not_found), fontFamily = ManropeFontFamily)
        }
        return
    }

    // Textos localizados de las reseñas de muestra de la guía visual (Image 3)
    val mockBadge1 = stringResource(R.string.reviews_mock_1_badge)
    val mockComment1 = stringResource(R.string.reviews_mock_1_comment)
    val mockBadge2 = stringResource(R.string.reviews_mock_2_badge)
    val mockComment2 = stringResource(R.string.reviews_mock_2_comment)
    val mockBadge3 = stringResource(R.string.reviews_mock_3_badge)
    val mockComment3 = stringResource(R.string.reviews_mock_3_comment)

    // Si el spot tiene pocas reseñas de muestra, le sumamos las de la guía visual (Image 3)
    val defaultMockReviews = remember(mockBadge1, mockComment1, mockBadge2, mockComment2, mockBadge3, mockComment3) {
        listOf(
            SpotReview(
                id = "mock_rev_1",
                authorName = "Renata Castaño",
                authorBadge = mockBadge1,
                avatarInitials = "RC",
                rating = 5,
                comment = mockComment1
            ),
            SpotReview(
                id = "mock_rev_2",
                authorName = "Javier Velásquez",
                authorBadge = mockBadge2,
                avatarInitials = "JV",
                rating = 4,
                comment = mockComment2
            ),
            SpotReview(
                id = "mock_rev_3",
                authorName = "María Paz",
                authorBadge = mockBadge3,
                avatarInitials = "MP",
                rating = 5,
                comment = mockComment3
            )
        )
    }

    val allReviews = remember(spot.reviews) {
        if (spot.reviews.size >= 3) spot.reviews else spot.reviews + defaultMockReviews
    }

    Scaffold(
        containerColor = EjeSpotBackground,
        modifier = Modifier.padding(padding)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Barra Superior: Botón Volver + Título "Reseñas"
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Surface(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = c.surface,
                    border = BorderStroke(1.dp, c.outlineSoft),
                    shadowElevation = 1.dp
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

                Text(
                    text = stringResource(R.string.detail_reviews_title),
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = c.text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta Resumen de Calificación (Imagen 3: 4.8 ★★★★★ 128 reseñas)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = c.surface,
                border = BorderStroke(1.dp, c.outlineSoft),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${spot.rating}",
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 44.sp,
                        color = c.text
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (star <= spot.rating.toInt()) Color(0xFFF0C868) else c.starIdle,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.detail_reviews_count, spot.reviewCount),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = c.placeholder
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lista de Reseñas de la Comunidad
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(allReviews) { review ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = c.surface,
                        border = BorderStroke(1.dp, c.outlineSoft),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar circular
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(c.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = review.avatarInitials,
                                        fontFamily = ManropeFontFamily,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = c.successText
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

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
            }
        }
    }
}
