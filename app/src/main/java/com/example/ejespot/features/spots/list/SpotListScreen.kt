package com.example.ejespot.features.spots.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.R
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.ui.theme.*

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.launch

@Composable
fun SpotListScreen(
    padding: PaddingValues = PaddingValues(),
    snackbarHostState: SnackbarHostState? = null,
    onNavigateToSpotDetail: (String) -> Unit,
    onNavigateToCreateSpot: () -> Unit,
    onNavigateToNotifications: (() -> Unit)? = null,
    viewModel: SpotListViewModel = viewModel()
) {
    val c = ejeSpotColors()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val visitedIds by viewModel.visitedSpotIds.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val categories = com.example.ejespot.domain.model.SpotCategories.filterOptions

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(c.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding() + 80.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Wordmark + Notificaciones + Ubicación actual
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Eje ",
                                fontFamily = FrauncesFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = c.text
                            )
                            Text(
                                text = "Spot",
                                fontFamily = FrauncesFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = c.secondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        // Pill Ubicación
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = c.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_location_icon),
                                    contentDescription = null,
                                    tint = c.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.feed_location_pill),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = c.primary
                                )
                            }
                        }
                    }

                    // Botón de notificaciones
                    val noNotificationsMessage = stringResource(R.string.feed_no_notifications)
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        color = c.surfaceVariant,
                        onClick = {
                            if (onNavigateToNotifications != null) {
                                onNavigateToNotifications()
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState?.showSnackbar(noNotificationsMessage)
                                }
                            }
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = stringResource(R.string.feed_notifications),
                                tint = c.text,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Barra de búsqueda
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(100.dp))
                        .background(c.surface, RoundedCornerShape(100.dp))
                        .border(1.dp, c.outline, RoundedCornerShape(100.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = c.textSoft,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.searchQuery.isEmpty()) {
                            Text(
                                text = stringResource(R.string.feed_search_placeholder),
                                fontFamily = ManropeFontFamily,
                                fontSize = 13.sp,
                                color = c.placeholder
                            )
                        }
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                fontFamily = ManropeFontFamily,
                                fontSize = 13.sp,
                                color = c.text
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Chips de categorías filtrables
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = uiState.selectedCategory == category
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) c.primary else c.chipIdle,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) c.primary else c.outline
                            ),
                            modifier = Modifier.clickable { viewModel.onCategorySelected(category) }
                        ) {
                            Text(
                                text = stringResource(
                                    com.example.ejespot.domain.model.SpotCategories.labelRes(category)
                                ),
                                fontFamily = ManropeFontFamily,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else c.textMuted,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // Sección 1: Cerca de ti · Verificados
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.feed_section_nearby_verified),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = c.textMuted
                )
            }

            items(uiState.spots) { spot ->
                SpotCard(
                    spot = spot,
                    isVisited = spot.id in visitedIds,
                    onClick = { onNavigateToSpotDetail(spot.id) }
                )
            }

            // Sección 2: Pendientes por verificar
            if (uiState.pendingSpots.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.feed_section_pending),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = c.textMuted
                    )
                }

                items(uiState.pendingSpots) { spot ->
                    SpotCard(
                        spot = spot,
                        isVisited = spot.id in visitedIds,
                        onClick = { onNavigateToSpotDetail(spot.id) }
                    )
                }
            }

            // Banner promocional "¿Descubriste un lugar increíble?"
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = EjeSpotPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.feed_banner_title),
                                fontFamily = FrauncesFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White,
                                lineHeight = 21.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.feed_banner_subtitle),
                                fontFamily = ManropeFontFamily,
                                fontSize = 11.5.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Button(
                            onClick = onNavigateToCreateSpot,
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = stringResource(R.string.feed_banner_button),
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.5.sp,
                                color = EjeSpotPrimary
                            )
                        }
                    }
                }
            }
        }

        // FAB flotante para agregar spot rápido
        FloatingActionButton(
            onClick = onNavigateToCreateSpot,
            containerColor = EjeSpotSecondary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = padding.calculateBottomPadding() + 16.dp, end = 16.dp)
                .size(54.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.feed_add_spot), modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun SpotCard(
    spot: Spot,
    isVisited: Boolean = false,
    onClick: () -> Unit
) {
    val c = ejeSpotColors()
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = c.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isVisited) c.successBg else c.outline
        ),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen real o cabecera ilustrada del spot
            Box(modifier = Modifier.size(76.dp)) {
                com.example.ejespot.core.ui.SpotImage(
                    spot = spot,
                    emojiSize = 28.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )

                if (isVisited) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFF2C5640),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.common_visited_badge),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 8.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = spot.name,
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = c.text
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Rating + Distancia + Badge Verificado
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFF0C868),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${spot.rating} · ${stringResource(R.string.spot_distance, spot.distance)}",
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textMuted
                    )

                    if (spot.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = c.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = stringResource(R.string.feed_verified),
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = c.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Badges: Categoría + Precio + Horario
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = c.secondaryContainer
                    ) {
                        Text(
                            text = stringResource(
                                com.example.ejespot.domain.model.SpotCategories.labelRes(spot.category)
                            ),
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = c.secondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = c.primaryContainer
                    ) {
                        Text(
                            text = spot.priceBadgeRes?.let { stringResource(it) } ?: spot.priceBadge,
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = c.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = spot.hoursRes?.let { stringResource(it) } ?: spot.hours,
                        fontFamily = ManropeFontFamily,
                        fontSize = 10.sp,
                        color = c.textSoft
                    )
                }
            }
        }
    }
}
