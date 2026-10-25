package com.example.ejespot.features.dashboard.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.ejespot.features.dashboard.navigation.DashboardDestination
import com.example.ejespot.ui.theme.*

@Composable
fun BottomNavigationBar(
    items: List<DashboardDestination>,
    currentDestination: NavDestination?,
    onItemSelected: (DashboardDestination) -> Unit
) {
    val c = ejeSpotColors()
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = c.surface
    ) {
        items.forEach { destination ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(destination.route::class)
            } == true

            NavigationBarItem(
                label = {
                    Text(
                        text = stringResource(destination.label),
                        fontFamily = ManropeFontFamily
                    )
                },
                selected = isSelected,
                onClick = { onItemSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = stringResource(destination.label)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EjeSpotPrimary,
                    selectedTextColor = EjeSpotPrimary,
                    indicatorColor = c.primaryContainer,
                    unselectedIconColor = c.textSoft,
                    unselectedTextColor = c.textSoft
                )
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<DashboardDestination>,
    currentRoute: String,
    onItemSelected: (DashboardDestination) -> Unit
) {
    val c = ejeSpotColors()
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = c.surface
    ) {
        items.forEach { destination ->
            val isSelected = currentRoute == destination.routeKey

            NavigationBarItem(
                label = {
                    Text(
                        text = stringResource(destination.label),
                        fontFamily = ManropeFontFamily
                    )
                },
                selected = isSelected,
                onClick = { onItemSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = stringResource(destination.label)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EjeSpotPrimary,
                    selectedTextColor = EjeSpotPrimary,
                    indicatorColor = c.primaryContainer,
                    unselectedIconColor = c.textSoft,
                    unselectedTextColor = c.textSoft
                )
            )
        }
    }
}

