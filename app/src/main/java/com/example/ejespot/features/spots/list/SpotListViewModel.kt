package com.example.ejespot.features.spots.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ejespot.core.data.SpotStore
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la lista de lugares (feed y mapa).
 *
 * Los datos viven en [SpotStore], que es el repositorio compartido en
 * memoria: así el feed, el mapa y la pantalla de visitados ven la misma
 * información y no se duplica al crear el ViewModel.
 */
class SpotListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SpotListUiState())
    val uiState: StateFlow<SpotListUiState> = _uiState.asStateFlow()

    /** Códigos de los lugares visitados por el usuario actual. */
    val visitedSpotIds: StateFlow<Set<String>> = SpotStore.visitedSpotIds

    /** Códigos de los lugares guardados por el usuario actual. */
    val savedSpotIds: StateFlow<Set<String>> = SpotStore.savedSpotIds

    init {
        refresh()
        // La lista se actualiza cuando cambian los lugares del store.
        viewModelScope.launch {
            SpotStore.spots.collect { refresh() }
        }
        viewModelScope.launch {
            SpotStore.pendingSpots.collect { refresh() }
        }
    }

    /** Recalcula la lista visible aplicando categoría y búsqueda. */
    private fun refresh() {
        val all = SpotStore.allSpots()
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

    // --- Consultas y acciones delegadas en el store compartido ---

    fun getSpotById(id: String): Spot? = SpotStore.getSpotById(id)

    fun addReview(spotId: String, newReview: SpotReview) = SpotStore.addReview(spotId, newReview)

    fun addPendingSpot(newSpot: Spot) = SpotStore.addPendingSpot(newSpot)

    fun toggleSaveSpot(spotId: String) {
        SpotStore.toggleSaved(spotId)
    }

    fun toggleVisitedSpot(spotId: String) {
        SpotStore.toggleVisited(spotId)
    }

    fun isSpotSaved(spotId: String): Boolean = SpotStore.isSaved(spotId)

    fun isSpotVisited(spotId: String): Boolean = SpotStore.isVisited(spotId)
}
