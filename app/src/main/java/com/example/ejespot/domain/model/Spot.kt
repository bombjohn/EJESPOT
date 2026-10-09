package com.example.ejespot.domain.model

data class SpotReview(
    val id: String,
    val authorName: String,
    val authorBadge: String, // e.g. "👑 Embajadora Local", "🥾 Aventurero"
    val avatarInitials: String, // e.g. "MJ", "DT"
    val rating: Int, // 1 to 5
    val comment: String,
    val date: String = "Hace 2 días"
)

data class SpotSpecialty(
    val name: String,
    val description: String,
    val price: String,
    val iconEmoji: String = "🍴"
)

data class SpotTechnicalInfo(
    val item1Label: String,
    val item1Value: String,
    val item2Label: String,
    val item2Value: String,
    val item3Label: String,
    val item3Value: String,
    val item4Label: String,
    val item4Value: String
)

data class SpotHighlight(
    val title: String,
    val subtitle: String,
    val iconEmoji: String = "✨"
)

data class Spot(
    val id: String,
    val name: String,
    val category: String, // Naturaleza, Café, Gastronomía, Historia, Cultura
    val location: String,
    val rating: Double,
    val reviewCount: Int = 184,
    val distance: String,
    val isVerified: Boolean,
    val priceBadge: String, // Gratuito, Moderado, Económico
    val hours: String,
    val priceRange: String = "Gratuito",
    val description: String,
    val addedBy: String,
    val imageResId: Int,
    val isPending: Boolean = false,
    val technicalTitle: String? = null,
    val technicalInfo: SpotTechnicalInfo? = null,
    val specialtiesTitle: String? = null,
    val specialties: List<SpotSpecialty> = emptyList(),
    val highlightsTitle: String? = null,
    val highlights: List<SpotHighlight> = emptyList(),
    val reviews: List<SpotReview> = emptyList()
)

