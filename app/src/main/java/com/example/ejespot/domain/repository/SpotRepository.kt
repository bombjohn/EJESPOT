package com.example.ejespot.domain.repository

import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato de acceso a los puntos de interés (Spots) de la app.
 *
 * Desacopla la lógica de negocio y los ViewModels de los detalles de
 * almacenamiento en memoria o base de datos local/remota.
 */
interface SpotRepository {
    val spots: StateFlow<List<Spot>>
    val pendingSpots: StateFlow<List<Spot>>
    val visitedSpotIds: StateFlow<Set<String>>
    val savedSpotIds: StateFlow<Set<String>>

    fun allSpots(): List<Spot>
    fun getSpotById(id: String): Spot?
    fun isVisited(spotId: String): Boolean
    fun isSaved(spotId: String): Boolean
    fun toggleVisited(spotId: String): Boolean
    fun toggleSaved(spotId: String): Boolean
    fun addReview(spotId: String, review: SpotReview)
    fun addPendingSpot(spot: Spot)
    fun approvePendingSpot(spotId: String)
    fun rejectPendingSpot(spotId: String)
    fun updateSpot(
        id: String,
        name: String,
        category: String,
        municipality: String,
        hours: String,
        priceRange: String,
        description: String
    )
    fun finalizeSpot(id: String, finalized: Boolean = true)
    fun deleteSpot(id: String)
    fun clearUserData(profileId: String)
    fun refreshForCurrentUser()
}
