package com.example.ejespot.domain.model

import androidx.annotation.StringRes

/**
 * Modelos de dominio de los puntos de interés.
 *
 * Los textos visibles al usuario pueden venir de dos fuentes: el contenido
 * quemado de la demo (traducible con recursos, guía 14) o el texto que
 * escribe el usuario en la app. Por eso los campos de texto libre tienen
 * además su equivalente `...Res`: si viene relleno se resuelve con
 * `stringResource(...)`; si viene null se usa el texto plano.
 */
data class SpotReview(
    val id: String,
    val authorName: String,
    val authorBadge: String = "", // e.g. "👑 Embajadora Local", "🥾 Aventurero"
    val avatarInitials: String, // e.g. "MJ", "DT"
    val rating: Int, // 1 to 5
    val comment: String = "",
    val date: String = "",
    val authorProfileId: String? = null,
    /** Insignia del autor como recurso traducible (contenido de la demo). */
    @StringRes val authorBadgeRes: Int? = null,
    /** Comentario como recurso traducible (contenido de la demo). */
    @StringRes val commentRes: Int? = null,
    /** Fecha relativa como recurso traducible (contenido de la demo). */
    @StringRes val dateRes: Int? = null
)

data class SpotSpecialty(
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val priceRes: Int,
    val iconEmoji: String = "🍴"
)

data class SpotTechnicalInfo(
    @StringRes val item1Label: Int,
    @StringRes val item1Value: Int,
    @StringRes val item2Label: Int,
    @StringRes val item2Value: Int,
    @StringRes val item3Label: Int,
    @StringRes val item3Value: Int,
    @StringRes val item4Label: Int,
    @StringRes val item4Value: Int
)

data class SpotHighlight(
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val iconEmoji: String = "✨"
)

data class Spot(
    val id: String,
    val name: String,
    val category: String, // Naturaleza, Café, Gastronomía, Historia, Cultura
    /** Municipio del Eje Cafetero donde está el lugar ("Pereira, Risaralda"). */
    val municipality: String,
    val rating: Double,
    val reviewCount: Int = 184,
    /** Distancia medida, sin preposición (ej. "2.1 km"). */
    val distance: String,
    val isVerified: Boolean,
    val priceBadge: String = "",
    val hours: String = "",
    val priceRange: String = "",
    val description: String = "",
    val addedBy: String,
    /** Foto real; null cuando el lugar solo tiene cabecera ilustrada. */
    val imageResId: Int? = null,
    /** Emoji con el que se dibuja la cabecera cuando no hay foto. */
    val emoji: String = "📍",
    /** Color de fondo de la cabecera ilustrada (formato 0xFFRRGGBB). */
    val accentColor: Long = 0xFF2C5640,
    /** Departamento del Eje Cafetero al que pertenece (Caldas, Quindío, Risaralda). */
    val department: String = "Quindío",
    /** Posición relativa dentro del mapa ilustrativo (0..1). */
    val mapRelX: Float = 0.5f,
    val mapRelY: Float = 0.5f,
    /** Coordenadas reales para el mapa (osmdroid). */
    val location: Location = Location(4.6400, -75.5700),
    val isPending: Boolean = false,
    @StringRes val technicalTitleRes: Int? = null,
    val technicalInfo: SpotTechnicalInfo? = null,
    @StringRes val specialtiesTitleRes: Int? = null,
    val specialties: List<SpotSpecialty> = emptyList(),
    @StringRes val highlightsTitleRes: Int? = null,
    val highlights: List<SpotHighlight> = emptyList(),
    val reviews: List<SpotReview> = emptyList(),
    /** Badge de precio como recurso traducible (contenido de la demo). */
    @StringRes val priceBadgeRes: Int? = null,
    /** Horario como recurso traducible (contenido de la demo). */
    @StringRes val hoursRes: Int? = null,
    /** Rango de precio como recurso traducible (contenido de la demo). */
    @StringRes val priceRangeRes: Int? = null,
    /** Descripción como recurso traducible (contenido de la demo). */
    @StringRes val descriptionRes: Int? = null
)
