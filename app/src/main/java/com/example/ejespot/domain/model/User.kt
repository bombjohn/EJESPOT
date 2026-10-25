package com.example.ejespot.domain.model

import androidx.annotation.StringRes
import com.example.ejespot.R

/**
 * Cuenta y sesión de la demo (Fase 2).
 * Los usuarios están quemados en memoria: no hay backend ni persistencia.
 */
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val role: UserRole = UserRole.USER,
    val profileId: String = "",
    val isGuest: Boolean = false
) {
    /**
     * Etiqueta legible del rol, usada en la UI.
     *
     * Se devuelve el **identificador del recurso** (no el texto) para que la
     * traducción la resuelva Android según el idioma del dispositivo (guía 14).
     */
    @get:StringRes
    val roleLabelRes: Int
        get() = when {
            isGuest -> R.string.profile_role_guest
            role == UserRole.ADMIN -> R.string.profile_role_moderator
            else -> R.string.profile_role_tourist
        }
}
