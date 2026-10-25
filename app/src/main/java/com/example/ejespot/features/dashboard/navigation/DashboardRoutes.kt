package com.example.ejespot.features.dashboard.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class DashboardRoutes {
    @Serializable
    data object HomeUser : DashboardRoutes()

    @Serializable
    data object Map : DashboardRoutes()

    @Serializable
    data object Visited : DashboardRoutes()

    @Serializable
    data object Profile : DashboardRoutes()

    @Serializable
    data class SpotDetail(val spotId: String) : DashboardRoutes()

    @Serializable
    data class SpotReviews(val spotId: String) : DashboardRoutes()

    @Serializable
    data class ReportSpot(val spotId: String) : DashboardRoutes()

    @Serializable
    data object CreateSpot : DashboardRoutes()

    @Serializable
    data object HomeAdmin : DashboardRoutes()

    // Perfil (pantallas 6.1 a 6.10)
    @Serializable
    data object EditProfile : DashboardRoutes()

    @Serializable
    data object Notifications : DashboardRoutes()

    @Serializable
    data object Gamification : DashboardRoutes()

    @Serializable
    data object Settings : DashboardRoutes()

    @Serializable
    data object Terms : DashboardRoutes()

    @Serializable
    data object Privacy : DashboardRoutes()

    @Serializable
    data object Permissions : DashboardRoutes()

    @Serializable
    data object Language : DashboardRoutes()

    @Serializable
    data object MyReviews : DashboardRoutes()

    @Serializable
    data object MyProposals : DashboardRoutes()
}


