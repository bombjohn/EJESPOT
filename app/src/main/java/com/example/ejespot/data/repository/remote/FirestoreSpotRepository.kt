package com.example.ejespot.data.repository.remote

import com.example.ejespot.domain.model.AppNotification
import com.example.ejespot.domain.model.Location
import com.example.ejespot.domain.model.NotificationType
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Repositorio de spots que persiste los datos en **Firebase Firestore**.
 *
 * Colecciones en Firestore:
 * - `spots/`        → Documentos con los datos de cada publicación
 * - `reviews/{spotId}/items/` → Sub-colección de reseñas de cada spot
 * - `notifications/` → Notificaciones por usuario
 * - `votes/`        → Registro de votos de importancia por usuario
 *
 * Si Firestore no está configurado, los métodos capturan las excepciones
 * y no propagan errores (la app sigue funcionando con SpotStore local).
 */
class FirestoreSpotRepository {

    private val db: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (_: Exception) {
        null
    }

    // -------------------------------------------------------------------------
    // SPOTS
    // -------------------------------------------------------------------------

    /** Guarda o actualiza un Spot en Firestore. */
    suspend fun saveSpot(spot: Spot) {
        db ?: return
        try {
            val data = hashMapOf(
                "id" to spot.id,
                "name" to spot.name,
                "category" to spot.category,
                "municipality" to spot.municipality,
                "rating" to spot.rating,
                "reviewCount" to spot.reviewCount,
                "distance" to spot.distance,
                "isVerified" to spot.isVerified,
                "priceBadge" to spot.priceBadge,
                "hours" to spot.hours,
                "priceRange" to spot.priceRange,
                "description" to spot.description,
                "addedBy" to spot.addedBy,
                "department" to spot.department,
                "latitude" to spot.location.latitude,
                "longitude" to spot.location.longitude,
                "isPending" to spot.isPending,
                "isFinalized" to spot.isFinalized,
                "importanceVotes" to spot.importanceVotes,
                "imageUrl" to (spot.imageUrl ?: ""),
                "imageUri" to (spot.imageUri ?: ""),
                "emoji" to spot.emoji,
                "accentColor" to spot.accentColor,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            db.collection("spots").document(spot.id).set(data).await()
        } catch (_: Exception) { /* Firestore no disponible */ }
    }

    /** Carga todos los spots desde Firestore. */
    suspend fun loadAllSpots(): List<Spot> {
        db ?: return emptyList()
        return try {
            val snapshot = db.collection("spots").get().await()
            snapshot.documents.mapNotNull { doc -> mapDocToSpot(doc) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Escucha en tiempo real cambios en la colección de spots.
     * Útil para que moderadores y usuarios vean nuevos spots sin reiniciar la app.
     */
    fun listenToSpots(onSpotsUpdated: (List<Spot>) -> Unit): ListenerRegistration? {
        val db = db ?: return null
        return try {
            db.collection("spots").addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val spots = snapshot.documents.mapNotNull { doc -> mapDocToSpot(doc) }
                onSpotsUpdated(spots)
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun mapDocToSpot(doc: DocumentSnapshot): Spot? {
        return try {
            val name = doc.getString("name") ?: ""
            if (name.isBlank()) return null
            Spot(
                id = doc.getString("id") ?: doc.id,
                name = name,
                category = doc.getString("category") ?: "Naturaleza",
                municipality = doc.getString("municipality") ?: "",
                rating = doc.getDouble("rating") ?: 5.0,
                reviewCount = (doc.getLong("reviewCount") ?: 0).toInt(),
                distance = doc.getString("distance") ?: "1.0 km",
                isVerified = doc.getBoolean("isVerified") ?: false,
                priceBadge = doc.getString("priceBadge") ?: "",
                hours = doc.getString("hours") ?: "",
                priceRange = doc.getString("priceRange") ?: "",
                description = doc.getString("description") ?: "",
                addedBy = doc.getString("addedBy") ?: "",
                department = doc.getString("department") ?: "",
                location = Location(
                    latitude = doc.getDouble("latitude") ?: 4.64,
                    longitude = doc.getDouble("longitude") ?: -75.57
                ),
                isPending = doc.getBoolean("isPending") ?: false,
                isFinalized = doc.getBoolean("isFinalized") ?: false,
                importanceVotes = (doc.getLong("importanceVotes") ?: 0).toInt(),
                imageUrl = doc.getString("imageUrl")?.ifBlank { null },
                imageUri = doc.getString("imageUri")?.ifBlank { null },
                emoji = doc.getString("emoji") ?: "📍",
                accentColor = doc.getLong("accentColor") ?: 0xFF2C5640
            )
        } catch (_: Exception) {
            null
        }
    }

    /** Actualiza campos parciales de un spot existente. */
    suspend fun updateSpotFields(spotId: String, fields: Map<String, Any>) {
        db ?: return
        try {
            db.collection("spots").document(spotId).update(fields).await()
        } catch (_: Exception) { /* ignorar */ }
    }

    /** Elimina un spot de Firestore. */
    suspend fun deleteSpot(spotId: String) {
        db ?: return
        try {
            db.collection("spots").document(spotId).delete().await()
        } catch (_: Exception) { /* ignorar */ }
    }

    // -------------------------------------------------------------------------
    // REVIEWS (sub-colección)
    // -------------------------------------------------------------------------

    /** Agrega una reseña al spot en Firestore. */
    suspend fun addReview(spotId: String, review: SpotReview) {
        db ?: return
        try {
            val data = hashMapOf(
                "id" to review.id,
                "authorName" to review.authorName,
                "authorBadge" to review.authorBadge,
                "avatarInitials" to review.avatarInitials,
                "rating" to review.rating,
                "comment" to review.comment,
                "date" to review.date,
                "authorProfileId" to (review.authorProfileId ?: ""),
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            db.collection("spots").document(spotId)
                .collection("reviews").document(review.id)
                .set(data).await()
        } catch (_: Exception) { /* ignorar */ }
    }

    // -------------------------------------------------------------------------
    // NOTIFICATIONS
    // -------------------------------------------------------------------------

    /** Guarda una notificación en Firestore. */
    suspend fun saveNotification(notification: AppNotification) {
        db ?: return
        try {
            val data = hashMapOf(
                "id" to notification.id,
                "userId" to notification.userId,
                "type" to notification.type.name,
                "title" to notification.title,
                "message" to notification.message,
                "relatedSpotId" to (notification.relatedSpotId ?: ""),
                "isRead" to notification.isRead,
                "timestamp" to notification.timestamp
            )
            db.collection("notifications").document(notification.id).set(data).await()
        } catch (_: Exception) { /* ignorar */ }
    }

    /** Carga todas las notificaciones desde Firestore. */
    suspend fun loadAllNotifications(): List<AppNotification> {
        db ?: return emptyList()
        return try {
            val snapshot = db.collection("notifications").get().await()
            snapshot.documents.mapNotNull { doc -> mapDocToNotification(doc) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    /** Escucha en tiempo real las notificaciones en Firestore. */
    fun listenToNotifications(onNotificationsUpdated: (List<AppNotification>) -> Unit): ListenerRegistration? {
        val db = db ?: return null
        return try {
            db.collection("notifications").addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull { doc -> mapDocToNotification(doc) }
                onNotificationsUpdated(list)
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun mapDocToNotification(doc: DocumentSnapshot): AppNotification? {
        return try {
            AppNotification(
                id = doc.getString("id") ?: doc.id,
                userId = doc.getString("userId") ?: "",
                type = try {
                    NotificationType.valueOf(doc.getString("type") ?: "SYSTEM")
                } catch (_: Exception) {
                    NotificationType.SYSTEM
                },
                title = doc.getString("title") ?: "",
                message = doc.getString("message") ?: "",
                relatedSpotId = doc.getString("relatedSpotId")?.ifBlank { null },
                isRead = doc.getBoolean("isRead") ?: false,
                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
            )
        } catch (_: Exception) {
            null
        }
    }

    /** Carga las notificaciones de un usuario desde Firestore. */
    suspend fun loadNotifications(userId: String): List<AppNotification> {
        db ?: return emptyList()
        return try {
            val snapshot = db.collection("notifications")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    AppNotification(
                        id = doc.getString("id") ?: doc.id,
                        userId = doc.getString("userId") ?: "",
                        type = try {
                            NotificationType.valueOf(doc.getString("type") ?: "SYSTEM")
                        } catch (_: Exception) {
                            NotificationType.SYSTEM
                        },
                        title = doc.getString("title") ?: "",
                        message = doc.getString("message") ?: "",
                        relatedSpotId = doc.getString("relatedSpotId")?.ifBlank { null },
                        isRead = doc.getBoolean("isRead") ?: false,
                        timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                    )
                } catch (_: Exception) { null }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    /** Marca una notificación como leída en Firestore. */
    suspend fun markNotificationRead(notificationId: String) {
        db ?: return
        try {
            db.collection("notifications").document(notificationId)
                .update("isRead", true).await()
        } catch (_: Exception) { /* ignorar */ }
    }

    // -------------------------------------------------------------------------
    // VOTES
    // -------------------------------------------------------------------------

    /** Registra o elimina el voto de importancia de un usuario para un spot. */
    suspend fun toggleVote(userId: String, spotId: String, isVoting: Boolean) {
        db ?: return
        try {
            val voteId = "${userId}_$spotId"
            if (isVoting) {
                db.collection("votes").document(voteId)
                    .set(hashMapOf("userId" to userId, "spotId" to spotId))
                    .await()
            } else {
                db.collection("votes").document(voteId).delete().await()
            }
        } catch (_: Exception) { /* ignorar */ }
    }
}
