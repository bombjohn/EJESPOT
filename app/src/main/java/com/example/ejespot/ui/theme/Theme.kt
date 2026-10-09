package com.example.ejespot.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EjeSpotPrimaryContainer,
    onPrimary = EjeSpotOnPrimaryContainer,
    primaryContainer = EjeSpotPrimary,
    secondary = EjeSpotSecondaryContainer,
    background = EjeSpotSplashDark,
    surface = EjeSpotSplashMid,
    onBackground = Color.White,
    onSurface = Color.White
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
    outline = EjeSpotOutline
)

@Composable
fun EjeSpotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}