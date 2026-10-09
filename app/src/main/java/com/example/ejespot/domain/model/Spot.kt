package com.example.ejespot.domain.model

data class Spot(
    val id: String,
    val name: String,
    val category: String, // Naturaleza, Café, Gastronomía, Historia, Cultura
    val location: String,
    val rating: Double,
    val distance: String,
    val isVerified: Boolean,
    val priceBadge: String, // Gratuito, Moderado, Económico
    val hours: String,
    val description: String,
    val addedBy: String,
    val isPending: Boolean = false
)
