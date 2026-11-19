package com.example.ejespot.features.spots.create

data class CreateSpotUiState(
    val name: String = "",
    val category: String = "Naturaleza",
    val location: String = "Pereira, Risaralda",
    /** Coordenadas elegidas en el mapa; null hasta que el usuario las seleccione. */
    val latitude: Double? = null,
    val longitude: Double? = null,
    val description: String = "",
    val hours: String = "7:00 a.m. - 5:00 p.m.",
    val priceType: String = "Gratuito",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val submittedSpotName: String = "",
    val submittedCategory: String = "",
    val submittedLocation: String = ""
)

