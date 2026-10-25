package com.example.ejespot.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Paleta Oficial EjeSpot (Design System Material You M3)
// --- Colores de marca (idénticos en claro y oscuro) ---
val EjeSpotPrimary = Color(0xFF3D6B4F)              // Verde café
val EjeSpotPrimaryContainer = Color(0xFFCFE8D6)     // Verde claro contenedor
val EjeSpotOnPrimary = Color(0xFFFFFFFF)
val EjeSpotOnPrimaryContainer = Color(0xFF0E2915)

val EjeSpotSecondary = Color(0xFFB5652F)            // Terracota
val EjeSpotSecondaryContainer = Color(0xFFF6DEC9)   // Terracota claro contenedor
val EjeSpotOnSecondaryContainer = Color(0xFF3D1F06)

val EjeSpotTertiary = Color(0xFF8A6A17)             // Dorado/Mostaza
val EjeSpotTertiaryContainer = Color(0xFFF5E3B8)
val EjeSpotOnTertiaryContainer = Color(0xFF2E2200)

val EjeSpotGold = Color(0xFFF0C868)                // Acento "Spot"
val EjeSpotSplashDark = Color(0xFF17301F)          // Fondo oscuro splash
val EjeSpotSplashMid = Color(0xFF234432)

// Error / Reporte
val EjeSpotError = Color(0xFF9E382B)

// --- Tokens "crudos" en modo claro (compatibilidad con código existente) ---
// Se mantienen como valores claros para los pocos lugares que aún los usan
// directamente; para las pantallas adaptativas usar [EjeSpotTheme.colors].
val EjeSpotBackground = Color(0xFFFBF6EE)           // Marfil cálido
val EjeSpotSurface = Color(0xFFFFFFFF)
val EjeSpotSurfaceVariant = Color(0xFFEFE6D6)
val EjeSpotOutline = Color(0xFFD8CCB4)

val EjeSpotText = Color(0xFF1A1A1A)       // Negro suave nítido
val EjeSpotTextMuted = Color(0xFF2E241C)  // Café oscuro sólido
val EjeSpotPlaceholder = Color(0xFF887D70)

/**
 * Conjunto de tokens de color adaptativos al modo claro/oscuro.
 *
 * Las pantallas deben leerlos a través de [EjeSpotTheme.colors] en lugar de
 * escribir colores fijos (por ejemplo `Color.White`), de forma que el
 * interruptor "Modo oscuro" tenga efecto real en toda la app.
 */
data class EjeSpotColors(
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val surfaceVariant: Color,
    val outline: Color,
    val outlineSoft: Color,
    val text: Color,
    val textMuted: Color,
    val textSoft: Color,
    val placeholder: Color,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val secondaryContainer: Color,
    val tertiaryContainer: Color,
    val chipIdle: Color,
    val mapCanvas: Color,
    val starIdle: Color,
    val avatarBg: Color,
    val successBg: Color,
    val successText: Color,
    val dangerBg: Color,
    val dangerText: Color,
    val scrim: Color,
    val isDark: Boolean
)

/** Tokens para el modo claro (aspecto original de la Fase 1). */
val LightEjeSpotColors = EjeSpotColors(
    background = Color(0xFFFBF6EE),
    surface = Color(0xFFFFFFFF),
    surfaceRaised = Color(0xFFFFFDF9),
    surfaceVariant = Color(0xFFEFE6D6),
    outline = Color(0xFFD8CCB4),
    outlineSoft = Color(0xFFE5DDD0),
    text = Color(0xFF1F1A15),
    textMuted = Color(0xFF3E352B),
    textSoft = Color(0xFF6E6252),
    placeholder = Color(0xFF887D70),
    primary = EjeSpotPrimary,
    primaryContainer = EjeSpotPrimaryContainer,
    onPrimaryContainer = EjeSpotOnPrimaryContainer,
    secondary = EjeSpotSecondary,
    secondaryContainer = EjeSpotSecondaryContainer,
    tertiaryContainer = EjeSpotTertiaryContainer,
    chipIdle = Color(0xFFFFFFFF),
    mapCanvas = Color(0xFF89BF82),
    starIdle = Color(0xFFE0D8CB),
    avatarBg = Color(0xFFE8DFD0),
    successBg = Color(0xFFD7E8DC),
    successText = Color(0xFF2C5640),
    dangerBg = Color(0xFFF7E4E1),
    dangerText = Color(0xFF9E382B),
    scrim = Color(0xCC1F1A15),
    isDark = false
)

/** Tokens para el modo oscuro (verdeprofundo + tarjetas elevadas). */
val DarkEjeSpotColors = EjeSpotColors(
    background = Color(0xFF12180F),
    surface = Color(0xFF1E271C),
    surfaceRaised = Color(0xFF26311F),
    surfaceVariant = Color(0xFF2C3828),
    outline = Color(0xFF3E4B38),
    outlineSoft = Color(0xFF354131),
    text = Color(0xFFF3EFE6),
    textMuted = Color(0xFFD8D2C4),
    textSoft = Color(0xFFB5AE9E),
    placeholder = Color(0xFF948C7C),
    primary = Color(0xFF8FCBA4),
    primaryContainer = Color(0xFF2A4A36),
    onPrimaryContainer = Color(0xFFDCF3E2),
    secondary = Color(0xFFE7A876),
    secondaryContainer = Color(0xFF4A2E17),
    tertiaryContainer = Color(0xFF4A3C14),
    chipIdle = Color(0xFF26311F),
    mapCanvas = Color(0xFF2A3A24),
    starIdle = Color(0xFF4A5544),
    avatarBg = Color(0xFF34402E),
    successBg = Color(0xFF25402F),
    successText = Color(0xFF8FCBA4),
    dangerBg = Color(0xFF43231F),
    dangerText = Color(0xFFEB9C90),
    scrim = Color(0xCC000000),
    isDark = true
)
