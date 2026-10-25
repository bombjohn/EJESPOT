package com.example.ejespot.data.repository.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ejespot.domain.model.UserSession
import com.example.ejespot.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Extensión que crea la instancia única de Data Store para la sesión.
 * Al declararla a nivel de archivo, Android garantiza una sola instancia
 * aunque se cree el repositorio varias veces.
 */
private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "session"
)

/**
 * Implementación con **Preferences Data Store** (guía 13).
 *
 * Reemplaza el guardado en memoria: la sesión sobrevive a cerrar la app.
 */
class LocalSessionRepository(
    private val context: Context
) : SessionRepository {

    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val PROFILE_ID = stringPreferencesKey("profile_id")
        val IS_GUEST = booleanPreferencesKey("is_guest")
    }

    /** Se reemite cada vez que cambian las preferencias guardadas. */
    override val session: Flow<UserSession?> = context.sessionDataStore.data.map { prefs ->
        val userId = prefs[Keys.USER_ID]
        val profileId = prefs[Keys.PROFILE_ID]

        // Si falta el identificador no hay sesión válida.
        if (userId.isNullOrBlank() || profileId.isNullOrBlank()) {
            null
        } else {
            UserSession(
                userId = userId,
                profileId = profileId,
                isGuest = prefs[Keys.IS_GUEST] ?: false
            )
        }
    }

    override suspend fun saveSession(session: UserSession) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.USER_ID] = session.userId
            prefs[Keys.PROFILE_ID] = session.profileId
            prefs[Keys.IS_GUEST] = session.isGuest
        }
    }

    override suspend fun clearSession() {
        context.sessionDataStore.edit { it.clear() }
    }
}
