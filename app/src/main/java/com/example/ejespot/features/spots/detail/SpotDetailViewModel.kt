package com.example.ejespot.features.spots.detail

import androidx.lifecycle.ViewModel
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SpotDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SpotDetailUiState())
    val uiState: StateFlow<SpotDetailUiState> = _uiState.asStateFlow()

    fun setSpot(spot: Spot?) {
        _uiState.update { it.copy(spot = spot) }
    }

    fun toggleSaved() {
        val willSave = !_uiState.value.isSaved
        _uiState.update {
            it.copy(
                isSaved = willSave,
                feedbackMessage = if (willSave) "¡Guardado en tus favoritos!" else "Eliminado de favoritos"
            )
        }
    }

    fun toggleVisited() {
        val willVisit = !_uiState.value.isVisited
        _uiState.update {
            it.copy(
                isVisited = willVisit,
                feedbackMessage = if (willVisit) "✓ ¡Marcado como visitado! Ganaste +10 XP" else "Lugar desmarcado de visitas"
            )
        }
    }

    fun setReviewDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showReviewDialog = visible) }
    }

    fun onReviewRatingChanged(rating: Int) {
        _uiState.update { it.copy(reviewRating = rating) }
    }

    fun onReviewAuthorChanged(author: String) {
        _uiState.update { it.copy(reviewAuthor = author) }
    }

    fun onReviewCommentChanged(comment: String) {
        _uiState.update { it.copy(reviewComment = comment) }
    }

    fun submitReview(onReviewAdded: (SpotReview) -> Unit) {
        val state = _uiState.value
        val author = if (state.reviewAuthor.isNotBlank()) state.reviewAuthor.trim() else "Explorador EjeSpot"
        val initials = author.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()
            .ifEmpty { "EX" }

        val newReview = SpotReview(
            id = "rev_${System.currentTimeMillis()}",
            authorName = author,
            authorBadge = "🌟 Explorador",
            avatarInitials = initials,
            rating = state.reviewRating,
            comment = state.reviewComment.ifBlank { "¡Lugar increíble y recomendado para visitar en el Eje Cafetero!" }
        )

        onReviewAdded(newReview)

        _uiState.update {
            it.copy(
                showReviewDialog = false,
                reviewAuthor = "",
                reviewComment = "",
                reviewRating = 5,
                feedbackMessage = "🎉 ¡Reseña publicada! Ganaste +25 XP"
            )
        }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}
