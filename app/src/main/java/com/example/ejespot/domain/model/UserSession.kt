package com.example.ejespot.domain.model

/**
 * Sesión persistida del usuario (Fase 2).
 *
 * Es un modelo de dominio: no sabe nada de Data Store. Solo guarda lo mínimo
 * necesario para reconstruir al usuario al volver a abrir la app.
 *
 * @param profileId Identificador del perfil, con el que se resuelven los
 *   lugares visitados y guardados del usuario.
 * @param isGuest `true` si el usuario entró como invitado (sin cuenta).
 */
data class UserSession(
    val userId: String,
    val profileId: String,
    val isGuest: Boolean
)
