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
import com.example.ejespot.features.dashboard.navigation.DashboardDestination
import com.example.ejespot.ui.theme.EjeSpotPrimary
import com.example.ejespot.ui.theme.EjeSpotPrimaryContainer
import com.example.ejespot.ui.theme.ManropeFontFamily

@Composable
fun BottomNavigationBar(
    items: List<DashboardDestination>,
    currentRoute: String,
    onItemSelected: (DashboardDestination) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White
    ) {
        items.forEach { destination ->
            val isSelected = currentRoute == destination.routeKey

            NavigationBarItem(
                label = {
                    Text(
                        text = destination.label,
                        fontFamily = ManropeFontFamily
                    )
                },
                selected = isSelected,
                onClick = { onItemSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EjeSpotPrimary,
                    selectedTextColor = EjeSpotPrimary,
                    indicatorColor = EjeSpotPrimaryContainer,
                    unselectedIconColor = Color(0xFF6E6252),
                    unselectedTextColor = Color(0xFF6E6252)
                )
            )
        }
    }
}
