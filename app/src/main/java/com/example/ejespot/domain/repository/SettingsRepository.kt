package com.example.ejespot.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Contrato para las preferencias de la app.
 *
 * En Fase 2 solo se guarda el idioma, que es lo que necesita la app para
 * mostrarse en español o inglés (guía 14) y sobrevivir al cierre (guía 13).
 */
interface SettingsRepository {

    /**
     * Emite la etiqueta del idioma elegido (`"es"`, `"en"`).
     * Una cadena vacía significa "seguir el idioma del dispositivo".
     */
    val languageTag: Flow<String>

    /** Guarda el idioma elegido. */
    suspend fun saveLanguageTag(tag: String)

    /** Borra el idioma guardado (vuelve al del dispositivo). */
    suspend fun clearLanguageTag()
}
