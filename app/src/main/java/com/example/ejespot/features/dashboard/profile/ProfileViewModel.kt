package com.example.ejespot.features.dashboard.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ejespot.R
import com.example.ejespot.core.data.SpotStore
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.core.settings.AppLanguageController
import com.example.ejespot.core.util.ResourceProviderHolder
import com.example.ejespot.domain.model.AppLanguage
import com.example.ejespot.domain.model.ProfileActivity
import com.example.ejespot.domain.model.ProfileDraft
import com.example.ejespot.domain.model.ProposalStatus
import com.example.ejespot.domain.model.ProposedSpot
import com.example.ejespot.domain.model.UserProfile
import com.example.ejespot.domain.model.UserStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(appliaation: Application) : AndroidViewModel(appliaation) {

    val availableLocations = listOf(
        "Pereira, Risaralda",
        "Salento, Quindío",
        "Manizales, Caldas",
        "Filandia, Quindío",
        "Santa Rosa de Cabal, Risaralda",
        "Armenia, Quindío",
        "Calarcá, Quindío"
    )

    private val currentProfileId: String get() = SessionManager.profileId

    /** Textos de `strings.xml` para el contenido quemado de la demo (guía 14). */
    private val resources get() = ResourceProviderHolder.resources

    private fun currentProfile(): UserProfile = ProfileSampleData.profileFor(currentProfileId)

    private fun currentProposals() = ProfileSampleData.proposalsFor(currentProfileId)

    private fun currentReviews() = ProfileSampleData.reviewsFor(currentProfileId)

    /**
     * Idioma activo según la preferencia guardada (guía 13).
     * Si no hay ninguna, se muestra el primero de la lista (español).
     */
    private fun currentLanguage(): AppLanguage {
        val tag = AppLanguageController.languageTag
        return ProfileSampleData.languages.firstOrNull { it.code.startsWith(tag) }
            ?: ProfileSampleData.languages.first()
    }

    /** Estadísticas reales: los visitados salen del store, no de números quemados. */
    private fun currentStats(): UserStats {
        val base = ProfileSampleData.baseStatsByUser[currentProfileId]
            ?: UserStats(visited = 0, reviews = 0, proposals = 0)
        return base.copy(
            visited = SpotStore.visitedSpotIds.value.size,
            proposals = currentProposals().size
        )
    }

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            profile = currentProfile(),
            stats = currentStats(),
            levels = ProfileSampleData.levels,
            activities = ProfileSampleData.activities,
            badges = ProfileSampleData.badges,
            notifications = ProfileSampleData.notifications,
            reviews = currentReviews(),
            proposals = currentProposals(),
            settings = ProfileSampleData.settings,
            permissions = ProfileSampleData.permissions,
            selectedLanguage = currentLanguage(),
            moderationQueue = ProfileSampleData.moderationQueue,
            draft = currentProfile().toDraft(),
            isModerator = SessionManager.role == com.example.ejespot.domain.model.UserRole.ADMIN
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // Mantiene el selector de idioma al día con la preferencia guardada.
        viewModelScope.launch {
            AppLanguageController.languageTagFlow.collect { tag ->
                val language = ProfileSampleData.languages.firstOrNull { it.code.startsWith(tag) }
                    ?: ProfileSampleData.languages.first()
                _uiState.update { it.copy(selectedLanguage = language) }
            }
        }

        // Mantiene visitados, reseñas y actividades sincronizados con el store compartido.
        viewModelScope.launch {
            SpotStore.visitedSpotIds.collect { visited ->
                _uiState.update { state ->
                    val base = ProfileSampleData.baseStatsByUser[currentProfileId]
                        ?: UserStats(visited = 0, reviews = 0, proposals = 0)
                    val liveReviews = liveReviewsFromStore()
                    state.copy(
                        stats = base.copy(
                            visited = visited.size,
                            reviews = liveReviews.size,
                            proposals = currentProposals().size
                        ),
                        reviews = liveReviews,
                        activities = state.activities.map { activity ->
                            when (activity.id) {
                                "visited" -> activity.copy(valueLabel = "${visited.size}")
                                "reviews" -> activity.copy(valueLabel = "${liveReviews.size}")
                                "proposals" -> activity.copy(valueLabel = "${currentProposals().size}")
                                else -> activity
                            }
                        }
                    )
                }
            }
        }
    }

    /**
     * Combina las reseñas quemadas del perfil con las que el usuario escribió
     * en esta sesión (identificadas por su `authorProfileId` en el store).
     */
    private fun liveReviewsFromStore(): List<com.example.ejespot.domain.model.MyReview> {
        val seeded = ProfileSampleData.reviewsFor(currentProfileId)
        val seededIds = seeded.mapNotNull { it.spotId }.toSet()

        val sessionReviews = SpotStore.allSpots()
            .flatMap { spot -> spot.reviews.map { spot to it } }
            .filter { (_, review) -> review.authorProfileId == currentProfileId }
            .filter { (spot, review) -> spot.id !in seededIds || review.id.startsWith("rev_1") }
            .map { (spot, review) ->
                com.example.ejespot.domain.model.MyReview(
                    id = review.id,
                    spotId = spot.id,
                    spotName = spot.name,
                    category = spot.municipality,
                    dateLabel = review.date,
                    // Las reseñas de la demo no traen fecha: se usa "Hace 2 días" (traducido).
                    dateLabelRes = review.dateRes ?: R.string.time_ago_2_days,
                    rating = review.rating.toDouble(),
                    comment = review.comment,
                    commentRes = review.commentRes,
                    helpfulCount = 0,
                    xpEarned = 25
                )
            }

        // Evita duplicados por id y mantiene primero lo más reciente del store.
        val byId = LinkedHashMap<String, com.example.ejespot.domain.model.MyReview>()
        sessionReviews.forEach { byId[it.id] = it }
        seeded.forEach { byId.putIfAbsent(it.id, it) }
        return byId.values.toList().distinctBy { (it.commentRes?.toString() ?: it.comment) + it.spotName }
    }

    /** Reconstruye el estado cuando cambia el usuario de la sesión. */
    fun reloadForCurrentUser() {
        val profile = currentProfile()
        val liveReviews = liveReviewsFromStore()
        _uiState.update {
            it.copy(
                profile = profile,
                stats = currentStats().copy(reviews = liveReviews.size),
                reviews = liveReviews,
                proposals = currentProposals(),
                draft = profile.toDraft(),
                draftError = null,
                isModerator = SessionManager.role == com.example.ejespot.domain.model.UserRole.ADMIN
            )
        }
    }

    // ---------------------------------------------------------------------
    // Editar perfil
    // ---------------------------------------------------------------------

    fun startEditing() {
        _uiState.update {
            it.copy(draft = it.profile.toDraft(), draftError = null)
        }
    }

    fun onDraftNameChanged(value: String) {
        _uiState.update { it.copy(draft = it.draft.copy(name = value), draftError = null) }
    }

    fun onDraftUsernameChanged(value: String) {
        _uiState.update { it.copy(draft = it.draft.copy(username = value), draftError = null) }
    }

    fun onDraftLocationChanged(value: String) {
        _uiState.update { it.copy(draft = it.draft.copy(location = value), draftError = null) }
    }

    fun onDraftBioChanged(value: String) {
        _uiState.update {
            it.copy(draft = it.draft.copy(bio = value.take(160)), draftError = null)
        }
    }

    /**
     * Valida y guarda los cambios del perfil en memoria.
     * @return true si el perfil fue actualizado correctamente.
     */
    fun saveProfile(): Boolean {
        val current = _uiState.value
        val name = current.draft.name.trim()
        val location = current.draft.location.trim()
        var username = current.draft.username.trim()

        val error = when {
            name.isBlank() -> "El nombre completo es obligatorio."
            name.length < 3 -> "El nombre debe tener al menos 3 caracteres."
            username.isBlank() -> "El nombre de usuario es obligatorio."
            else -> null
        }

        if (error != null) {
            _uiState.update { it.copy(draftError = error) }
            return false
        }

        if (!username.startsWith("@")) username = "@$username"
        if (username.length < 4) {
            _uiState.update { it.copy(draftError = "El nombre de usuario debe tener al menos 3 caracteres.") }
            return false
        }
        if (location.isBlank()) {
            _uiState.update { it.copy(draftError = "Seleaaiona una ubicación.") }
            return false
        }

        val updated = current.profile.copy(
            name = name,
            username = username,
            location = location,
            bio = current.draft.bio.trim(),
            // La bio editada por el usuario es texto plano: deja de usar el recurso.
            bioRes = null,
            initials = name.toInitials()
        )

        _uiState.update {
            it.copy(
                profile = updated,
                draft = updated.toDraft(),
                draftError = null
            )
        }
        return true
    }

    // ---------------------------------------------------------------------
    // Notificaciones
    // ---------------------------------------------------------------------

    fun markNotificationRead(id: String) {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { notification ->
                    if (notification.id == id) notification.copy(isRead = true) else notification
                }
            )
        }
    }

    fun markAllNotificationsRead() {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { it.copy(isRead = true) }
            )
        }
    }

    // ---------------------------------------------------------------------
    // Configuración
    // ---------------------------------------------------------------------

    fun toggleNearbyNotifications() {
        _uiState.update {
            it.copy(settings = it.settings.copy(nearbyNotifications = !it.settings.nearbyNotifications))
        }
    }

    fun toggleProposalNotifications() {
        _uiState.update {
            it.copy(settings = it.settings.copy(proposalNotifications = !it.settings.proposalNotifications))
        }
    }

    fun toggleRewardNotifications() {
        _uiState.update {
            it.copy(settings = it.settings.copy(rewardNotifications = !it.settings.rewardNotifications))
        }
    }

    fun toggleDarkMode() {
        _uiState.update {
            it.copy(settings = it.settings.copy(darkMode = !it.settings.darkMode))
        }
        // El tema raíz lee este mismo valor, así el interruptor sí cambia la app.
        com.example.ejespot.core.session.AppearanceSettings.darkMode = _uiState.value.settings.darkMode
    }

    // ---------------------------------------------------------------------
    // Idioma
    // ---------------------------------------------------------------------

    /**
     * Cambia el idioma de la app.
     *
     * El idioma se aplica al instante y además se guarda en Data Store,
     * para que la app arranque en ese idioma la próxima vez (guías 13 y 14).
     */
    fun selectLanguage(language: AppLanguage) {
        _uiState.update { it.copy(selectedLanguage = language) }
        AppLanguageController.setLanguage(getApplication(), language.code)
    }

    // ---------------------------------------------------------------------
    // Permisos
    // ---------------------------------------------------------------------

    fun togglePermission(id: String) {
        _uiState.update { state ->
            state.copy(
                permissions = state.permissions.map { permission ->
                    if (permission.id == id) {
                        permission.copy(isGranted = !permission.isGranted)
                    } else {
                        permission
                    }
                }
            )
        }
    }

    // ---------------------------------------------------------------------
    // Reseñas y propuestas
    // ---------------------------------------------------------------------

    fun toggleReviewHelpful(reviewId: String) {
        _uiState.update { state ->
            state.copy(
                reviews = state.reviews.map { review ->
                    if (review.id == reviewId) {
                        review.copy(
                            isMarkedHelpful = !review.isMarkedHelpful,
                            helpfulCount = if (review.isMarkedHelpful) {
                                (review.helpfulCount - 1).coerceAtLeast(0)
                            } else {
                                review.helpfulCount + 1
                            }
                        )
                    } else {
                        review
                    }
                }
            )
        }
    }

    // ---------------------------------------------------------------------
    // Moderación (rol especial de Embajador Local)
    // ---------------------------------------------------------------------

    fun resolveModerationReport(id: String) {
        _uiState.update { state ->
            state.copy(moderationQueue = state.moderationQueue.filterNot { it.id == id })
        }
    }

    fun approveProposal(proposalId: String) {
        _uiState.update { state ->
            state.copy(
                proposals = state.proposals.map {
                    if (it.id == proposalId) it.copy(status = ProposalStatus.APPROVED, xpEarned = 100) else it
                }
            )
        }
    }

    fun rejectProposal(proposalId: String) {
        _uiState.update { state ->
            state.copy(
                proposals = state.proposals.filterNot { it.id == proposalId }
            )
        }
    }

    fun editProposal(proposalId: String, newName: String, newLocation: String, newNote: String) {
        _uiState.update { state ->
            val updated = state.proposals.map { p ->
                if (p.id == proposalId) {
                    p.copy(
                        name = newName.trim(),
                        location = newLocation.trim(),
                        customNote = newNote.trim(),
                        status = ProposalStatus.PENDING
                    )
                } else p
            }
            state.copy(proposals = updated)
        }
    }

    fun deleteProposal(proposalId: String) {
        _uiState.update { state ->
            val updated = state.proposals.filterNot { it.id == proposalId }
            state.copy(
                proposals = updated,
                stats = state.stats.copy(proposals = updated.size),
                activities = state.activities.map { activity ->
                    if (activity.id == "proposals") activity.copy(valueLabel = "${updated.size}") else activity
                }
            )
        }
    }

    fun addProposalFromCreatedSpot(name: String, location: String, note: String, spotId: String) {
        _uiState.update { state ->
            val newProposal = ProposedSpot(
                id = "prop_${System.currentTimeMillis()}",
                name = name,
                location = location,
                customSubmittedLabel = "Hoy",
                status = ProposalStatus.PENDING,
                xpEarned = 0,
                customNote = note,
                spotId = spotId
            )
            val updated: List<ProposedSpot> = listOf(newProposal) + state.proposals
            state.copy(
                proposals = updated,
                stats = state.stats.copy(proposals = updated.size),
                activities = state.activities.map { activity ->
                    if (activity.id == "proposals") activity.copy(valueLabel = "${updated.size}") else activity
                }
            )
        }
    }

    private fun UserProfile.toDraft() = ProfileDraft(
        name = name,
        username = username,
        location = location,
        bio = bioRes?.let { resources.getString(it) } ?: bio
    )

    private fun String.toInitials(): String {
        val words = trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        return when {
            words.isEmpty() -> "US"
            words.size == 1 -> words.first().take(2).uppercase()
            else -> (words[0].take(1) + words[1].take(1)).uppercase()
        }
    }
}
