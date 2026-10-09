package com.example.ejespot.features.spots.create

data class CreateSpotUiState(
    val name: String = "",
    val category: String = "Naturaleza",
    val department: String = "Quindío (Salento)",
    val description: String = "",
    val hours: String = "8:00 AM - 5:00 PM",
    val priceType: String = "Gratuito",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
