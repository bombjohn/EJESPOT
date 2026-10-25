package com.example.ejespot

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.ejespot.core.settings.AppLanguageController
import com.example.ejespot.navigation.AppNavigation
import com.example.ejespot.ui.theme.EjeSpotTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    /**
     * Aplica el idioma elegido por el usuario.
     *
     * En Android 13+ esto lo hace `LocaleManager` (y él mismo recrea la
     * Activity). En versiones anteriores hay que ajustar el contexto base,
     * que es exactamente lo que se hace aquí.
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLanguageController.localizedContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La sesión (Data Store) y el acceso a los strings se preparan en
        // EjeSpotApplication.onCreate(), antes que cualquier Activity.
        enableEdgeToEdge()
        setContent {
            EjeSpotTheme {
                AppNavigation()
            }
        }

        // El idioma guardado se carga de forma asíncrona: si todavía no se
        // aplicó cuando la Activity se creó, se recrea para mostrar el idioma
        // correcto (solo necesario antes de Android 13).
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            lifecycleScope.launch {
                kotlinx.coroutines.delay(150)
                if (AppLanguageController.appliedTag != AppLanguageController.languageTag) {
                    recreate()
                }
            }
        }
    }
}
