package com.example.ejespot.features.dashboard.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ejespot.R
import com.example.ejespot.domain.model.UserRole

enum class DashboardDestination(
    val route: DashboardRoutes,
    val routeKey: String,
    @StringRes val label: Int,
    val icon: ImageVector,
    val roles: Set<UserRole>
) {
    EXPLORE(DashboardRoutes.HomeUser, "explore", R.string.nav_home, Icons.Default.Explore, setOf(UserRole.USER)),
    MAP(DashboardRoutes.Map, "map", R.string.nav_map, Icons.Default.Map, setOf(UserRole.USER)),
    VISITED(DashboardRoutes.Visited, "visited", R.string.nav_visited, Icons.Default.Favorite, setOf(UserRole.USER)),
    PROFILE(DashboardRoutes.Profile, "profile", R.string.nav_profile, Icons.Default.Person, UserRole.entries.toSet()),
    ADMIN_HOME(DashboardRoutes.HomeAdmin, "admin_home", R.string.moderation_title, Icons.AutoMirrored.Filled.List, setOf(UserRole.ADMIN))
}


