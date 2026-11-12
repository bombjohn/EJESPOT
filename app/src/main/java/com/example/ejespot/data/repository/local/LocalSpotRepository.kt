package com.example.ejespot.data.repository.local

import com.example.ejespot.core.data.SpotStore
import com.example.ejespot.domain.model.AppNotification
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import com.example.ejespot.domain.repository.SpotRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * Implementación local en memoria del repositorio de Spots.
 */
class LocalSpotRepository : SpotRepository {
    override val spots: StateFlow<List<Spot>> = SpotStore.spots
    override val pendingSpots: StateFlow<List<Spot>> = SpotStore.pendingSpots
    override val visitedSpotIds: StateFlow<Set<String>> = SpotStore.visitedSpotIds
    override val savedSpotIds: StateFlow<Set<String>> = SpotStore.savedSpotIds
    override val votedSpotIds: StateFlow<Set<String>> = SpotStore.votedSpotIds
    override val notifications: StateFlow<List<AppNotification>> = SpotStore.notifications

    override fun allSpots(): List<Spot> = SpotStore.allSpots()
    override fun getSpotById(id: String): Spot? = SpotStore.getSpotById(id)
    override fun isVisited(spotId: String): Boolean = SpotStore.isVisited(spotId)
    override fun isSaved(spotId: String): Boolean = SpotStore.isSaved(spotId)
    override fun isVotedImportant(spotId: String): Boolean = SpotStore.isVotedImportant(spotId)
    override fun toggleVisited(spotId: String): Boolean = SpotStore.toggleVisited(spotId)
    override fun toggleSaved(spotId: String): Boolean = SpotStore.toggleSaved(spotId)
    override fun toggleImportanceVote(spotId: String): Boolean = SpotStore.toggleImportanceVote(spotId)
    override fun addReview(spotId: String, review: SpotReview) = SpotStore.addReview(spotId, review)
    override fun addPendingSpot(spot: Spot) = SpotStore.addPendingSpot(spot)
    override fun approvePendingSpot(spotId: String) = SpotStore.approvePendingSpot(spotId)
    override fun rejectPendingSpot(spotId: String, reason: String) = SpotStore.rejectPendingSpot(spotId, reason)
    override fun updateSpot(
        id: String,
        name: String,
        category: String,
        municipality: String,
        hours: String,
        priceRange: String,
        description: String
    ) = SpotStore.updateSpot(id, name, category, municipality, hours, priceRange, description)

    override fun finalizeSpot(id: String, finalized: Boolean) = SpotStore.finalizeSpot(id, finalized)
    override fun deleteSpot(id: String) = SpotStore.deleteSpot(id)
    override fun clearUserData(profileId: String) = SpotStore.clearUserData(profileId)
    override fun refreshForCurrentUser() = SpotStore.refreshForCurrentUser()

    override fun getNotificationsForUser(userId: String): List<AppNotification> =
        SpotStore.getNotificationsForUser(userId)
    override fun markNotificationAsRead(id: String) = SpotStore.markNotificationAsRead(id)
    override fun markAllNotificationsAsRead(userId: String) = SpotStore.markAllNotificationsAsRead(userId)
    override fun addNotification(notification: AppNotification) = SpotStore.addNotification(notification)
}
