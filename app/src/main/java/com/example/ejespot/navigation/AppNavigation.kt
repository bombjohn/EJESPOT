package com.example.ejespot.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.features.auth.ui.ForgotPasswordScreen
import com.example.ejespot.features.auth.ui.LoginScreen
import com.example.ejespot.features.auth.ui.RegisterScreen
import com.example.ejespot.features.dashboard.MainScreen
import com.example.ejespot.features.onboarding.ui.OnboardingScreen
import com.example.ejespot.features.splash.ui.SplashScreen

/**
 * Contenedor principal de la navegación (guía 10).
 *
 * Aquí se asocian cada ruta con su pantalla mediante `composable`. Quien
 * decide a dónde lleva cada botón es este archivo: las pantallas solo avisan
 * qué se presionó mediante funciones `onNavigateTo...`.
 *
 * Las rutas están en [MainRoutes]; las del panel interno, en
 * `features/dashboard/navigation/DashboardRoutes.kt` (NavHost anidado).
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // El Surface toma el color de fondo del tema, así las pantallas se ven
    // bien en modo claro y oscuro.
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = MainRoutes.Splash
        ) {
            composable<MainRoutes.Splash> {
                SplashScreen(
                    onSplashFinished = {
                        // Si Data Store restauró una sesión, entramos directo al
                        // dashboard; si no, pasamos por el onboarding y el login.
                        val nextRoute = if (SessionManager.currentUser != null) {
                            MainRoutes.Main
                        } else {
                            MainRoutes.Onboarding
                        }
                        navController.navigate(nextRoute) {
                            popUpTo(MainRoutes.Splash) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.Onboarding> {
                OnboardingScreen(
                    onContinue = {
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(MainRoutes.Onboarding) { inclusive = true }
                        }
                    },
                    onSkip = {
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(MainRoutes.Onboarding) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.Login> {
                LoginScreen(
                    onLoginSuccess = { user ->
                        SessionManager.signIn(user)
                        navController.navigate(MainRoutes.Main) {
                            popUpTo(MainRoutes.Login) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(MainRoutes.Register)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(MainRoutes.ForgotPassword)
                    }
                )
            }

            composable<MainRoutes.Register> {
                RegisterScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = { user ->
                        // La cuenta creada inicia sesión y queda persistida en Data Store.
                        SessionManager.signIn(user)
                        navController.navigate(MainRoutes.Main) {
                            popUpTo(MainRoutes.Register) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.ForgotPassword> {
                ForgotPasswordScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            // Pantalla Principal (Dashboard con NavigationBar + Nested Navigation)
            composable<MainRoutes.Main> {
                MainScreen(
                    role = SessionManager.role,
                    onLogout = {
                        SessionManager.signOut()
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(MainRoutes.Main) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
