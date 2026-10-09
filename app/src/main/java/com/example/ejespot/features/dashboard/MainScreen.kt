package com.example.ejespot.features.dashboard

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import com.example.ejespot.domain.model.UserRole
import com.example.ejespot.features.dashboard.component.BottomNavigationBar
import com.example.ejespot.features.dashboard.navigation.DashboardDestination
import com.example.ejespot.features.dashboard.navigation.DashboardNavigation

@Composable
fun MainScreen(
    role: UserRole = UserRole.USER,
    onLogout: () -> Unit
) {
    var currentTab by remember {
        mutableStateOf(if (role == UserRole.ADMIN) "admin_home" else "explore")
    }

    // Filtrar los items de la barra según el rol como indica la guía
    val items = remember(role) {
        DashboardDestination.entries.filter { role in it.roles }
    }

    // SnackbarHost compartido para todo el dashboard
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = items,
                currentRoute = currentTab,
                onItemSelected = { destination ->
                    currentTab = destination.routeKey
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->
        DashboardNavigation(
            padding = padding,
            currentTab = currentTab,
            role = role,
            snackbarHostState = snackbarHostState,
            onLogout = onLogout
        )
    }
}
