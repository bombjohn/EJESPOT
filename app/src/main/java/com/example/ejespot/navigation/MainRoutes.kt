package com.example.ejespot.navigation

import kotlinx.serialization.Serializable

/**
 * Rutas de navegación de la app (guía 10).
 *
 * Es una `sealed class`, así que la lista de rutas posibles queda fija y el
 * compilador la conoce todas. La anotación `@Serializable` permite que
 * Navigation Compose convierta cada ruta (con sus parámetros) en texto.
 *
 * Las rutas del panel interno (con barra inferior) viven aparte, en
 * `features/dashboard/navigation/DashboardRoutes.kt`, porque ese `NavHost` es
 * anidado y maneja su propio back stack.
 */
@Serializable
sealed class MainRoutes {

    @Serializable
    data object Splash : MainRoutes()

    @Serializable
    data object Onboarding : MainRoutes()

    @Serializable
    data object Login : MainRoutes()

    @Serializable
    data object Register : MainRoutes()

    @Serializable
    data object ForgotPassword : MainRoutes()

    @Serializable
    data object Main : MainRoutes()
}
