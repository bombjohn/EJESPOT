package com.example.ejespot.core.session

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf

/**
 * Preferencias de apariencia compartidas entre Configuración y el tema raíz.
 * Se guardan en SharedPreferences para que el modo oscuro sobreviva al reinicio.
 */
object AppearanceSettings {
    private const val PREFS_NAME = "ejespot_appearance"
    private const val KEY_DARK_MODE = "dark_mode"

    private var prefs: SharedPreferences? = null
    private val _darkMode = mutableStateOf(false)

    var darkMode: Boolean
        get() = _darkMode.value
        set(value) {
            _darkMode.value = value
            prefs?.edit()?.putBoolean(KEY_DARK_MODE, value)?.apply()
        }

    /** Carga la preferencia guardada. Se llama desde la Application. */
    fun init(context: Context) {
        if (prefs != null) return
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _darkMode.value = prefs!!.getBoolean(KEY_DARK_MODE, false)
    }
}
