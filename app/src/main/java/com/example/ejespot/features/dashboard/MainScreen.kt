package com.example.ejespot.features.dashboard

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ejespot.domain.model.UserRole
import com.example.ejespot.features.dashboard.component.BottomNavigationBar
import com.example.ejespot.features.dashboard.navigation.DashboardDestination
import com.example.ejespot.features.dashboard.navigation.DashboardNavigation

@Composable
fun MainScreen(
    role: UserRole = UserRole.USER,
    onLogout: () -> Unit,
    dashboardNavController: NavHostController = rememberNavController()
) {
    // Filtrar los items de la barra según el rol como indica la guía
    val items = remember(role) {
        DashboardDestination.entries.filter { role in it.roles }
    }

    val navBackStackEntry by dashboardNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Mostrar barra inferior solo en destinos principales del Dashboard
    val showBottomBar = currentDestination == null || items.any { destination ->
        currentDestination.hierarchy.any { it.hasRoute(destination.route::class) }
    }

    // SnackbarHost compartido para todo el dashboard
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    items = items,
                    currentDestination = currentDestination,
                    onItemSelected = { destination ->
                        dashboardNavController.navigate(destination.route) {
                            popUpTo(dashboardNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->
        DashboardNavigation(
            navController = dashboardNavController,
            padding = padding,
            role = role,
            snackbarHostState = snackbarHostState,
            onLogout = onLogout
        )
    }
}

