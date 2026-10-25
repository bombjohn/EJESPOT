package com.example.ejespot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.ejespot.core.session.AppearanceSettings

private val DarkColorScheme = darkColorScheme(
    primary = DarkEjeSpotColors.primary,
    onPrimary = Color(0xFF0E2915),
    primaryContainer = EjeSpotPrimary,
    onPrimaryContainer = Color(0xFFDCF3E2),
    secondary = DarkEjeSpotColors.secondary,
    onSecondary = Color(0xFF2A1608),
    secondaryContainer = DarkEjeSpotColors.secondaryContainer,
    onSecondaryContainer = Color(0xFFFBE3CE),
    tertiary = Color(0xFFE0C77A),
    onTertiary = Color(0xFF2E2200),
    tertiaryContainer = DarkEjeSpotColors.tertiaryContainer,
    onTertiaryContainer = Color(0xFFF5E3B8),
    background = DarkEjeSpotColors.background,
    onBackground = DarkEjeSpotColors.text,
    surface = DarkEjeSpotColors.surface,
    onSurface = DarkEjeSpotColors.text,
    surfaceVariant = DarkEjeSpotColors.surfaceVariant,
    onSurfaceVariant = DarkEjeSpotColors.textMuted,
    outline = DarkEjeSpotColors.outline,
    outlineVariant = DarkEjeSpotColors.outlineSoft,
    error = DarkEjeSpotColors.dangerText
)

private val LightColorScheme = lightColorScheme(
    primary = EjeSpotPrimary,
    onPrimary = EjeSpotOnPrimary,
    primaryContainer = EjeSpotPrimaryContainer,
    onPrimaryContainer = EjeSpotOnPrimaryContainer,
    secondary = EjeSpotSecondary,
    onSecondary = Color.White,
    secondaryContainer = EjeSpotSecondaryContainer,
    onSecondaryContainer = EjeSpotOnSecondaryContainer,
    tertiary = EjeSpotTertiary,
    onTertiary = Color.White,
    tertiaryContainer = EjeSpotTertiaryContainer,
    onTertiaryContainer = EjeSpotOnTertiaryContainer,
    background = EjeSpotBackground,
    onBackground = EjeSpotText,
    surface = EjeSpotSurface,
    onSurface = EjeSpotText,
    surfaceVariant = EjeSpotSurfaceVariant,
    onSurfaceVariant = EjeSpotTextMuted,
    outline = EjeSpotOutline,
    outlineVariant = EjeSpotSurfaceVariant,
    error = EjeSpotError
)

/** Tokens adaptativos accesibles desde cualquier @Composable. */
val LocalEjeSpotColors = staticCompositionLocalOf { LightEjeSpotColors }

@Composable
fun EjeSpotTheme(
    darkTheme: Boolean = AppearanceSettings.darkMode,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val tokens = if (darkTheme) DarkEjeSpotColors else LightEjeSpotColors

    CompositionLocalProvider(LocalEjeSpotColors provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Lectura ergonómica de los tokens adaptativos dentro de un @Composable:
 *
 *     val c = ejeSpotColors()
 *     Box(Modifier.background(c.surface))
 */
@Composable
@ReadOnlyComposable
fun ejeSpotColors(): EjeSpotColors = LocalEjeSpotColors.current
