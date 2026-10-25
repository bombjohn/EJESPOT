package com.example.ejespot.features.spots.visited

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.features.spots.list.SpotCard
import com.example.ejespot.features.spots.list.SpotListViewModel
import com.example.ejespot.ui.theme.*

/**
 * Identificadores internos de los filtros de la pantalla "Mis Lugares".
 * No son texto visible: la etiqueta que se muestra sale de los recursos.
 */
private const val FILTER_VISITED = "visited"
private const val FILTER_FAVORITES = "favorites"

/**
 * Pantalla "Mis Lugares".
 *
 * Muestra los lugares que el usuario actual marcó como visitados o guardó
 * desde el detalle, el mapa o el feed. Los datos provienen de [SpotStore]
 * (compartido) y son independientes por cuenta.
 */
@Composable
fun VisitedScreen(
    padding: PaddingValues = PaddingValues(),
    snackbarHostState: SnackbarHostState? = null,
    onNavigateToSpotDetail: (String) -> Unit,
    viewModel: SpotListViewModel
) {
    val c = ejeSpotColors()
    val uiState by viewModel.uiState.collectAsState()
    val visitedSpotIds by viewModel.visitedSpotIds.collectAsState()
    val savedSpotIds by viewModel.savedSpotIds.collectAsState()

    val allSpots = uiState.spots + uiState.pendingSpots

    val visitedSpots = remember(visitedSpotIds, allSpots) {
        allSpots.filter { it.id in visitedSpotIds }
    }
    val savedSpots = remember(savedSpotIds, allSpots) {
        allSpots.filter { it.id in savedSpotIds }
    }

    var selectedFilter by remember { mutableStateOf(FILTER_VISITED) }
    val filters = listOf(FILTER_VISITED, FILTER_FAVORITES)

    val displayedSpots = when (selectedFilter) {
        FILTER_FAVORITES -> savedSpots
        else -> visitedSpots
    }

    val departmentsCount = remember(visitedSpots) {
        visitedSpots.map { it.department }.distinct().size
    }
    val savedPendingCount = remember(savedSpots, visitedSpotIds) {
        savedSpots.count { it.id !in visitedSpotIds }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.background)
            .padding(padding)
    ) {
        // Encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.visited_title),
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = c.text
                    )
                    Text(
                        text = if (SessionManager.isGuest) {
                            stringResource(R.string.visited_subtitle_guest)
                        } else {
                            stringResource(R.string.visited_subtitle_user, SessionManager.displayName)
                        },
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = c.textSoft
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = c.surfaceVariant,
                    border = BorderStroke(1.dp, c.outlineSoft)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = EjeSpotPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.visited_count, visitedSpots.size),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = c.text
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tarjetas de estadísticas reales del usuario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    value = "${visitedSpots.size}",
                    label = stringResource(R.string.visited_tab_visited),
                    valueColor = EjeSpotPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "${savedSpots.size}",
                    label = stringResource(R.string.visited_stat_favorites),
                    valueColor = c.dangerText,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "$departmentsCount",
                    label = stringResource(R.string.visited_stat_departments),
                    valueColor = c.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            if (visitedSpots.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(
                        R.string.visited_progress,
                        ((visitedSpots.size * 100f) / allSpots.size.coerceAtLeast(1)).toInt()
                    ),
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.5.sp,
                    color = c.placeholder
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selector de filtro
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    val count = if (filter == FILTER_FAVORITES) savedSpots.size else visitedSpots.size
                    val filterLabel = if (filter == FILTER_FAVORITES) {
                        stringResource(R.string.visited_stat_favorites)
                    } else {
                        stringResource(R.string.visited_tab_visited)
                    }
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) EjeSpotPrimary else c.chipIdle,
                        border = BorderStroke(1.dp, if (isSelected) EjeSpotPrimary else c.outline),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = "$filterLabel ($count)",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else c.textMuted,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Lista o estado vacío
        if (displayedSpots.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (selectedFilter == FILTER_FAVORITES) "🤍" else "🧭",
                        fontSize = 52.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (selectedFilter == FILTER_FAVORITES) {
                            stringResource(R.string.visited_empty_favorites)
                        } else {
                            stringResource(R.string.visited_empty_visited)
                        },
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = c.text
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (selectedFilter == FILTER_FAVORITES) {
                            stringResource(R.string.visited_empty_favorites_hint)
                        } else {
                            stringResource(R.string.visited_empty_visited_hint)
                        },
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = c.placeholder,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(displayedSpots, key = { it.id }) { spot ->
                    SpotCard(
                        spot = spot,
                        isVisited = spot.id in visitedSpotIds,
                        onClick = { onNavigateToSpotDetail(spot.id) }
                    )
                }

                if (selectedFilter == FILTER_FAVORITES && savedPendingCount > 0) {
                    item {
                        Text(
                            text = stringResource(R.string.visited_saved_pending, savedPendingCount),
                            fontFamily = ManropeFontFamily,
                            fontSize = 11.5.sp,
                            color = c.placeholder,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    val c = ejeSpotColors()
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = c.surface,
        border = BorderStroke(1.dp, c.outline),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = value,
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = valueColor
            )
            Text(
                text = label,
                fontFamily = ManropeFontFamily,
                fontSize = 11.5.sp,
                color = c.textSoft
            )
        }
    }
}
