package com.example.ejespot.domain.repository

import com.example.ejespot.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

/**
 * Contrato para conservar la sesión entre ejecuciones de la app.
 *
 * El dominio no sabe **dónde** se guarda (Data Store, base de datos, etc.),
 * por eso la implementación vive en la capa de datos.
 */
interface SessionRepository {

    /** Emite la sesión guardada, o `null` si no hay ninguna. */
    val session: Flow<UserSession?>

    /** Guarda la sesión activa. */
    suspend fun saveSession(session: UserSession)

    /** Borra la sesión guardada (cerrar sesión). */
    suspend fun clearSession()
}
