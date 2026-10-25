package com.example.ejespot.core.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Preferencias de apariencia de la demo (Fase 2).
 *
 * Vive en memoria junto a la sesión, para que la pantalla de Configuración
 * y el tema raíz ([com.example.ejespot.ui.theme.EjeSpotTheme]) compartan
 * el mismo valor y el interruptor "Modo oscuro" tenga efecto real.
 */
object AppearanceSettings {
    var darkMode: Boolean by mutableStateOf(false)
}
