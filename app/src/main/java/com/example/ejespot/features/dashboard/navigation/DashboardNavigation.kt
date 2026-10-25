package com.example.ejespot.features.dashboard.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.ejespot.core.data.SpotStore
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.domain.model.UserRole
import com.example.ejespot.features.dashboard.profile.EditProfileScreen
import com.example.ejespot.features.dashboard.profile.GamificationScreen
import com.example.ejespot.features.dashboard.profile.LanguageScreen
import com.example.ejespot.features.dashboard.profile.LegalDocument
import com.example.ejespot.features.dashboard.profile.LegalScreen
import com.example.ejespot.features.dashboard.profile.MyProposalsScreen
import com.example.ejespot.features.dashboard.profile.MyReviewsScreen
import com.example.ejespot.features.dashboard.profile.NotificationsScreen
import com.example.ejespot.features.dashboard.profile.PermissionsScreen
import com.example.ejespot.features.dashboard.profile.ProfileScreen
import com.example.ejespot.features.dashboard.profile.ProfileViewModel
import com.example.ejespot.features.dashboard.profile.SettingsScreen
import com.example.ejespot.features.spots.create.CreateSpotScreen
import com.example.ejespot.features.spots.detail.ReportSpotScreen
import com.example.ejespot.features.spots.detail.SpotDetailScreen
import com.example.ejespot.features.spots.detail.SpotReviewsScreen
import com.example.ejespot.features.spots.list.SpotListScreen
import com.example.ejespot.features.spots.list.SpotListViewModel
import com.example.ejespot.features.spots.map.MapScreen
import com.example.ejespot.features.spots.visited.VisitedScreen
import kotlinx.coroutines.launch

