package com.example.ejespot.core.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.data.repository.remote.FirestoreSpotRepository
import com.example.ejespot.domain.model.AppNotification
import com.example.ejespot.domain.model.NotificationType
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotReview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado compartido de los puntos de interés con sincronización reactiva local
 * y persistencia en la nube mediante [FirestoreSpotRepository].
 *
 * - Los lugares y las reseñas son globales (los ve toda la comunidad).
 * - Los "visitados" y "guardados" son independientes por usuario:
 *   se guardan en un mapa cuya llave es el `profileId` de la sesión.
 *
 * Al ser un `object`, sobrevive a la navegación y a la rotación de pantalla.
 */
object SpotStore {

    private val firestoreScope = CoroutineScope(Dispatchers.IO)
    private val firestoreRepo = FirestoreSpotRepository()

    private var prefs: android.content.SharedPreferences? = null

    fun init(context: android.content.Context) {
        if (prefs != null) return
        val p = context.getSharedPreferences("ejespot_store_prefs", android.content.Context.MODE_PRIVATE)
        prefs = p

        // Cargar conjuntos guardados, visitados y votos persistidos
        p.all.forEach { (k, value) ->
            if (value is Set<*> && value.all { it is String }) {
                @Suppress("UNCHECKED_CAST")
                val set = value as Set<String>
                when {
                    k.startsWith("saved_") -> savedByUser[k.removePrefix("saved_")] = set
                    k.startsWith("visited_") -> visitedByUser[k.removePrefix("visited_")] = set
                    k.startsWith("voted_") -> votedImportantByUser[k.removePrefix("voted_")] = set
                }
            }
        }

        // Cargar spots personalizados creados por el usuario
        val savedSpotsJson = p.getString("custom_spots", null)
        if (!savedSpotsJson.isNullOrBlank()) {
            runCatching {
                val array = org.json.JSONArray(savedSpotsJson)
                val loadedList = mutableListOf<Spot>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    loadedList.add(
                        Spot(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            category = obj.getString("category"),
                            municipality = obj.getString("municipality"),
                            rating = obj.optDouble("rating", 5.0),
                            reviewCount = obj.optInt("reviewCount", 0),
                            distance = obj.optString("distance", "1.0 km"),
                            isVerified = obj.optBoolean("isVerified", false),
                            priceBadge = obj.optString("priceBadge", ""),
                            hours = obj.optString("hours", ""),
                            priceRange = obj.optString("priceRange", ""),
                            description = obj.optString("description", ""),
                            addedBy = obj.optString("addedBy", "@usuario"),
                            imageUrl = obj.optString("imageUrl").ifBlank { null },
                            imageUri = obj.optString("imageUri").ifBlank { null },
                            location = com.example.ejespot.domain.model.Location(
                                latitude = obj.optDouble("lat", 4.8143),
                                longitude = obj.optDouble("lng", -75.6946)
                            ),
                            isPending = obj.optBoolean("isPending", true),
                            isFinalized = obj.optBoolean("isFinalized", false),
                            importanceVotes = obj.optInt("importanceVotes", 0)
                        )
                    )
                }
                if (loadedList.isNotEmpty()) {
                    _pendingSpots.update { current ->
                        val existingIds = current.map { it.id }.toSet()
                        loadedList.filter { it.id !in existingIds && it.isPending } + current
                    }
                    _spots.update { current ->
                        val existingIds = current.map { it.id }.toSet()
                        loadedList.filter { it.id !in existingIds && !it.isPending } + current
                    }
                }
            }
        }

