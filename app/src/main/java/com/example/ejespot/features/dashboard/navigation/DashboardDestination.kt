package com.example.ejespot.features.dashboard.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ejespot.domain.model.UserRole

enum class DashboardDestination(
    val routeKey: String,
    val label: String,
    val icon: ImageVector,
    val roles: Set<UserRole>
) {
    EXPLORE("explore", "Explorar", Icons.Default.Explore, setOf(UserRole.USER)),
    SEARCH("search", "Buscar", Icons.Default.Search, setOf(UserRole.USER)),
    ADMIN_HOME("admin_home", "Moderación", Icons.AutoMirrored.Filled.List, setOf(UserRole.ADMIN)),
    PROFILE("profile", "Perfil", Icons.Default.AccountCircle, UserRole.entries.toSet())
}
