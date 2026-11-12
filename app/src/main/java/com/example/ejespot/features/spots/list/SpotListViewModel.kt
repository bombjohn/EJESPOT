package com.example.ejespot.features.spots.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ejespot.data.repository.local.LocalSpotRepository
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import com.example.ejespot.domain.repository.SpotRepository
import com.example.ejespot.domain.usecase.ToggleImportanceVoteUseCase
import com.example.ejespot.domain.usecase.ToggleSaveSpotUseCase
import com.example.ejespot.domain.usecase.ToggleVisitSpotUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la lista de lugares (feed y mapa).
 *
 * Utiliza [SpotRepository] y los casos de uso para acceder a los datos compartidos.
 */
class SpotListViewModel(
    private val spotRepository: SpotRepository = LocalSpotRepository(),
    private val toggleSaveSpotUseCase: ToggleSaveSpotUseCase = ToggleSaveSpotUseCase(spotRepository),
    private val toggleVisitSpotUseCase: ToggleVisitSpotUseCase = ToggleVisitSpotUseCase(spotRepository),
    private val toggleImportanceVoteUseCase: ToggleImportanceVoteUseCase = ToggleImportanceVoteUseCase(spotRepository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpotListUiState())
    val uiState: StateFlow<SpotListUiState> = _uiState.asStateFlow()

    /** Códigos de los lugares visitados por el usuario actual. */
    val visitedSpotIds: StateFlow<Set<String>> = spotRepository.visitedSpotIds

    /** Códigos de los lugares guardados por el usuario actual. */
    val savedSpotIds: StateFlow<Set<String>> = spotRepository.savedSpotIds

    /** Códigos de los lugares votados como 'Me interesa' por el usuario actual. */
    val votedSpotIds: StateFlow<Set<String>> = spotRepository.votedSpotIds

    init {
        refresh()
        // La lista se actualiza cuando cambian los lugares del repositorio.
        viewModelScope.launch {
            spotRepository.spots.collect { refresh() }
        }
        viewModelScope.launch {
            spotRepository.pendingSpots.collect { refresh() }
        }
    }

    /** Recalcula la lista visible aplicando categoría y búsqueda. */
    private fun refresh() {
        val all = spotRepository.allSpots()
        _uiState.update { state ->
            state.copy(
                spots = applyFilters(all, state),
                pendingSpots = all.filter { it.isPending }
            )
        }
    }

    private fun applyFilters(spots: List<Spot>, state: SpotListUiState): List<Spot> {
        return spots.filter { spot ->
            val matchesCategory = state.selectedCategory == "Todos" ||
                spot.category == state.selectedCategory
            val matchesSearch = state.searchQuery.isBlank() ||
                spot.name.contains(state.searchQuery, ignoreCase = true) ||
                spot.municipality.contains(state.searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        refresh()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        refresh()
    }

    // --- Consultas y acciones delegadas en el repositorio y casos de uso ---

    fun getSpotById(id: String): Spot? = spotRepository.getSpotById(id)

    fun addReview(spotId: String, newReview: SpotReview) = spotRepository.addReview(spotId, newReview)

    fun addPendingSpot(newSpot: Spot) = spotRepository.addPendingSpot(newSpot)

    fun toggleSaveSpot(spotId: String) {
        toggleSaveSpotUseCase(spotId)
    }

    fun toggleVisitedSpot(spotId: String) {
        toggleVisitSpotUseCase(spotId)
    }

    fun toggleImportanceVote(spotId: String) {
        toggleImportanceVoteUseCase(spotId)
    }

    fun isSpotSaved(spotId: String): Boolean = spotRepository.isSaved(spotId)

    fun isSpotVisited(spotId: String): Boolean = spotRepository.isVisited(spotId)

    fun isSpotVotedImportant(spotId: String): Boolean = spotRepository.isVotedImportant(spotId)
}
