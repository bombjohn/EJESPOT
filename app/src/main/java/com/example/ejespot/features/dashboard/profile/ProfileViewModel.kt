package com.example.ejespot.features.dashboard.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ejespot.R
import com.example.ejespot.core.data.SpotStore
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.core.settings.AppLanguageController
import com.example.ejespot.core.util.ResourceProviderHolder
import com.example.ejespot.data.repository.local.LocalProfileRepository
import com.example.ejespot.data.repository.local.LocalSpotRepository
import com.example.ejespot.domain.model.AppLanguage
import com.example.ejespot.domain.model.ModerationReport
import com.example.ejespot.domain.model.MyReview
import com.example.ejespot.domain.model.ProfileActivity
import com.example.ejespot.domain.model.ProfileDraft
import com.example.ejespot.domain.model.ProposalStatus
import com.example.ejespot.domain.model.ProposedSpot
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.UserProfile
import com.example.ejespot.domain.model.UserStats
import com.example.ejespot.domain.repository.ProfileRepository
import com.example.ejespot.domain.repository.SpotRepository
import com.example.ejespot.domain.usecase.ApproveProposalUseCase
import com.example.ejespot.domain.usecase.DeleteAccountUseCase
import com.example.ejespot.domain.usecase.RejectProposalUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel @JvmOverloads constructor(
    application: Application,
    private val spotRepository: SpotRepository = LocalSpotRepository(),
    private val profileRepository: ProfileRepository = LocalProfileRepository(),
    private val approveProposalUseCase: ApproveProposalUseCase = ApproveProposalUseCase(spotRepository),
    private val rejectProposalUseCase: RejectProposalUseCase = RejectProposalUseCase(spotRepository),
    private val deleteAccountUseCase: DeleteAccountUseCase = DeleteAccountUseCase(spotRepository, profileRepository)
) : AndroidViewModel(application) {

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

    private fun currentProfile(): UserProfile = profileRepository.getProfile(currentProfileId)

    /**
     * Construye las propuestas reales del usuario consultando el SpotStore
     * (spots pendientes y aprobados) y las notificaciones de moderación (rechazos).
     */
    private fun getUserProposalsFromStore(): List<ProposedSpot> {
        val allSpots = spotRepository.allSpots()
        val pendingSpots = spotRepository.pendingSpots.value
        // Los spots propuestos en esta sesión quedan registrados en el perfil.
        // Se incluyen aunque el tag `addedBy` del spot no coincida con el
        // usuario (por ejemplo, sesiones restauradas desde Data Store), para
        // que un punto recién publicado nunca aparezca como lista vacía.
        val recordedSpotIds = profileRepository.getProposals(currentProfileId)
            .mapNotNull { it.spotId }
            .toSet()
        val userSpots = (pendingSpots + allSpots)
            .filter { isSpotOwnedByCurrentUser(it) || it.id in recordedSpotIds }
            .distinctBy { it.id }

        val currentUserId = SessionManager.currentUser?.id ?: ""
        val rejectNotifs = SpotStore.getNotificationsForUser(currentUserId).filter {
            it.type == com.example.ejespot.domain.model.NotificationType.REJECTION
        }

        val result = mutableListOf<ProposedSpot>()

        for (spot in userSpots) {
            val rejectNotif = rejectNotifs.firstOrNull {
                it.relatedSpotId == spot.id || it.message.contains(spot.name, ignoreCase = true)
            }
            val status = when {
                rejectNotif != null -> ProposalStatus.REJECTED
                spot.isPending -> ProposalStatus.PENDING
                else -> ProposalStatus.APPROVED
            }
            val reason = rejectNotif?.message?.substringAfter("Motivo: ")?.trim()

            result.add(
                ProposedSpot(
                    id = spot.id,
                    name = spot.name,
                    location = spot.municipality,
                    submittedLabelRes = R.string.time_today,
                    status = status,
                    xpEarned = if (status == ProposalStatus.APPROVED) 100 else 0,
                    customNote = spot.description.ifBlank { null },
                    spotId = spot.id,
                    rejectionReason = reason
                )
            )
        }

        // Incluir también propuestas rechazadas cuyo spot ya no exista en la lista activa
        for (notif in rejectNotifs) {
            val sid = notif.relatedSpotId
            val already = result.any {
                it.spotId == sid || (sid != null && it.id == sid) ||
                        (it.name.isNotBlank() && notif.message.contains(it.name, ignoreCase = true))
            }
            if (!already) {
                val spotName = notif.message.substringAfter("Tu propuesta '").substringBefore("'")
                    .ifBlank { notif.message.substringAfter("'").substringBefore("'") }
                    .ifBlank { "Lugar propuesto" }
                val reason = notif.message.substringAfter("Motivo: ").trim()
                    .ifBlank { "No cumple con las pautas comunitarias" }
                result.add(
                    ProposedSpot(
                        id = sid ?: notif.id,
                        name = spotName,
                        location = "Eje Cafetero",
                        submittedLabelRes = R.string.time_today,
                        status = ProposalStatus.REJECTED,
                        xpEarned = 0,
                        customNote = null,
                        spotId = sid,
                        rejectionReason = reason
                    )
                )
            }
        }

        return result
    }

    private fun currentProposals(): List<ProposedSpot> {
        val seeded = profileRepository.getProposals(currentProfileId)
        val fromStore = getUserProposalsFromStore()
        val map = LinkedHashMap<String, ProposedSpot>()

        // Lo que el usuario creó/editó en su perfil es la fuente principal:
        // conserva nombre, ubicación y nota aunque el store siga con datos
        // antiguos. Por eso los flujos de sincronización no "borran" las
        // propuestas recién publicadas.
        seeded.forEach { prop ->
            val key = prop.spotId ?: prop.id
            map[key] = prop
        }

        fromStore.forEach { prop ->
            val key = prop.spotId ?: prop.id
            val existing = map[key]
            if (existing == null) {
                map[key] = prop
            } else if (existing.status == ProposalStatus.PENDING &&
                prop.status != ProposalStatus.PENDING
            ) {
                // El store sí refleja una decisión de moderación posterior.
                map[key] = existing.copy(
                    status = prop.status,
                    rejectionReason = prop.rejectionReason,
                    xpEarned = prop.xpEarned
                )
            }
        }
        return map.values.toList()
    }

    private fun currentReviews() = profileRepository.getReviews(currentProfileId)

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
            moderationProposals = spotRepository.pendingSpots.value.map { it.toProposal() },
            draft = currentProfile().toDraft(),
            isModerator = SessionManager.role == com.example.ejespot.domain.model.UserRole.ADMIN,
            activeSpotsCount = spotRepository.allSpots().count { it.addedBy.contains(currentProfileId, ignoreCase = true) && !it.isPending && !it.isFinalized }.coerceAtLeast(if (currentProfileId == "camila") 2 else 1),
            pendingSpotsCount = currentProposals().count { it.status == ProposalStatus.PENDING },
            finalizedSpotsCount = spotRepository.allSpots().count { it.addedBy.contains(currentProfileId, ignoreCase = true) && it.isFinalized }
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

        // Mantiene métricas y spots sincronizados con el repositorio compartido.
        viewModelScope.launch {
            spotRepository.spots.collect { spots ->
                _uiState.update { state ->
                    val userProposals = currentProposals()
                    // Toda reseña nueva se guarda dentro de un spot, por lo que
                    // este flujo es el que debe recalcular también las reseñas
                    // del usuario; si no, la lista del perfil se queda vieja.
                    val liveReviews = liveReviewsFromStore()
                    state.copy(
                        proposals = userProposals,
                        reviews = liveReviews,
                        activeSpotsCount = spots.count { isSpotOwnedByCurrentUser(it) && !it.isPending && !it.isFinalized }.coerceAtLeast(if (currentProfileId == "camila") 2 else 0),
                        pendingSpotsCount = userProposals.count { it.status == ProposalStatus.PENDING },
                        finalizedSpotsCount = spots.count { isSpotOwnedByCurrentUser(it) && it.isFinalized },
                        stats = state.stats.copy(
                            reviews = liveReviews.size,
                            proposals = userProposals.size
                        ),
                        activities = state.activities.map { activity ->
                            when (activity.id) {
                                "reviews" -> activity.copy(valueLabel = "${liveReviews.size}")
                                "proposals" -> activity.copy(valueLabel = "${userProposals.size}")
                                else -> activity
                            }
                        }
                    )
                }
            }
        }

        // Mantiene visitados, reseñas y actividades sincronizados con el store compartido.
        viewModelScope.launch {
            SpotStore.visitedSpotIds.collect { visited ->
                _uiState.update { state ->
                    val base = ProfileSampleData.baseStatsByUser[currentProfileId]
                        ?: UserStats(visited = 0, reviews = 0, proposals = 0)
                    val liveReviews = liveReviewsFromStore()
                    val userProposals = currentProposals()
                    state.copy(
                        stats = base.copy(
                            visited = visited.size,
                            reviews = liveReviews.size,
                            proposals = userProposals.size
                        ),
                        reviews = liveReviews,
                        proposals = userProposals,
                        activities = state.activities.map { activity ->
                            when (activity.id) {
                                "visited" -> activity.copy(valueLabel = "${visited.size}")
                                "reviews" -> activity.copy(valueLabel = "${liveReviews.size}")
                                "proposals" -> activity.copy(valueLabel = "${userProposals.size}")
                                else -> activity
                            }
                        }
                    )
                }
            }
        }

        // Cola global de moderación: cualquier spot pendiente del store (de
        // cualquier usuario) aparece como propuesta por revisar. También
        // mantiene al día las propuestas propias con lo que ocurre en el
        // store (aprobada, rechazada o creada en otra sesión).
        viewModelScope.launch {
            spotRepository.pendingSpots.collect { pending ->
                _uiState.update { state ->
                    val refreshedProposals = currentProposals()
                    state.copy(
                        moderationProposals = pending.map { it.toProposal() },
                        proposals = refreshedProposals,
                        stats = state.stats.copy(proposals = refreshedProposals.size),
                        pendingSpotsCount = refreshedProposals.count { it.status == ProposalStatus.PENDING },
                        activities = state.activities.map { activity ->
                            if (activity.id == "proposals") {
                                activity.copy(valueLabel = "${refreshedProposals.size}")
                            } else {
                                activity
                            }
                        }
                    )
                }
            }
        }

        // Sincroniza propuestas ante notificaciones (aprobaciones o motivos de rechazo).
        viewModelScope.launch {
            SpotStore.notifications.collect {
                _uiState.update { state ->
                    val refreshedProposals = currentProposals()
                    state.copy(
                        proposals = refreshedProposals,
                        stats = state.stats.copy(proposals = refreshedProposals.size),
                        pendingSpotsCount = refreshedProposals.count { it.status == ProposalStatus.PENDING },
                        activities = state.activities.map { activity ->
                            if (activity.id == "proposals") {
                                activity.copy(valueLabel = "${refreshedProposals.size}")
                            } else {
                                activity
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

        val user = SessionManager.currentUser
        val userHandle = if (user != null) "@${SessionManager.displayName.replace(" ", "_").lowercase()}" else ""
        val userEmailPrefix = user?.email?.substringBefore("@")?.lowercase() ?: ""

        fun isReviewByCurrentUser(review: com.example.ejespot.domain.model.SpotReview): Boolean {
            val authorId = review.authorProfileId
            // Las reseñas del invitado no tienen authorProfileId.
            if (authorId == null) return SessionManager.isGuest
            return authorId == currentProfileId ||
                    (user != null && (
                        authorId.equals(user.id, ignoreCase = true) ||
                        authorId.equals(user.email, ignoreCase = true) ||
                        authorId.equals(userEmailPrefix, ignoreCase = true) ||
                        authorId.equals(userHandle.removePrefix("@"), ignoreCase = true)
                    ))
        }

        val sessionReviews = SpotStore.allSpots()
            .flatMap { spot -> spot.reviews.map { spot to it } }
            .filter { (_, review) -> isReviewByCurrentUser(review) }
            .map { (spot, review) ->
                com.example.ejespot.domain.model.MyReview(
                    id = review.id,
                    spotId = spot.id,
                    spotName = spot.name,
                    category = spot.municipality,
                    dateLabel = review.date,
                    dateLabelRes = review.dateRes
                        ?: if (review.date.isBlank()) R.string.time_today else R.string.time_ago_2_days,
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
        val userProposals = currentProposals()
        _uiState.update {
            it.copy(
                profile = profile,
                stats = currentStats().copy(reviews = liveReviews.size, proposals = userProposals.size),
                reviews = liveReviews,
                proposals = userProposals,
                moderationProposals = spotRepository.pendingSpots.value.map { it.toProposal() },
                draft = profile.toDraft(),
                draftError = null,
                isModerator = SessionManager.role == com.example.ejespot.domain.model.UserRole.ADMIN,
                pendingSpotsCount = userProposals.count { it.status == ProposalStatus.PENDING },
                activities = it.activities.map { activity ->
                    when (activity.id) {
                        "reviews" -> activity.copy(valueLabel = "${liveReviews.size}")
                        "proposals" -> activity.copy(valueLabel = "${userProposals.size}")
                        else -> activity
                    }
                }
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

        ProfileSampleData.registerProfile(
            profileId = currentProfileId,
            name = name,
            username = username,
            location = location,
            bio = updated.bio
        )
        // Mantener la sesión alineada: de lo contrario los spots y reseñas
        // nuevos se etiquetan con el nombre antiguo.
        SessionManager.updateProfileName(name)

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

    /**
     * Etiqueta `@usuario` con la que [com.example.ejespot.core.data.SpotStore]
     * registra los spots creados desde "Crear lugar".
     */
    private fun currentUserSpotTag(): String =
        if (SessionManager.isGuest) {
            "@invitado"
        } else {
            "@${SessionManager.displayName.replace(" ", "_").lowercase()}"
        }

    /**
     * Verifica si un spot fue creado/propuesto por el usuario con sesión activa.
     */
    private fun isSpotOwnedByCurrentUser(spot: Spot): Boolean {
        if (SessionManager.isGuest) {
            return spot.addedBy.equals("@invitado", ignoreCase = true) ||
                    spot.addedBy.equals("invitado", ignoreCase = true)
        }
        val user = SessionManager.currentUser ?: return false
        val userHandle = "@${SessionManager.displayName.replace(" ", "_").lowercase()}"
        val userEmailHandle = "@${user.email.substringBefore("@").lowercase()}"
        val cleanAddedBy = spot.addedBy.removePrefix("@").trim()

        val isDirectMatch = spot.addedBy.equals(userHandle, ignoreCase = true) ||
                spot.addedBy.equals(userEmailHandle, ignoreCase = true) ||
                spot.addedBy.equals(user.email, ignoreCase = true) ||
                spot.addedBy.equals(user.id, ignoreCase = true) ||
                spot.addedBy.equals(user.profileId, ignoreCase = true) ||
                spot.addedBy.equals("@tu", ignoreCase = true) ||
                spot.addedBy.equals("tú", ignoreCase = true) ||
                cleanAddedBy.equals(SessionManager.displayName.replace(" ", "_"), ignoreCase = true) ||
                cleanAddedBy.equals(user.name.replace(" ", "_"), ignoreCase = true) ||
                cleanAddedBy.equals(user.email.substringBefore("@"), ignoreCase = true) ||
                cleanAddedBy.equals(user.id, ignoreCase = true) ||
                cleanAddedBy.equals(user.profileId, ignoreCase = true) ||
                cleanAddedBy.equals(currentProfileId, ignoreCase = true)

        if (isDirectMatch) return true

        // Coincidencia para variantes de nombres o emails (ej. "John Cardona", "johncpapaz")
        val johnInUser = user.name.contains("john", ignoreCase = true) ||
                user.email.contains("john", ignoreCase = true)
        if (johnInUser && cleanAddedBy.contains("john", ignoreCase = true)) {
            return true
        }

        return false
    }

    /** Convierte un spot pendiente del store en propuesta para el panel. */
    private fun Spot.toProposal(): ProposedSpot = ProposedSpot(
        id = id,
        name = name,
        location = municipality,
        submittedLabelRes = R.string.time_today,
        status = ProposalStatus.PENDING,
        xpEarned = 0,
        customNote = description.ifBlank { null },
        spotId = id
    )

    /**
     * Aprueba una propuesta de la cola global de moderación. El spot pasa a
     * la lista oficial y el autor recibe la notificación (+100 XP).
     */
    fun approvePendingSpotById(spotId: String) {
        approveProposalUseCase(spotId)
        ProfileSampleData.updateProposalStatus(spotId, ProposalStatus.APPROVED)
        _uiState.update { state ->
            val updated = state.proposals.map {
                if (it.spotId == spotId || it.id == spotId) {
                    it.copy(status = ProposalStatus.APPROVED, xpEarned = 100)
                } else it
            }
            state.copy(
                proposals = updated,
                pendingSpotsCount = updated.count { it.status == ProposalStatus.PENDING }
            )
        }
    }

    /**
     * Rechaza una propuesta de la cola global de moderación con el motivo
     * indicado por el moderador.
     */
    fun rejectPendingSpotById(spotId: String, reason: String) {
        rejectProposalUseCase(spotId, reason)
        ProfileSampleData.updateProposalStatus(spotId, ProposalStatus.REJECTED, reason)
        _uiState.update { state ->
            val updated = state.proposals.map {
                if (it.spotId == spotId || it.id == spotId) {
                    it.copy(status = ProposalStatus.REJECTED, rejectionReason = reason)
                } else it
            }
            state.copy(
                proposals = updated,
                pendingSpotsCount = updated.count { it.status == ProposalStatus.PENDING }
            )
        }
    }

    fun approveProposal(proposalId: String) {
        val target = _uiState.value.proposals.firstOrNull { it.id == proposalId }
        val spotId = target?.spotId
        if (spotId != null) {
            approveProposalUseCase(spotId)
        }
        _uiState.update { state ->
            state.copy(
                proposals = state.proposals.map {
                    if (it.id == proposalId) it.copy(status = ProposalStatus.APPROVED, xpEarned = 100) else it
                }
            )
        }
    }

    fun rejectProposal(proposalId: String, reason: String = "No cumple con las pautas de contenido") {
        val target = _uiState.value.proposals.firstOrNull { it.id == proposalId }
        val spotId = target?.spotId
        if (spotId != null) {
            rejectProposalUseCase(spotId, reason)
        }
        _uiState.update { state ->
            state.copy(
                proposals = state.proposals.map {
                    if (it.id == proposalId) it.copy(status = ProposalStatus.REJECTED, rejectionReason = reason) else it
                }
            )
        }
    }

    fun addModerationReport(report: ModerationReport) {
        _uiState.update { state ->
            state.copy(moderationQueue = listOf(report) + state.moderationQueue)
        }
    }

    fun deleteAccount(onFinished: () -> Unit) {
        val pid = currentProfileId
        viewModelScope.launch {
            deleteAccountUseCase(pid, onFinished)
        }
    }

    fun editProposal(proposalId: String, newName: String, newLocation: String, newNote: String) {
        val target = _uiState.value.proposals.firstOrNull { it.id == proposalId } ?: return
        val edited = target.copy(
            name = newName.trim(),
            location = newLocation.trim(),
            customNote = newNote.trim(),
            status = ProposalStatus.PENDING
        )
        ProfileSampleData.updateProposal(currentProfileId, edited)
        ProfileSampleData.updateProposal(currentUserSpotTag(), edited)
        // Propagar el cambio al spot compartido para que el feed y Firestore
        // dejen de mostrar el nombre y la nota antiguos.
        target.spotId?.let { id ->
            spotRepository.getSpotById(id)?.let { spot ->
                spotRepository.updateSpot(
                    id = id,
                    name = edited.name,
                    category = spot.category,
                    municipality = edited.location,
                    hours = spot.hours,
                    priceRange = spot.priceRange,
                    description = edited.customNote.orEmpty()
                )
            }
        }
        _uiState.update { state ->
            val updated = state.proposals.map { if (it.id == proposalId) edited else it }
            state.copy(
                proposals = updated,
                moderationProposals = state.moderationProposals.map {
                    if (it.id == proposalId || it.spotId == target.spotId) edited else it
                }
            )
        }
    }

    fun deleteProposal(proposalId: String) {
        val target = _uiState.value.proposals.firstOrNull { it.id == proposalId }
        ProfileSampleData.removeProposal(currentProfileId, proposalId)
        ProfileSampleData.removeProposal(currentUserSpotTag(), proposalId)
        target?.spotId?.let { spotRepository.deleteSpot(it) }
        _uiState.update { state ->
            val updated = state.proposals.filterNot {
                it.id == proposalId || (target?.spotId != null && it.spotId == target.spotId)
            }
            val updatedModeration = state.moderationProposals.filterNot {
                it.id == proposalId || (target?.spotId != null && it.spotId == target.spotId)
            }
            state.copy(
                proposals = updated,
                moderationProposals = updatedModeration,
                pendingSpotsCount = updated.count { it.status == ProposalStatus.PENDING },
                stats = state.stats.copy(proposals = updated.size),
                activities = state.activities.map { activity ->
                    if (activity.id == "proposals") activity.copy(valueLabel = "${updated.size}") else activity
                }
            )
        }
    }

    fun addProposalFromCreatedSpot(name: String, location: String, note: String, spotId: String) {
        val newProposal = ProposedSpot(
            id = spotId,
            name = name,
            location = location,
            customSubmittedLabel = "Hoy",
            status = ProposalStatus.PENDING,
            xpEarned = 0,
            customNote = note,
            spotId = spotId
        )
        ProfileSampleData.addProposal(currentProfileId, newProposal)
        val tag = currentUserSpotTag()
        ProfileSampleData.addProposal(tag, newProposal)

        _uiState.update { state ->
            val updated = (listOf(newProposal) + state.proposals).distinctBy { it.spotId ?: it.id }
            val updatedModeration = (listOf(newProposal) + state.moderationProposals).distinctBy { it.spotId ?: it.id }
            state.copy(
                proposals = updated,
                moderationProposals = updatedModeration,
                stats = state.stats.copy(proposals = updated.size),
                pendingSpotsCount = updated.count { it.status == ProposalStatus.PENDING },
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
