package com.example.ejespot.domain.model

import androidx.annotation.StringRes
import com.example.ejespot.R

/**
 * Modelos de dominio del módulo de Perfil (pantallas 6.1 a 6.10).
 * En esta fase la información vive en memoria y es administrada por ProfileViewModel.
 *
 * Los textos visibles al usuario provienen del contenido quemado de la demo,
 * por eso se guardan como recursos (`@StringRes`) y se pintan con
 * `stringResource(...)` (guía 14). Los identificadores de datos
 * (nivel de comunidad, categoría, nombre del lugar) no se traducen.
 */

enum class NotificationCategory {
    XP,
    REVIEW,
    COMMENT,
    LEVEL,
    NEARBY
}

/**
 * Agrupación por día de las notificaciones.
 *
 * El enum es el identificador de datos; la etiqueta visible sale de
 * [labelRes], igual que en [SpotCategories] y [ProfileLevels].
 */
enum class NotificationDayGroup {
    TODAY,
    YESTERDAY;

    /** Recurso con la etiqueta del grupo ya traducida. */
    @StringRes
    fun labelRes(): Int = when (this) {
        TODAY -> R.string.notifications_group_today
        YESTERDAY -> R.string.notifications_group_yesterday
    }
}

data class ProfileNotification(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int,
    @StringRes val timeLabelRes: Int,
    val dayGroup: NotificationDayGroup,
    val category: NotificationCategory,
    val isRead: Boolean = false
)

enum class LevelStatus {
    ACHIEVED,
    CURRENT,
    LOCKED
}

data class CommunityLevel(
    /** Identificador del nivel (se traduce con [ProfileLevels.labelRes]). */
    val name: String,
    @StringRes val rangeLabelRes: Int,
    val status: LevelStatus,
    val emoji: String
)

data class AchievementBadge(
    val id: String,
    val emoji: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val isUnlocked: Boolean,
    @StringRes val progressLabelRes: Int? = null
)

data class ProfileActivity(
    val id: String,
    val emoji: String,
    @StringRes val labelRes: Int,
    /** Valor dinámico (ej. "23"); si hay [valueLabelRes] se usa ese recurso. */
    val valueLabel: String = "",
    @StringRes val valueLabelRes: Int? = null
)

data class UserStats(
    val visited: Int,
    val reviews: Int,
    val proposals: Int
)

data class UserProfile(
    val name: String,
    val username: String,
    val location: String,
    /** Biografía editeda por el usuario (texto plano). */
    val bio: String,
    val initials: String,
    val level: Int,
    val levelName: String,
    val xp: Int,
    val nextLevelXp: Int,
    val nextLevelName: String,
    val memberSince: String,
    /** Biología de la demo como recurso traducible. */
    @StringRes val bioRes: Int? = null,
    /** Antigüedad de la cuenta de la demo como recurso traducible. */
    @StringRes val memberSinceRes: Int? = null
) {
    val xpProgress: Float
        get() = if (nextLevelXp <= 0) 0f else (xp.toFloat() / nextLevelXp).coerceIn(0f, 1f)
}

data class ProfileDraft(
    val name: String = "",
    val username: String = "",
    val location: String = "",
    val bio: String = ""
)

data class MyReview(
    val id: String,
    val spotId: String?,
    val spotName: String,
    /** Guarda la ubicación del lugar, no una categoría. */
    val category: String,
    val dateLabel: String,
    val rating: Double,
    val comment: String,
    val helpfulCount: Int,
    val xpEarned: Int,
    val isMarkedHelpful: Boolean = false,
    /** Fecha relativa como recurso traducible (contenido de la demo). */
    @StringRes val dateLabelRes: Int? = null,
    /** Comentario como recurso traducible (contenido de la demo). */
    @StringRes val commentRes: Int? = null
)

enum class ProposalStatus {
    APPROVED,
    PENDING
}

data class ProposedSpot(
    val id: String,
    val name: String,
    val location: String,
    @StringRes val submittedLabelRes: Int = 0,
    val customSubmittedLabel: String? = null,
    val status: ProposalStatus,
    val xpEarned: Int,
    @StringRes val noteRes: Int = 0,
    val customNote: String? = null,
    val spotId: String? = null
)

data class ProfileSettings(
    val nearbyNotifications: Boolean = true,
    val proposalNotifications: Boolean = true,
    val rewardNotifications: Boolean = false,
    val darkMode: Boolean = false
)

data class AppPermission(
    val id: String,
    val emoji: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val isGranted: Boolean
)

data class AppLanguage(
    val code: String,
    val flag: String,
    val name: String,
    val nativeName: String
)

data class ModerationReport(
    val id: String,
    val spotName: String,
    @StringRes val reasonRes: Int,
    val reporter: String,
    @StringRes val timeLabelRes: Int,
    val spotId: String? = null
)
