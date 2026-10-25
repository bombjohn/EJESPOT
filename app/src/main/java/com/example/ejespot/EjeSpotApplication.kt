package com.example.ejespot

import android.app.Application
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.core.settings.AppLanguageController
import com.example.ejespot.core.util.ResourceProviderHolder

/**
 * Punto de entrada de la aplicación.
 *
 * Aquí se preparan los servicios globales que el resto de la app consume
 * como singletons (sin Hilt):
 * - [ResourceProviderHolder] para que los ViewModel puedan leer `strings.xml`.
 * - [SessionManager] para restaurar la sesión persistida en Data Store.
 * - [AppLanguageController] para aplicar el idioma elegido por el usuario.
 *
 * `Application.onCreate()` se ejecuta antes que cualquier Activity, por lo que
 * estos recursos quedan disponibles desde el primer ViewModel que se cree.
 */
class EjeSpotApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Textos de strings.xml accesibles desde cualquier ViewModel.
        ResourceProviderHolder.init(applicationContext)

        // Idioma elegido por el usuario (guía 14 + guía 13).
        AppLanguageController.init(applicationContext)

        // Sesión persistida (Data Store): se restaura de forma asíncrona.
        SessionManager.init(applicationContext)
        SessionManager.restore()
    }
}
