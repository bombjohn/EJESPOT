package com.example.ejespot.core.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Estado compartido de los puntos de interés (Fase 2, todo en memoria).
 *
 * - Los lugares y las reseñas son globales (los ve toda la comunidad).
 * - Los "visitados" y "guardados" son independientes por usuario:
 *   se guardan en un mapa cuya llave es el `profileId` de la sesión.
 *
 * Al ser un `object`, sobrevive a la navegación y a la rotación de pantalla.
 */
object SpotStore {

    private val _spots = MutableStateFlow(SpotSampleData.spots)
    val spots: StateFlow<List<Spot>> = _spots.asStateFlow()

    private val _pendingSpots = MutableStateFlow(SpotSampleData.pendingSpots)
    val pendingSpots: StateFlow<List<Spot>> = _pendingSpots.asStateFlow()

    // --- Relación usuario <-> lugares ---

    private val savedByUser = mutableStateMapOf<String, Set<String>>(
        // Renata y Camila ya tienen lugares guardados al entrar.
        "renata" to setOf("1", "2", "5"),
        "camila" to setOf("6", "11"),
        "mateo" to setOf("3"),
        "invitado" to emptySet()
    )

    private val visitedByUser = mutableStateMapOf<String, Set<String>>(
        "renata" to setOf("1", "2", "3", "5", "6", "7", "8", "9", "10", "11", "13", "14", "15"),
        "camila" to setOf("1", "6", "11", "12"),
        "mateo" to setOf("3", "9"),
        "invitado" to emptySet()
    )

    /** Códigos de lugares que el usuario actual tiene marcados como visitados. */
    private val _visitedSpotIds = MutableStateFlow(currentVisited())
    val visitedSpotIds: StateFlow<Set<String>> = _visitedSpotIds.asStateFlow()

    /** Códigos de lugares que el usuario actual tiene guardados. */
    private val _savedSpotIds = MutableStateFlow(currentSaved())
    val savedSpotIds: StateFlow<Set<String>> = _savedSpotIds.asStateFlow()

    private fun currentKey(): String = SessionManager.profileId

    private fun currentVisited(): Set<String> = visitedByUser[currentKey()] ?: emptySet()

    private fun currentSaved(): Set<String> = savedByUser[currentKey()] ?: emptySet()

    /**
     * Vuelve a emitir los conjuntos del usuario actual.
     * Debe llamarse al iniciar sesión y al cerrar sesión.
     */
    fun refreshForCurrentUser() {
        _visitedSpotIds.value = currentVisited()
        _savedSpotIds.value = currentSaved()
    }

    // --- Consultas ---

    fun getSpotById(id: String): Spot? =
        (_spots.value + _pendingSpots.value).firstOrNull { it.id == id }

    /** Lista completa de lugares (verificados + pendientes) en memoria. */
    fun allSpots(): List<Spot> = _spots.value + _pendingSpots.value

    fun isVisited(spotId: String): Boolean = currentVisited().contains(spotId)

    fun isSaved(spotId: String): Boolean = currentSaved().contains(spotId)

    // --- Acciones ---

    /** Marca o desmarca un lugar como visitado para el usuario actual. */
    fun toggleVisited(spotId: String): Boolean {
        val key = currentKey()
        val current = visitedByUser[key] ?: emptySet()
        val nowVisited = spotId !in current
        visitedByUser[key] = if (nowVisited) current + spotId else current - spotId
        _visitedSpotIds.value = visitedByUser[key] ?: emptySet()
        return nowVisited
    }

    /** Marca o desmarca un lugar como guardado para el usuario actual. */
    fun toggleSaved(spotId: String): Boolean {
        val key = currentKey()
        val current = savedByUser[key] ?: emptySet()
        val nowSaved = spotId !in current
        savedByUser[key] = if (nowSaved) current + spotId else current - spotId
        _savedSpotIds.value = savedByUser[key] ?: emptySet()
        return nowSaved
    }

    /** Agrega una reseña (global) al lugar indicado. */
    fun addReview(spotId: String, review: SpotReview) {
        val spot = getSpotById(spotId) ?: return
        if (spot.isPending) {
            _pendingSpots.update { list ->
                list.map { if (it.id == spotId) it.withReview(review) else it }
            }
        } else {
            _spots.update { list ->
                list.map { if (it.id == spotId) it.withReview(review) else it }
            }
        }
    }

    /** Registra un punto propuesto por la comunidad. */
    fun addPendingSpot(spot: Spot) {
        _pendingSpots.update { listOf(spot.copy(isPending = true)) + it }
    }

    private fun Spot.withReview(review: SpotReview) = copy(
        reviews = listOf(review) + reviews,
        reviewCount = reviewCount + 1
    )
}
