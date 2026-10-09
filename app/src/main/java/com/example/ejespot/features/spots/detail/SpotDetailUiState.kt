package com.example.ejespot.features.spots.detail

import com.example.ejespot.domain.model.Spot

data class SpotDetailUiState(
    val spot: Spot? = null,
    val isSaved: Boolean = false,
    val isVisited: Boolean = false,
    val showReviewDialog: Boolean = false,
    val reviewRating: Int = 5,
    val reviewAuthor: String = "",
    val reviewComment: String = "",
    val feedbackMessage: String? = null
)
