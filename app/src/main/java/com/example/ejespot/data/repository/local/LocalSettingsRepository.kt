package com.example.ejespot.data.repository.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ejespot.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Extensión que crea la instancia única de Data Store para las preferencias.
 * Al declararla a nivel de archivo, Android garantiza una sola instancia.
 */
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

/**
 * Implementación de las preferencias con **Preferences Data Store** (guía 13).
 *
 * Guarda el idioma elegido en la pantalla de Configuración para que la app
 * arranque en ese idioma la próxima vez (guía 14).
 */
class LocalSettingsRepository(
    private val context: Context
) : SettingsRepository {

    private object Keys {
        val LANGUAGE_TAG = stringPreferencesKey("language_tag")
    }

    override val languageTag: Flow<String> = context.settingsDataStore.data.map { prefs ->
        prefs[Keys.LANGUAGE_TAG] ?: ""
    }

    override suspend fun saveLanguageTag(tag: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.LANGUAGE_TAG] = tag
        }
    }

    override suspend fun clearLanguageTag() {
        context.settingsDataStore.edit { prefs ->
            prefs.remove(Keys.LANGUAGE_TAG)
        }
    }
}
