package com.example.ejespot.features.dashboard.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.domain.model.UserRole
import com.example.ejespot.features.spots.create.CreateSpotScreen
import com.example.ejespot.features.spots.detail.SpotDetailScreen
import com.example.ejespot.features.spots.list.SpotListScreen
import com.example.ejespot.features.spots.list.SpotListViewModel
import com.example.ejespot.ui.theme.ManropeFontFamily

@Composable
fun DashboardNavigation(
    padding: PaddingValues,
    currentTab: String,
    role: UserRole,
    snackbarHostState: SnackbarHostState,
    onLogout: () -> Unit,
    spotListViewModel: SpotListViewModel = viewModel()
) {
    var activeSubScreen by remember { mutableStateOf<String?>(null) }
    var selectedSpotId by remember { mutableStateOf<String?>(null) }

    when {
        activeSubScreen == "create_spot" -> {
            CreateSpotScreen(
                padding = padding,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { activeSubScreen = null }
            )
        }

        activeSubScreen == "spot_detail" && selectedSpotId != null -> {
            val spot = spotListViewModel.getSpotById(selectedSpotId!!)
            SpotDetailScreen(
                spot = spot,
                padding = padding,
                snackbarHostState = snackbarHostState,
                onNavigateBack = {
                    activeSubScreen = null
                    selectedSpotId = null
                },
                onAddReview = { newReview ->
                    spot?.let { spotListViewModel.addReview(it.id, newReview) }
                }
            )
        }

        else -> {
            when (currentTab) {
                "explore" -> {
                    SpotListScreen(
                        padding = padding,
                        onNavigateToSpotDetail = { id ->
                            selectedSpotId = id
                            activeSubScreen = "spot_detail"
                        },
                        onNavigateToCreateSpot = {
                            activeSubScreen = "create_spot"
                        },
                        viewModel = spotListViewModel
                    )
                }

                "search" -> {
                    SpotListScreen(
                        padding = padding,
                        onNavigateToSpotDetail = { id ->
                            selectedSpotId = id
                            activeSubScreen = "spot_detail"
                        },
                        onNavigateToCreateSpot = {
                            activeSubScreen = "create_spot"
                        },
                        viewModel = spotListViewModel
                    )
                }

                "admin_home" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Panel de Moderación (Admin)",
                            fontFamily = ManropeFontFamily
                        )
                    }
                }

                "profile" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Button(onClick = onLogout) {
                            Text(text = "Cerrar sesión")
                        }
                    }
                }
            }
        }
    }
}
