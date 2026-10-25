package com.example.ejespot.features.dashboard.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.features.dashboard.profile.components.ProfileChip
import com.example.ejespot.features.dashboard.profile.components.ProfileEmptyState
import com.example.ejespot.features.dashboard.profile.components.ProfileTopBar
import com.example.ejespot.ui.theme.*

@Composable
fun MyReviewsScreen(
    padding: PaddingValues = PaddingValues(),
    viewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState? = null,
    onNavigateBack: () -> Unit,
    onOpenSpot: (String) -> Unit
) {
    val c = ejeSpotColors()
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("Todas") }

    val filtered = remember(uiState.reviews, selectedFilter) {
        when (selectedFilter) {
            "5 estrellas" -> uiState.reviews.filter { it.rating >= 5.0 }
            "4 estrellas" -> uiState.reviews.filter { it.rating >= 4.0 && it.rating < 5.0 }
            else -> uiState.reviews
        }
    }

    val totalXp = remember(uiState.reviews) { uiState.reviews.sumOf { it.xpEarned } }
    val averageRating = remember(uiState.reviews) {
        if (uiState.reviews.isEmpty()) 0.0
        else uiState.reviews.map { it.rating }.average()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
    ) {
        ProfileTopBar(title = stringResource(R.string.my_reviews_title), onBack = onNavigateBack)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(R.string.my_reviews_published_by, uiState.profile.name),
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                color = c.textSoft
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                SummaryCard(
                    value = uiState.reviews.size.toString(),
                    label = stringResource(R.string.profile_stats_reviews),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    value = averageRating.toString().take(3),
                    label = stringResource(R.string.my_reviews_average),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    value = "+$totalXp",
                    label = stringResource(R.string.my_reviews_xp_earned),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileChip(
                    text = stringResource(R.string.my_reviews_filter_all),
                    selected = selectedFilter == "Todas",
                    onClick = { selectedFilter = "Todas" }
                )
                ProfileChip(
                    text = stringResource(R.string.my_reviews_filter_5),
                    selected = selectedFilter == "5 estrellas",
                    onClick = { selectedFilter = "5 estrellas" }
                )
                ProfileChip(
                    text = stringResource(R.string.my_reviews_filter_4),
                    selected = selectedFilter == "4 estrellas",
                    onClick = { selectedFilter = "4 estrellas" }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filtered.isEmpty()) {
            ProfileEmptyState(
                emoji = "✍️",
                title = stringResource(R.string.my_reviews_empty_filter_title),
                message = stringResource(R.string.my_reviews_empty_filter_message)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                items(filtered, key = { it.id }) { review ->
                    ReviewCard(
                        review = review,
                        onOpenSpot = { spotId -> onOpenSpot(spotId) },
                        onToggleHelpful = { viewModel.toggleReviewHelpful(review.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(value: String, label: String, modifier: Modifier = Modifier) {
    val c = ejeSpotColors()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(13.dp),
        color = c.surface,
        border = BorderStroke(1.dp, c.outline)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = c.primary
            )
            Text(
                text = label,
                fontFamily = ManropeFontFamily,
                fontSize = 9.5.sp,
                color = c.textSoft
            )
        }
    }
}

@Composable
private fun ReviewCard(
    review: com.example.ejespot.domain.model.MyReview,
    onOpenSpot: (String) -> Unit,
    onToggleHelpful: () -> Unit
) {
    val isClickable = review.spotId != null
    val c = ejeSpotColors()

    Surface(
        onClick = {
            review.spotId?.let(onOpenSpot)
        },
        enabled = isClickable,
        shape = RoundedCornerShape(15.dp),
        color = c.surface,
        border = BorderStroke(1.dp, c.outlineSoft),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.spotName,
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = c.text,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        // `MyReview.category` guarda la ubicación del lugar, no una categoría.
                        text = "${review.category} · ${review.dateLabelRes?.let { stringResource(it) } ?: review.dateLabel}",
                        fontFamily = ManropeFontFamily,
                        fontSize = 10.5.sp,
                        color = c.placeholder
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF7E7BE)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFE0A400),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = review.rating.toString(),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFF7A5A00)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(9.dp))

            Text(
                text = review.commentRes?.let { stringResource(it) } ?: review.comment,
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                color = c.textMuted,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(11.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (review.isMarkedHelpful) c.successBg else c.surfaceVariant,
                    border = BorderStroke(
                        1.dp,
                        if (review.isMarkedHelpful) c.successBg else c.outlineSoft
                    ),
                    onClick = onToggleHelpful
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ThumbUp,
                            contentDescription = stringResource(R.string.my_reviews_helpful_description),
                            tint = if (review.isMarkedHelpful) c.primary else c.placeholder,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.my_reviews_helpful_votes, review.helpfulCount),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = if (review.isMarkedHelpful) c.primary else c.textSoft
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = c.primaryContainer
                ) {
                    Text(
                        text = stringResource(R.string.my_reviews_xp_earned_badge, review.xpEarned),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.5.sp,
                        color = c.primary,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
            }

            if (isClickable) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { review.spotId?.let(onOpenSpot) },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.my_reviews_view_spot),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        color = c.secondary
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = c.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
