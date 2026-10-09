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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.R
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.ui.theme.*

@Composable
fun SpotListScreen(
    padding: PaddingValues = PaddingValues(),
    onNavigateToSpotDetail: (String) -> Unit,
    onNavigateToCreateSpot: () -> Unit,
    viewModel: SpotListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = listOf("Todos", "Naturaleza", "Café", "Gastronomía", "Historia")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
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
                                color = Color(0xFF1F1A15)
                            )
                            Text(
                                text = "Spot",
                                fontFamily = FrauncesFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = EjeSpotSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        // Pill Ubicación
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = EjeSpotPrimaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_location_icon),
                                    contentDescription = null,
                                    tint = EjeSpotPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Salento, Quindío · Eje Cafetero",
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EjeSpotPrimary
                                )
                            }
                        }
                    }

                    // Botón de notificaciones
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        color = Color(0xFFEFE6D6)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones",
                                tint = Color(0xFF1F1A15),
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
                        .background(Color.White, RoundedCornerShape(100.dp))
                        .border(1.dp, EjeSpotOutline, RoundedCornerShape(100.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF6E6252),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.searchQuery.isEmpty()) {
                            Text(
                                text = "Buscar miradores, cafés, senderos...",
                                fontFamily = ManropeFontFamily,
                                fontSize = 13.sp,
                                color = Color(0xFF887D70)
                            )
                        }
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                fontFamily = ManropeFontFamily,
                                fontSize = 13.sp,
                                color = Color(0xFF1F1A15)
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
                            color = if (isSelected) EjeSpotPrimary else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) EjeSpotPrimary else EjeSpotOutline
                            ),
                            modifier = Modifier.clickable { viewModel.onCategorySelected(category) }
                        ) {
                            Text(
                                text = category,
                                fontFamily = ManropeFontFamily,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF2E241C),
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
                    text = "Cerca de ti · Verificados por la comunidad",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = Color(0xFF2E241C)
                )
            }

            items(uiState.spots) { spot ->
                SpotCard(spot = spot, onClick = { onNavigateToSpotDetail(spot.id) })
            }

            // Sección 2: Pendientes por verificar
            if (uiState.pendingSpots.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pendientes por verificar (Comunidad)",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color(0xFF2E241C)
                    )
                }

                items(uiState.pendingSpots) { spot ->
                    SpotCard(spot = spot, onClick = { onNavigateToSpotDetail(spot.id) })
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
                                text = "¿Descubriste un\nlugar increíble?",
                                fontFamily = FrauncesFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White,
                                lineHeight = 21.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Propón un nuevo spot y gana +100 XP",
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
                                text = "+ Propón Spot",
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
            Icon(Icons.Default.Add, contentDescription = "Agregar Spot", modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun SpotCard(
    spot: Spot,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, EjeSpotOutline),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen real del spot
            androidx.compose.foundation.Image(
                painter = painterResource(id = spot.imageResId),
                contentDescription = spot.name,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = spot.name,
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1F1A15)
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
                        text = "${spot.rating} · ${spot.distance}",
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E241C)
                    )

                    if (spot.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = EjeSpotPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Verificado",
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EjeSpotPrimary
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
                        color = EjeSpotSecondaryContainer
                    ) {
                        Text(
                            text = spot.category,
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EjeSpotSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = EjeSpotPrimaryContainer
                    ) {
                        Text(
                            text = spot.priceBadge,
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EjeSpotPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = spot.hours,
                        fontFamily = ManropeFontFamily,
                        fontSize = 10.sp,
                        color = Color(0xFF6E6252)
                    )
                }
            }
        }
    }
}
