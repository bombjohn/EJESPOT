package com.example.ejespot.features.spots.detail

import androidx.lifecycle.ViewModel
import com.example.ejespot.R
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.core.util.ResourceProviderHolder
import com.example.ejespot.data.repository.local.LocalSpotRepository
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import com.example.ejespot.domain.repository.SpotRepository
import com.example.ejespot.domain.usecase.DeleteSpotUseCase
import com.example.ejespot.domain.usecase.FinalizeSpotUseCase
import com.example.ejespot.domain.usecase.ToggleImportanceVoteUseCase
import com.example.ejespot.domain.usecase.ToggleSaveSpotUseCase
import com.example.ejespot.domain.usecase.ToggleVisitSpotUseCase
import com.example.ejespot.domain.usecase.UpdateSpotUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SpotDetailViewModel(
    private val spotRepository: SpotRepository = LocalSpotRepository(),
    private val toggleSaveSpotUseCase: ToggleSaveSpotUseCase = ToggleSaveSpotUseCase(spotRepository),
    private val toggleVisitSpotUseCase: ToggleVisitSpotUseCase = ToggleVisitSpotUseCase(spotRepository),
    private val toggleImportanceVoteUseCase: ToggleImportanceVoteUseCase = ToggleImportanceVoteUseCase(spotRepository),
    private val updateSpotUseCase: UpdateSpotUseCase = UpdateSpotUseCase(spotRepository),
    private val finalizeSpotUseCase: FinalizeSpotUseCase = FinalizeSpotUseCase(spotRepository),
    private val deleteSpotUseCase: DeleteSpotUseCase = DeleteSpotUseCase(spotRepository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpotDetailUiState())
    val uiState: StateFlow<SpotDetailUiState> = _uiState.asStateFlow()

    /** Textos de la pantalla tomados de `strings.xml` (guía 14). */
    private val resources get() = ResourceProviderHolder.resources

    fun setSpot(spot: Spot?) {
        _uiState.update {
            it.copy(
                spot = spot,
                isSaved = spot?.let { s -> spotRepository.isSaved(s.id) } ?: false,
                isVisited = spot?.let { s -> spotRepository.isVisited(s.id) } ?: false,
                isImportant = spot?.let { s -> spotRepository.isVotedImportant(s.id) } ?: false,
                reviewAuthor = if (SessionManager.isGuest) "" else SessionManager.displayName
            )
        }
    }

    fun toggleImportant() {
        if (SessionManager.isGuest) {
            _uiState.update { it.copy(feedbackMessage = "Inicia sesión para votar por lugares importantes") }
            return
        }
        val spotId = _uiState.value.spot?.id ?: return
        val nowImportant = toggleImportanceVoteUseCase(spotId)
        val updatedSpot = spotRepository.getSpotById(spotId)
        _uiState.update {
            it.copy(
                spot = updatedSpot,
                isImportant = nowImportant,
                feedbackMessage = if (nowImportant) "¡Marcado como importante! Ayudas a destacar este lugar." else "Voto retirado"
            )
        }
    }

    fun toggleSaved() {
        if (SessionManager.isGuest) {
            _uiState.update { it.copy(feedbackMessage = "Inicia sesión para guardar lugares en tus favoritos") }
            return
        }
        val spotId = _uiState.value.spot?.id ?: return
        val nowSaved = toggleSaveSpotUseCase(spotId)
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
        if (SessionManager.isGuest) {
            _uiState.update { it.copy(feedbackMessage = "Inicia sesión para registrar tus lugares visitados y ganar XP") }
            return
        }
        val spotId = _uiState.value.spot?.id ?: return
        val nowVisited = toggleVisitSpotUseCase(spotId)
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
        if (visible && SessionManager.isGuest) {
            _uiState.update { it.copy(feedbackMessage = "Inicia sesión para escribir una reseña y compartir tu experiencia") }
            return
        }
        _uiState.update { it.copy(showReviewDialog = visible) }
    }

    fun setEditDialogVisible(visible: Boolean) {
        if (visible && SessionManager.isGuest) {
            _uiState.update { it.copy(feedbackMessage = "Inicia sesión para editar este lugar") }
            return
        }
        _uiState.update { it.copy(showEditDialog = visible) }
    }

    fun setDeleteDialogVisible(visible: Boolean) {
        if (visible && SessionManager.isGuest) {
            _uiState.update { it.copy(feedbackMessage = "Inicia sesión para gestionar este lugar") }
            return
        }
        _uiState.update { it.copy(showDeleteDialog = visible) }
    }

    fun updateSpot(
        name: String,
        category: String,
        municipality: String,
        hours: String,
        priceRange: String,
        description: String
    ) {
        val spotId = _uiState.value.spot?.id ?: return
        updateSpotUseCase(spotId, name, category, municipality, hours, priceRange, description)
        _uiState.update { state ->
            state.copy(
                spot = spotRepository.getSpotById(spotId),
                showEditDialog = false,
                feedbackMessage = "Lugar actualizado correctamente"
            )
        }
    }

    fun toggleFinalizeSpot() {
        val spot = _uiState.value.spot ?: return
        val newFinalized = !spot.isFinalized
        finalizeSpotUseCase(spot.id, newFinalized)
        _uiState.update { state ->
            state.copy(
                spot = spotRepository.getSpotById(spot.id),
                feedbackMessage = if (newFinalized) "Lugar marcado como finalizado/inactivo" else "Lugar reactivado con éxito"
            )
        }
    }

    fun deleteSpot(onDeleted: () -> Unit) {
        val spotId = _uiState.value.spot?.id ?: return
        deleteSpotUseCase(spotId)
        _uiState.update { it.copy(showDeleteDialog = false, feedbackMessage = "Lugar eliminado") }
        onDeleted()
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
