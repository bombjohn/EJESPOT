package com.example.ejespot.features.spots.detail

import androidx.lifecycle.ViewModel
import com.example.ejespot.R
import com.example.ejespot.core.data.SpotStore
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.core.util.ResourceProviderHolder
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SpotDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SpotDetailUiState())
    val uiState: StateFlow<SpotDetailUiState> = _uiState.asStateFlow()

    /** Textos de la pantalla tomados de `strings.xml` (guía 14). */
    private val resources get() = ResourceProviderHolder.resources

    fun setSpot(spot: Spot?) {
        _uiState.update {
            it.copy(
                spot = spot,
                isSaved = spot?.let { s -> SpotStore.isSaved(s.id) } ?: false,
                isVisited = spot?.let { s -> SpotStore.isVisited(s.id) } ?: false,
                reviewAuthor = if (SessionManager.isGuest) "" else SessionManager.displayName
            )
        }
    }

    fun toggleSaved() {
        val spotId = _uiState.value.spot?.id ?: return
        val nowSaved = SpotStore.toggleSaved(spotId)
        _uiState.update {
            it.copy(
                isSaved = nowSaved,
                feedbackMessage = if (nowSaved) {
                    resources.getString(R.string.vm_saved_to_favorites)
                } else {
                    resources.getString(R.string.vm_removed_from_favorites)
                }
            )
        }
    }

    fun toggleVisited() {
        val spotId = _uiState.value.spot?.id ?: return
        val nowVisited = SpotStore.toggleVisited(spotId)
        _uiState.update {
            it.copy(
                isVisited = nowVisited,
                feedbackMessage = if (nowVisited) {
                    resources.getString(R.string.vm_marked_visited)
                } else {
                    resources.getString(R.string.vm_unmarked_visited)
                }
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
        val author = when {
            state.reviewAuthor.isNotBlank() -> state.reviewAuthor.trim()
            !SessionManager.isGuest -> SessionManager.displayName
            else -> resources.getString(R.string.vm_default_review_author)
        }
        val initials = author.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()
            .ifEmpty { "EX" }

        val newReview = SpotReview(
            id = "rev_${System.currentTimeMillis()}",
            authorName = author,
            authorBadge = if (SessionManager.isGuest) {
                resources.getString(R.string.vm_explorer_badge)
            } else {
                resources.getString(R.string.vm_guide_badge, SessionManager.roleLabel)
            },
            avatarInitials = initials,
            rating = state.reviewRating,
            comment = state.reviewComment.ifBlank {
                resources.getString(R.string.vm_default_review_comment)
            },
            authorProfileId = if (SessionManager.isGuest) null else SessionManager.profileId
        )

        onReviewAdded(newReview)

        _uiState.update {
            it.copy(
                showReviewDialog = false,
                reviewAuthor = "",
                reviewComment = "",
                reviewRating = 5,
                feedbackMessage = resources.getString(R.string.vm_review_published)
            )
        }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}
