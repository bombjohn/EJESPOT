package com.example.ejespot.features.spots.list

import com.example.ejespot.domain.model.Spot

data class SpotListUiState(
    val spots: List<Spot> = emptyList(),
    val pendingSpots: List<Spot> = emptyList(),
    val selectedCategory: String = "Todos",
    val searchQuery: String = "",
    val isLoading: Boolean = false
)
