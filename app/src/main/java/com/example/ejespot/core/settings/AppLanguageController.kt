package com.example.ejespot.core.settings

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ejespot.data.repository.local.LocalSettingsRepository
import com.example.ejespot.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Idioma activo de la app (guía 14) y su persistencia (guía 13).
 *
 * Android resuelve los textos según el idioma del sistema usando las carpetas
 * de recursos (`values/` en español, `values-en/` en inglés). Este controlador:
 *
 * 1. Guarda el idioma elegido en Data Store, para que la app lo recupere al
 *    arrancar.
 * 2. Lo aplica a la app en caliente:
 *    - **Android 13+** usa `LocaleManager`, que además recrea la Activity.
 *    - **Android 12 y anteriores** ajusta el contexto de la Activity en
 *      `attachBaseContext` (ver `MainActivity`).
 */
object AppLanguageController {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /** Repositorio de preferencias. Se inicializa en [init]. */
    private lateinit var repository: SettingsRepository

    /** Etiqueta del idioma activo (`"es"`, `"en"`). Vacío = idioma del dispositivo. */
    private val _languageTag = MutableStateFlow("")

    /** Etiqueta del idioma activo, como flujo (para los ViewModel). */
    val languageTagFlow: StateFlow<String> = _languageTag.asStateFlow()

    /** Etiqueta del idioma activo (`"es"`, `"en"`). Vacío = idioma del dispositivo. */
    var languageTag: String
        get() = _languageTag.value
        private set(value) {
            _languageTag.value = value
        }

    /** Etiqueta que se aplicó realmente al contexto de la app. */
    var appliedTag: String = ""
        private set

    /** Prepara el controlador y carga el idioma guardado. Idempotente. */
    fun init(context: Context) {
        if (::repository.isInitialized) return
        repository = LocalSettingsRepository(context.applicationContext)
        scope.launch {
            val saved = runCatching { repository.languageTag.first() }.getOrDefault("")
            languageTag = saved
        }
    }

    /**
     * Cambia el idioma de la app y lo persiste.
     * @param tag etiqueta de idioma (`"es"`, `"en"`); vacío = idioma del sistema.
     */
    fun setLanguage(context: Context, tag: String) {
        languageTag = tag
        applyToProcess(context)
        scope.launch {
            if (tag.isBlank()) {
                runCatching { repository.clearLanguageTag() }
            } else {
                runCatching { repository.saveLanguageTag(tag) }
            }
        }
    }

    /**
     * Aplica el idioma a todo el proceso.
     * En Android 13+ `LocaleManager` se encarga (y recrea la Activity).
     */
    private fun applyToProcess(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(android.app.LocaleManager::class.java)
            localeManager.applicationLocales = LocaleList.forLanguageTags(languageTag)
        }
        // En versiones anteriores el idioma se aplica sobre el contexto de la
        // Activity (attachBaseContext) y se marca como aplicado.
        appliedTag = languageTag
    }

    /**
     * Devuelve un contexto con el idioma seleccionado aplicado.
     * Se usa en `MainActivity.attachBaseContext` para Android 12 y anteriores.
     */
    fun localizedContext(base: Context): Context {
        val tag = languageTag
        if (tag.isBlank()) return base

        val locale = Locale.forLanguageTag(tag)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        return base.createConfigurationContext(config)
    }
}