@Composable
fun DashboardNavigation(
    navController: NavHostController,
    padding: PaddingValues,
    role: UserRole,
    snackbarHostState: SnackbarHostState,
    onLogout: () -> Unit,
    spotListViewModel: SpotListViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val startDestination: DashboardRoutes = if (role == UserRole.ADMIN) {
        DashboardRoutes.HomeAdmin
    } else {
        DashboardRoutes.HomeUser
    }

    // Al entrar al dashboard se sincronizan los datos del usuario que inició sesión.
    LaunchedEffect(SessionManager.profileId) {
        SpotStore.refreshForCurrentUser()
        profileViewModel.reloadForCurrentUser()
    }

    // Navegación entre pestañas del bottom bar conservando el estado de cada una
    val navigateToTab: (DashboardRoutes) -> Unit = { destination ->
        navController.navigate(destination) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Tab 1: Explorar (Feed Principal)
        composable<DashboardRoutes.HomeUser> {
            SpotListScreen(
                padding = padding,
                snackbarHostState = snackbarHostState,
                onNavigateToSpotDetail = { id ->
                    navController.navigate(DashboardRoutes.SpotDetail(spotId = id))
                },
                onNavigateToCreateSpot = {
                    navController.navigate(DashboardRoutes.CreateSpot)
                },
                onNavigateToNotifications = {
                    navController.navigate(DashboardRoutes.Notifications)
                },
                viewModel = spotListViewModel
            )
        }

        // Tab 2: Mapa interactivo (Pantalla 3.4)
        composable<DashboardRoutes.Map> {
            MapScreen(
                padding = padding,
                snackbarHostState = snackbarHostState,
                onNavigateToSpotDetail = { id ->
                    navController.navigate(DashboardRoutes.SpotDetail(spotId = id))
                },
                viewModel = spotListViewModel
            )
        }

        // Tab 3: Lugares Visitados y Guardados
        composable<DashboardRoutes.Visited> {
            VisitedScreen(
                padding = padding,
                snackbarHostState = snackbarHostState,
                onNavigateToSpotDetail = { id ->
                    navController.navigate(DashboardRoutes.SpotDetail(spotId = id))
                },
                viewModel = spotListViewModel
            )
        }

        // Tab 4: Perfil de Usuario (Pantalla 6.1)
        composable<DashboardRoutes.Profile> {
            ProfileScreen(
                padding = padding,
                viewModel = profileViewModel,
                snackbarHostState = snackbarHostState,
                onEditProfile = {
                    profileViewModel.startEditing()
                    navController.navigate(DashboardRoutes.EditProfile)
                },
                onNotifications = {
                    navController.navigate(DashboardRoutes.Notifications)
                },
                onSettings = {
                    navController.navigate(DashboardRoutes.Settings)
                },
                onGamification = {
                    navController.navigate(DashboardRoutes.Gamification)
                },
                onMyReviews = {
                    navController.navigate(DashboardRoutes.MyReviews)
                },
                onMyProposals = {
                    navController.navigate(DashboardRoutes.MyProposals)
                },
                onNavigateToVisited = { navigateToTab(DashboardRoutes.Visited) },
                onLogout = onLogout
            )
        }

        // Pantalla 5.1: Crear Spot
        composable<DashboardRoutes.CreateSpot> {
            CreateSpotScreen(
                padding = padding,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() },
                onSpotCreated = { newSpot ->
                    spotListViewModel.addPendingSpot(newSpot)
                    profileViewModel.addProposalFromCreatedSpot(
                        name = newSpot.name,
                        location = newSpot.municipality,
                        note = newSpot.description,
                        spotId = newSpot.id
                    )
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Spot '${newSpot.name}' registrado como pendiente de revisión")
                    }
                }
            )
        }

        // Pantalla Detalle de Spot
        composable<DashboardRoutes.SpotDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<DashboardRoutes.SpotDetail>()
            val spot = spotListViewModel.getSpotById(route.spotId)
            SpotDetailScreen(
                spot = spot,
                padding = padding,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() },
                onAddReview = { newReview ->
                    spot?.let { spotListViewModel.addReview(it.id, newReview) }
                },
                onNavigateToReviews = { id ->
                    navController.navigate(DashboardRoutes.SpotReviews(spotId = id))
                },
                onNavigateToReport = { id ->
                    navController.navigate(DashboardRoutes.ReportSpot(spotId = id))
                }
            )
        }

        // Pantalla 4.5: Ver Todas las Reseñas
        composable<DashboardRoutes.SpotReviews> { backStackEntry ->
            val route = backStackEntry.toRoute<DashboardRoutes.SpotReviews>()
            val spot = spotListViewModel.getSpotById(route.spotId)
            SpotReviewsScreen(
                spot = spot,
                padding = padding,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() },
                onAddReview = { newReview ->
                    spot?.let { spotListViewModel.addReview(it.id, newReview) }
                }
            )
        }

        // Pantalla 4.6: Reportar o Denunciar Contenido
        composable<DashboardRoutes.ReportSpot> { backStackEntry ->
            val route = backStackEntry.toRoute<DashboardRoutes.ReportSpot>()
            val spot = spotListViewModel.getSpotById(route.spotId)
            ReportSpotScreen(
                spot = spot,
                padding = padding,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ------------------------------------------------------------------
        // Perfil: Pantallas 6.2 a 6.10
        // ------------------------------------------------------------------

        // Pantalla 6.2: Editar Perfil
        composable<DashboardRoutes.EditProfile> {
            EditProfileScreen(
                padding = padding,
                viewModel = profileViewModel,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla 6.3: Notificaciones
        composable<DashboardRoutes.Notifications> {
            NotificationsScreen(
                padding = padding,
                viewModel = profileViewModel,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla 6.4: Gamificación / Reconocimientos
        composable<DashboardRoutes.Gamification> {
            GamificationScreen(
                padding = padding,
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() },
                onExplore = { navigateToTab(DashboardRoutes.HomeUser) }
            )
        }

        // Pantalla 6.5: Configuración
        composable<DashboardRoutes.Settings> {
            SettingsScreen(
                padding = padding,
                viewModel = profileViewModel,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() },
                onPrivacyPolicy = { navController.navigate(DashboardRoutes.Privacy) },
                onTerms = { navController.navigate(DashboardRoutes.Terms) },
                onPermissions = { navController.navigate(DashboardRoutes.Permissions) },
                onLanguage = { navController.navigate(DashboardRoutes.Language) },
                onDeleteAccount = onLogout
            )
        }

        // Pantalla 6.6: Términos y Condiciones
        composable<DashboardRoutes.Terms> {
            LegalScreen(
                document = LegalDocument.TERMS,
                padding = padding,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Política de Privacidad (accesible desde Configuración)
        composable<DashboardRoutes.Privacy> {
            LegalScreen(
                document = LegalDocument.PRIVACY,
                padding = padding,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla 6.7: Permisos de la App
        composable<DashboardRoutes.Permissions> {
            PermissionsScreen(
                padding = padding,
                viewModel = profileViewModel,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla 6.8: Idioma
        composable<DashboardRoutes.Language> {
            LanguageScreen(
                padding = padding,
                viewModel = profileViewModel,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla 6.9: Mis Reseñas Completas
        composable<DashboardRoutes.MyReviews> {
            MyReviewsScreen(
                padding = padding,
                viewModel = profileViewModel,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() },
                onOpenSpot = { spotId ->
                    navController.navigate(DashboardRoutes.SpotDetail(spotId = spotId))
                }
            )
        }

        // Pantalla 6.10: Mis Puntos Propuestos
        composable<DashboardRoutes.MyProposals> {
            MyProposalsScreen(
                padding = padding,
                viewModel = profileViewModel,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMap = { navigateToTab(DashboardRoutes.Map) },
                onOpenSpot = { spotId ->
                    navController.navigate(DashboardRoutes.SpotDetail(spotId = spotId))
                },
                onProposeSpot = { navController.navigate(DashboardRoutes.CreateSpot) }
            )
        }

        // Panel de Moderación (Admin)
        composable<DashboardRoutes.HomeAdmin> {
            AdminModerationHome(
                padding = padding,
                viewModel = profileViewModel,
                snackbarHostState = snackbarHostState,
                onNavigateToSpotDetail = { spotId ->
                    navController.navigate(DashboardRoutes.SpotDetail(spotId = spotId))
                }
            )
        }
    }
}