        // Cargar notificaciones persistidas
        val savedNotifsJson = p.getString("custom_notifications", null)
        if (!savedNotifsJson.isNullOrBlank()) {
            runCatching {
                val array = org.json.JSONArray(savedNotifsJson)
                val loadedNotifs = mutableListOf<AppNotification>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    loadedNotifs.add(
                        AppNotification(
                            id = obj.getString("id"),
                            userId = obj.getString("userId"),
                            title = obj.getString("title"),
                            message = obj.getString("message"),
                            type = try {
                                NotificationType.valueOf(obj.getString("type"))
                            } catch (_: Exception) {
                                NotificationType.SYSTEM
                            },
                            relatedSpotId = obj.optString("relatedSpotId").ifBlank { null },
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                            isRead = obj.optBoolean("isRead", false)
                        )
                    )
                }
                if (loadedNotifs.isNotEmpty()) {
                    _notifications.update { current ->
                        val existingIds = current.map { it.id }.toSet()
                        loadedNotifs.filter { it.id !in existingIds } + current
                    }
                }
            }
        }

        refreshForCurrentUser()
    }

    private fun persistCustomSpots() {
        val p = prefs ?: return
        try {
            val allCustom = (_spots.value + _pendingSpots.value).filter { it.id.startsWith("spot_") }
            val array = org.json.JSONArray()
            for (spot in allCustom) {
                val obj = org.json.JSONObject().apply {
                    put("id", spot.id)
                    put("name", spot.name)
                    put("category", spot.category)
                    put("municipality", spot.municipality)
                    put("rating", spot.rating)
                    put("reviewCount", spot.reviewCount)
                    put("distance", spot.distance)
                    put("isVerified", spot.isVerified)
                    put("priceBadge", spot.priceBadge)
                    put("hours", spot.hours)
                    put("priceRange", spot.priceRange)
                    put("description", spot.description)
                    put("addedBy", spot.addedBy)
                    put("imageUrl", spot.imageUrl ?: "")
                    put("imageUri", spot.imageUri ?: "")
                    put("lat", spot.location.latitude)
                    put("lng", spot.location.longitude)
                    put("isPending", spot.isPending)
                    put("isFinalized", spot.isFinalized)
                    put("importanceVotes", spot.importanceVotes)
                }
                array.put(obj)
            }
            p.edit().putString("custom_spots", array.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun persistNotifications() {
        val p = prefs ?: return
        try {
            val array = org.json.JSONArray()
            for (notif in _notifications.value) {
                val obj = org.json.JSONObject().apply {
                    put("id", notif.id)
                    put("userId", notif.userId)
                    put("title", notif.title)
                    put("message", notif.message)
                    put("type", notif.type.name)
                    put("relatedSpotId", notif.relatedSpotId ?: "")
                    put("timestamp", notif.timestamp)
                    put("isRead", notif.isRead)
                }
                array.put(obj)
            }
            p.edit().putString("custom_notifications", array.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun persistUserSets(key: String) {
        val p = prefs ?: return
        try {
            p.edit()
                .putStringSet("saved_$key", savedByUser[key] ?: emptySet())
                .putStringSet("visited_$key", visitedByUser[key] ?: emptySet())
                .putStringSet("voted_$key", votedImportantByUser[key] ?: emptySet())
                .apply()
        } catch (_: Exception) {}
    }

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

    /** Votos de 'Es importante / Me interesa' por usuario. */
    private val votedImportantByUser = mutableStateMapOf<String, Set<String>>(
        "renata" to setOf("1", "3"),
        "camila" to setOf("1", "2"),
        "mateo" to setOf("1"),
        "invitado" to emptySet()
    )

    /** Códigos de lugares que el usuario actual tiene marcados como visitados. */
    private val _visitedSpotIds = MutableStateFlow(currentVisited())
    val visitedSpotIds: StateFlow<Set<String>> = _visitedSpotIds.asStateFlow()

    /** Códigos de lugares que el usuario actual tiene guardados. */
    private val _savedSpotIds = MutableStateFlow(currentSaved())
    val savedSpotIds: StateFlow<Set<String>> = _savedSpotIds.asStateFlow()

    /** Códigos de lugares que el usuario actual ha votado como importantes. */
    private val _votedSpotIds = MutableStateFlow(currentVotedImportant())
    val votedSpotIds: StateFlow<Set<String>> = _votedSpotIds.asStateFlow()

    // --- Sistema de Notificaciones en memoria (Fase 1) ---
    private val _notifications = MutableStateFlow<List<AppNotification>>(
        listOf(
            AppNotification(
                id = "notif_1",
                userId = "camila",
                title = "Nuevo comentario en tu publicación",
                message = "Carlos Gómez comentó: 'Excelente lugar, la vista al atardecer es mágica.'",
                type = NotificationType.COMMENT,
                relatedSpotId = "1",
                timestamp = System.currentTimeMillis() - 3600000 * 2,
                isRead = false
            ),
            AppNotification(
                id = "notif_2",
                userId = "camila",
                title = "¡Publicación Aprobada!",
                message = "El moderador Mateo Salazar verificó y publicó 'Mirador Valle de Cocora'. Ganaste +100 XP.",
                type = NotificationType.APPROVAL,
                relatedSpotId = "1",
                timestamp = System.currentTimeMillis() - 3600000 * 24,
                isRead = true
            ),
            AppNotification(
                id = "notif_3",
                userId = "camila",
                title = "¡Nueva Insignia Desbloqueada!",
                message = "¡Felicidades! Alcanzaste el nivel 'Aventurero' con más de 100 XP acumulados.",
                type = NotificationType.ACHIEVEMENT,
                relatedSpotId = null,
                timestamp = System.currentTimeMillis() - 3600000 * 48,
                isRead = true
            ),
            AppNotification(
                id = "notif_4",
                userId = "renata",
                title = "¡A alguien le interesa tu publicación!",
                message = "A Sofía le pareció importante 'Termales Santa Rosa de Cabal'.",
                type = NotificationType.VOTE,
                relatedSpotId = "2",
                timestamp = System.currentTimeMillis() - 3600000 * 5,
                isRead = false
            ),
            AppNotification(
                id = "notif_5",
                userId = "mateo",
                title = "Nuevo spot propuesto",
                message = "Se ha propuesto 'Café San Alberto' en Buenavista para revisión de moderación.",
                type = NotificationType.SYSTEM,
                relatedSpotId = "3",
                timestamp = System.currentTimeMillis() - 3600000 * 8,
                isRead = false
            )
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private fun currentKey(): String = SessionManager.profileId

    private fun currentVisited(): Set<String> = visitedByUser[currentKey()] ?: emptySet()

    private fun currentSaved(): Set<String> = savedByUser[currentKey()] ?: emptySet()

    private fun currentVotedImportant(): Set<String> = votedImportantByUser[currentKey()] ?: emptySet()

    /**
     * Vuelve a emitir los conjuntos del usuario actual.
     * Debe llamarse al iniciar sesión y al cerrar sesión.
     */
    fun refreshForCurrentUser() {
        _visitedSpotIds.value = currentVisited()
        _savedSpotIds.value = currentSaved()
        _votedSpotIds.value = currentVotedImportant()
    }

    // --- Notificaciones ---

    fun getNotificationsForUser(userId: String): List<AppNotification> =
        _notifications.value.filter { it.userId == userId || it.userId == "all" }
            .sortedByDescending { it.timestamp }

    fun markNotificationAsRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
        persistNotifications()
        firestoreScope.launch {
            firestoreRepo.markNotificationRead(id)
        }
    }

    fun markAllNotificationsAsRead(userId: String) {
        _notifications.update { list ->
            list.map { if (it.userId == userId) it.copy(isRead = true) else it }
        }
        persistNotifications()
    }

    fun addNotification(notification: AppNotification) {
        _notifications.update { listOf(notification) + it }
        persistNotifications()
        firestoreScope.launch {
            firestoreRepo.saveNotification(notification)
        }
    }

    // --- Consultas ---

    fun getSpotById(id: String): Spot? =
        (_spots.value + _pendingSpots.value).firstOrNull { it.id == id }

    /** Lista completa de lugares (verificados + pendientes) en memoria. */
    fun allSpots(): List<Spot> = _spots.value + _pendingSpots.value

    fun isVisited(spotId: String): Boolean = currentVisited().contains(spotId)

    fun isSaved(spotId: String): Boolean = currentSaved().contains(spotId)

    fun isVotedImportant(spotId: String): Boolean = currentVotedImportant().contains(spotId)

    // --- Acciones ---

    /** Vota o desmarca 'Es importante / Me interesa' para el usuario actual. */
    fun toggleImportanceVote(spotId: String): Boolean {
        val key = currentKey()
        val current = votedImportantByUser[key] ?: emptySet()
        val nowVoted = spotId !in current
        votedImportantByUser[key] = if (nowVoted) current + spotId else current - spotId
        _votedSpotIds.value = votedImportantByUser[key] ?: emptySet()

        val updater: (Spot) -> Spot = { spot ->
            val newVotes = if (nowVoted) spot.importanceVotes + 1 else (spot.importanceVotes - 1).coerceAtLeast(0)
            spot.copy(importanceVotes = newVotes)
        }
        _spots.update { list -> list.map { if (it.id == spotId) updater(it) else it } }
        _pendingSpots.update { list -> list.map { if (it.id == spotId) updater(it) else it } }

        if (nowVoted) {
            val spot = getSpotById(spotId)
            if (spot != null) {
                val author = spot.addedBy.removePrefix("@").trim()
                if (author.isNotBlank() && author != key && author != "tú" && author != "invitado") {
                    addNotification(
                        AppNotification(
                            id = "notif_${System.currentTimeMillis()}",
                            userId = author,
                            title = "¡A alguien le interesa tu publicación!",
                            message = "${SessionManager.displayName} votó 'Me interesa' en tu publicación '${spot.name}'.",
                            type = NotificationType.VOTE,
                            relatedSpotId = spotId
                        )
                    )
                }
            }
        }

        firestoreScope.launch {
            firestoreRepo.toggleVote(key, spotId, nowVoted)
            val spot = getSpotById(spotId)
            if (spot != null) {
                firestoreRepo.updateSpotFields(spotId, mapOf("importanceVotes" to spot.importanceVotes))
            }
        }

        persistUserSets(key)
        persistCustomSpots()

        return nowVoted
    }

    /** Marca o desmarca un lugar como visitado para el usuario actual. */
    fun toggleVisited(spotId: String): Boolean {
        val key = currentKey()
        val current = visitedByUser[key] ?: emptySet()
        val nowVisited = spotId !in current
        visitedByUser[key] = if (nowVisited) current + spotId else current - spotId
        _visitedSpotIds.value = visitedByUser[key] ?: emptySet()
        persistUserSets(key)
        return nowVisited
    }

    /** Marca o desmarca un lugar como guardado para el usuario actual. */
    fun toggleSaved(spotId: String): Boolean {
        val key = currentKey()
        val current = savedByUser[key] ?: emptySet()
        val nowSaved = spotId !in current
        savedByUser[key] = if (nowSaved) current + spotId else current - spotId
        _savedSpotIds.value = savedByUser[key] ?: emptySet()
        persistUserSets(key)
        return nowSaved
    }

    /** Agrega una reseña (global) al lugar indicado y notifica al autor. */
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

        firestoreScope.launch {
            firestoreRepo.addReview(spotId, review)
        }

        // Generar notificación para el autor de la publicación si no es el mismo usuario
        val author = spot.addedBy.removePrefix("@").trim()
        val current = SessionManager.profileId
        if (author.isNotBlank() && author != current && author != "tú" && author != "invitado") {
            addNotification(
                AppNotification(
                    id = "notif_${System.currentTimeMillis()}",
                    userId = author,
                    title = "Nuevo comentario en tu publicación",
                    message = "${review.authorName} comentó en '${spot.name}': \"${review.comment.take(60)}\"",
                    type = NotificationType.COMMENT,
                    relatedSpotId = spotId
                )
            )
        }
    }

    /** Registra un punto propuesto por la comunidad. */
    fun addPendingSpot(spot: Spot) {
        val pendingSpot = spot.copy(isPending = true)
        _pendingSpots.update { listOf(pendingSpot) + it }
        persistCustomSpots()
        firestoreScope.launch {
            firestoreRepo.saveSpot(pendingSpot)
        }
    }

    /**
     * Aprueba un spot pendiente: se retira de la lista de pendientes y
     * pasa a estar verificado y publicado en la lista oficial de spots.
     */
    fun approvePendingSpot(spotId: String) {
        val pending = _pendingSpots.value.firstOrNull { it.id == spotId }
        if (pending != null) {
            val approved = pending.copy(isPending = false, isVerified = true)
            _pendingSpots.update { list -> list.filterNot { it.id == spotId } }
            _spots.update { list -> listOf(approved) + list.filterNot { it.id == spotId } }
            persistCustomSpots()

            firestoreScope.launch {
                firestoreRepo.updateSpotFields(spotId, mapOf("isPending" to false, "isVerified" to true))
            }

            val author = pending.addedBy.removePrefix("@").trim()
            if (author.isNotBlank() && author != "tú" && author != "invitado") {
                addNotification(
                    AppNotification(
                        id = "notif_${System.currentTimeMillis()}",
                        userId = author,
                        title = "¡Publicación Verificada!",
                        message = "Un moderador aprobó '${pending.name}'. ¡Ya está disponible en el feed oficial (+100 XP)!",
                        type = NotificationType.APPROVAL,
                        relatedSpotId = spotId
                    )
                )
            }
        }
    }

    /**
     * Rechaza un spot pendiente con motivo y notifica al autor.
     */
    fun rejectPendingSpot(spotId: String, reason: String = "No cumple con las pautas comunitarias") {
        val pending = _pendingSpots.value.firstOrNull { it.id == spotId }
        if (pending != null) {
            _pendingSpots.update { list -> list.filterNot { it.id == spotId } }
            persistCustomSpots()

            firestoreScope.launch {
                firestoreRepo.deleteSpot(spotId)
            }

            val author = pending.addedBy.removePrefix("@").trim()
            if (author.isNotBlank() && author != "tú" && author != "invitado") {
                addNotification(
                    AppNotification(
                        id = "notif_${System.currentTimeMillis()}",
                        userId = author,
                        title = "Publicación no aprobada",
                        message = "Tu propuesta '${pending.name}' fue rechazada. Motivo: $reason",
                        type = NotificationType.REJECTION,
                        relatedSpotId = spotId
                    )
                )
            }
        }
    }

    /**
     * Actualiza la información editable de un spot existente.
     */
    fun updateSpot(
        id: String,
        name: String,
        category: String,
        municipality: String,
        hours: String,
        priceRange: String,
        description: String
    ) {
        val updater: (Spot) -> Spot = {
            it.copy(
                name = name.trim(),
                category = category.trim(),
                municipality = municipality.trim(),
                hours = hours.trim(),
                priceRange = priceRange.trim(),
                description = description.trim()
            )
        }
        _spots.update { list -> list.map { if (it.id == id) updater(it) else it } }
        _pendingSpots.update { list -> list.map { if (it.id == id) updater(it) else it } }
        persistCustomSpots()

        firestoreScope.launch {
            val updated = getSpotById(id)
            if (updated != null) firestoreRepo.saveSpot(updated)
        }
    }

    /**
     * Marca un spot como finalizado/inactivo o lo reactiva.
     */
    fun finalizeSpot(id: String, finalized: Boolean = true) {
        val updater: (Spot) -> Spot = { it.copy(isFinalized = finalized) }
        _spots.update { list -> list.map { if (it.id == id) updater(it) else it } }
        _pendingSpots.update { list -> list.map { if (it.id == id) updater(it) else it } }
        persistCustomSpots()

        firestoreScope.launch {
            firestoreRepo.updateSpotFields(id, mapOf("isFinalized" to finalized))
        }
    }

    /**
     * Elimina un spot por ID de ambas listas (verificados o pendientes).
     */
    fun deleteSpot(id: String) {
        _spots.update { list -> list.filterNot { it.id == id } }
        _pendingSpots.update { list -> list.filterNot { it.id == id } }
        persistCustomSpots()

        firestoreScope.launch {
            firestoreRepo.deleteSpot(id)
        }

        // También limpiar de guardados y visitados
        savedByUser.keys.forEach { user ->
            val set = savedByUser[user]
            if (set != null && id in set) savedByUser[user] = set - id
        }
        visitedByUser.keys.forEach { user ->
            val set = visitedByUser[user]
            if (set != null && id in set) visitedByUser[user] = set - id
        }
        refreshForCurrentUser()
    }

    /**
     * Limpia los datos asociados a un usuario cuando elimina su cuenta.
     */
    fun clearUserData(profileId: String) {
        savedByUser.remove(profileId)
        visitedByUser.remove(profileId)
        // Eliminar spots propuestos por este usuario
        _pendingSpots.update { list -> list.filterNot { it.addedBy == profileId } }
        persistCustomSpots()
        refreshForCurrentUser()
    }

    private fun Spot.withReview(review: SpotReview) = copy(
        reviews = listOf(review) + reviews,
        reviewCount = reviewCount + 1
    )
}
