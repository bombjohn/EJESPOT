package com.example.ejespot.features.dashboard.navigation

sealed class DashboardRoutes {
    data object HomeUser : DashboardRoutes()
    data object Search : DashboardRoutes()
    data class SpotDetail(val spotId: String) : DashboardRoutes()
    data object CreateSpot : DashboardRoutes()
    data object HomeAdmin : DashboardRoutes()
    data object Profile : DashboardRoutes()
}
