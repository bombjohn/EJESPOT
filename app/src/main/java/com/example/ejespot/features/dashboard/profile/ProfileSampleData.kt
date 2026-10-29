package com.example.ejespot.features.dashboard.profile

import com.example.ejespot.R
import com.example.ejespot.domain.model.*

/**
 * Datos quemados de la demo (Fase 2).
 *
 * Se comparten entre todas las pantallas del módulo de Perfil para mantener
 * consistencia.
 *
 * El contenido visible al usuario se guarda como recursos de `strings.xml`
 * para que también exista en inglés (guía 14). Los identificadores de datos
 * (nivel de comunidad, categoría, nombre del lugar, alias de usuario) se
 * mantienen en texto plano porque no se traducen.
 */
object ProfileSampleData {

    /** Perfil de la cuenta principal de la demo. */
    val profile = UserProfile(
        name = "Renata Castaño",
        username = "@renacastano",
        location = "Pereira, Risaralda",
        bio = "",
        bioRes = R.string.profile_bio_renata,
        initials = "RC",
        level = 4,
        levelName = ProfileLevels.EXPLORADOR_CAFETERO,
        xp = 820,
        nextLevelXp = 1200,
        nextLevelName = ProfileLevels.EMBAJADOR_LOCAL,
        memberSince = "",
        memberSinceRes = R.string.profile_member_since_renata
    )

    /**
     * Perfiles quemados por cuenta demo.
     * La llave es el `profileId` de [com.example.ejespot.core.session.SessionManager].
     */
    val profilesByUser: Map<String, UserProfile> = mapOf(
        "renata" to profile,
        "mateo" to UserProfile(
            name = "Mateo Salazar",
            username = "@mateo_modera",
            location = "Manizales, Caldas",
            bio = "",
            bioRes = R.string.profile_bio_mateo,
            initials = "MS",
            level = 6,
            levelName = ProfileLevels.EMBAJADOR_LOCAL,
            xp = 2450,
            nextLevelXp = 3000,
            nextLevelName = ProfileLevels.LEYENDA_EJE,
            memberSince = "",
            memberSinceRes = R.string.profile_member_since_mateo
        ),
        "camila" to UserProfile(
            name = "Camila Herrera",
            username = "@camiviajera",
            location = "Salento, Quindío",
            bio = "",
            bioRes = R.string.profile_bio_camila,
            initials = "CH",
            level = 3,
            levelName = ProfileLevels.AVENTURERA,
            xp = 430,
            nextLevelXp = 500,
            nextLevelName = ProfileLevels.EXPLORADOR_CAFETERO,
            memberSince = "",
            memberSinceRes = R.string.profile_member_since_camila
        ),
        "invitado" to UserProfile(
            name = "Invitado EjeSpot",
            username = "@invitado",
            location = "Pereira, Risaralda",
            bio = "",
            bioRes = R.string.profile_bio_invitado,
            initials = "IE",
            level = 1,
            levelName = ProfileLevels.TURISTA,
            xp = 0,
            nextLevelXp = 200,
            nextLevelName = ProfileLevels.AVENTURERO,
            memberSince = "",
            memberSinceRes = R.string.profile_member_since_invitado
        )
    )

    private val dynamicProfiles = mutableMapOf<String, UserProfile>()

    fun registerProfile(profileId: String, name: String, username: String) {
        val initials = name.trim().split(Regex("\\s+")).take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
        dynamicProfiles[profileId] = UserProfile(
            name = name,
            username = if (username.startsWith("@")) username else "@$username",
            location = "Pereira, Risaralda",
            bio = "Nuevo viajero explorando el Paisaje Cultural Cafetero.",
            initials = if (initials.isNotEmpty()) initials else "US",
            level = 1,
            levelName = ProfileLevels.TURISTA,
            xp = 0,
            nextLevelXp = 200,
            nextLevelName = ProfileLevels.AVENTURERO,
            memberSince = "Hoy"
        )
    }

    /** Perfil correspondiente al identificador de sesión (o el invitado). */
    fun profileFor(profileId: String): UserProfile =
        dynamicProfiles[profileId] ?: profilesByUser[profileId] ?: profilesByUser.getValue("invitado")

    /** Propuestas quemadas por cuenta. */
    val proposalsByUser: Map<String, List<ProposedSpot>> get() = mapOf(
        "renata" to proposals,
        "camila" to proposals.take(2),
        "mateo" to emptyList(),
        "invitado" to emptyList()
    )

    fun proposalsFor(profileId: String): List<ProposedSpot> =
        proposalsByUser[profileId] ?: emptyList()

    /** Reseñas quemadas por cuenta. */
    val reviewsByUser: Map<String, List<MyReview>> get() = mapOf(
        "renata" to reviews,
        "camila" to reviews.take(4),
        "mateo" to reviews.filter { it.spotId == "3" || it.spotId == "4" },
        "invitado" to emptyList()
    )

    fun reviewsFor(profileId: String): List<MyReview> =
        reviewsByUser[profileId] ?: emptyList()

    /** Estadísticas iniciales por cuenta (los visitados reales se suman en el ViewModel). */
    val baseStatsByUser: Map<String, UserStats> = mapOf(
        "renata" to UserStats(visited = 0, reviews = 14, proposals = 3),
        "camila" to UserStats(visited = 0, reviews = 4, proposals = 2),
        "mateo" to UserStats(visited = 0, reviews = 2, proposals = 0),
        "invitado" to UserStats(visited = 0, reviews = 0, proposals = 0)
    )

    val stats = UserStats(visited = 23, reviews = 14, proposals = 3)

    val levels = listOf(
        CommunityLevel(ProfileLevels.TURISTA, R.string.profile_level_range_turista, LevelStatus.ACHIEVED, "🌱"),
        CommunityLevel(ProfileLevels.AVENTURERO, R.string.profile_level_range_aventurero, LevelStatus.ACHIEVED, "🥾"),
        CommunityLevel(ProfileLevels.EXPLORADOR_CAFETERO, R.string.profile_level_range_explorador, LevelStatus.CURRENT, "🧭"),
        CommunityLevel(ProfileLevels.EMBAJADOR_LOCAL, R.string.profile_level_range_embajador, LevelStatus.LOCKED, "👑")
    )

    val activities = listOf(
        ProfileActivity("visited", "🥾", R.string.profile_activity_visited, "23"),
        ProfileActivity("reviews", "✍️", R.string.profile_activity_reviews, "14"),
        ProfileActivity("proposals", "📍", R.string.profile_activity_proposals, "3"),
        ProfileActivity(
            "progress",
            "🏆",
            R.string.profile_activity_progress,
            valueLabel = "",
            valueLabelRes = R.string.profile_activity_progress_value
        )
    )

    val badges = listOf(
        AchievementBadge(
            id = "coffee",
            emoji = "☕",
            titleRes = R.string.profile_badge_coffee_title,
            descriptionRes = R.string.profile_badge_coffee_description,
            isUnlocked = true
        ),
        AchievementBadge(
            id = "hiker",
            emoji = "🥾",
            titleRes = R.string.profile_badge_hiker_title,
            descriptionRes = R.string.profile_badge_hiker_description,
            isUnlocked = true
        ),
        AchievementBadge(
            id = "photographer",
            emoji = "📷",
            titleRes = R.string.profile_badge_photographer_title,
            descriptionRes = R.string.profile_badge_photographer_description,
            isUnlocked = false,
            progressLabelRes = R.string.profile_badge_photographer_progress
        ),
        AchievementBadge(
            id = "ambassador",
            emoji = "👑",
            titleRes = R.string.profile_badge_ambassador_title,
            descriptionRes = R.string.profile_badge_ambassador_description,
            isUnlocked = false,
            progressLabelRes = R.string.profile_badge_ambassador_progress
        )
    )

    val notifications = listOf(
        ProfileNotification(
            id = "n1",
            titleRes = R.string.profile_notification_1_title,
            messageRes = R.string.profile_notification_1_message,
            timeLabelRes = R.string.time_ago_55_min,
            dayGroup = NotificationDayGroup.TODAY,
            category = NotificationCategory.XP
        ),
        ProfileNotification(
            id = "n2",
            titleRes = R.string.profile_notification_2_title,
            messageRes = R.string.profile_notification_2_message,
            timeLabelRes = R.string.time_ago_2_hours,
            dayGroup = NotificationDayGroup.TODAY,
            category = NotificationCategory.REVIEW
        ),
        ProfileNotification(
            id = "n3",
            titleRes = R.string.profile_notification_3_title,
            messageRes = R.string.profile_notification_3_message,
            timeLabelRes = R.string.time_ago_5_hours,
            dayGroup = NotificationDayGroup.TODAY,
            category = NotificationCategory.COMMENT
        ),
        ProfileNotification(
            id = "n4",
            titleRes = R.string.profile_notification_4_title,
            messageRes = R.string.profile_notification_4_message,
            timeLabelRes = R.string.time_yesterday_1022,
            dayGroup = NotificationDayGroup.YESTERDAY,
            category = NotificationCategory.LEVEL,
            isRead = true
        ),
        ProfileNotification(
            id = "n5",
            titleRes = R.string.profile_notification_5_title,
            messageRes = R.string.profile_notification_5_message,
            timeLabelRes = R.string.time_yesterday_0805,
            dayGroup = NotificationDayGroup.YESTERDAY,
            category = NotificationCategory.NEARBY,
            isRead = true
        ),
        ProfileNotification(
            id = "n6",
            titleRes = R.string.profile_notification_6_title,
            messageRes = R.string.profile_notification_6_message,
            timeLabelRes = R.string.time_yesterday_0740,
            dayGroup = NotificationDayGroup.YESTERDAY,
            category = NotificationCategory.REVIEW,
            isRead = true
        )
    )

    val reviews = listOf(
        MyReview(
            id = "r1",
            spotId = "1",
            spotName = "Mirador Valle de Cocora",
            category = "Salento, Quindío",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_2_days,
            rating = 5.0,
            comment = "",
            commentRes = R.string.review_comment_renata_1,
            helpfulCount = 12,
            xpEarned = 25
        ),
        MyReview(
            id = "r2",
            spotId = null,
            spotName = "Finca Cafetera El Ocaso",
            category = "Salento, Quindío",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_1_week,
            rating = 4.8,
            comment = "",
            commentRes = R.string.review_comment_r2,
            helpfulCount = 8,
            xpEarned = 25
        ),
        MyReview(
            id = "r3",
            spotId = null,
            spotName = "Termales Santa Rosa de Cabal",
            category = "Santa Rosa de Cabal, Risaralda",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_2_weeks,
            rating = 4.9,
            comment = "",
            commentRes = R.string.review_comment_9,
            helpfulCount = 15,
            xpEarned = 25
        ),
        MyReview(
            id = "r4",
            spotId = "2",
            spotName = "Simón Simón Campestre",
            category = "Pereira, Risaralda",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_3_weeks,
            rating = 5.0,
            comment = "",
            commentRes = R.string.review_comment_renata_2,
            helpfulCount = 9,
            xpEarned = 25
        ),
        MyReview(
            id = "r5",
            spotId = "3",
            spotName = "Plaza de Bolívar, Manizales",
            category = "Manizales, Caldas",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_1_month,
            rating = 4.7,
            comment = "",
            commentRes = R.string.review_comment_renata_3,
            helpfulCount = 6,
            xpEarned = 25
        ),
        MyReview(
            id = "r6",
            spotId = "4",
            spotName = "Cascada Los Frailes",
            category = "Pereira, Risaralda",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_1_month,
            rating = 4.6,
            comment = "",
            commentRes = R.string.review_comment_renata_4,
            helpfulCount = 4,
            xpEarned = 25
        ),
        MyReview(
            id = "r7",
            spotId = null,
            spotName = "Café Jesús Martín",
            category = "Salento, Quindío",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_1_month,
            rating = 5.0,
            comment = "",
            commentRes = R.string.review_comment_13,
            helpfulCount = 11,
            xpEarned = 25
        ),
        MyReview(
            id = "r8",
            spotId = null,
            spotName = "Mirador El Silencio",
            category = "Pereira, Risaralda",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_2_months,
            rating = 4.4,
            comment = "",
            commentRes = R.string.review_comment_16,
            helpfulCount = 3,
            xpEarned = 25
        ),
        MyReview(
            id = "r9",
            spotId = null,
            spotName = "Salento Centro Histórico",
            category = "Salento, Quindío",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_2_months,
            rating = 4.8,
            comment = "",
            commentRes = R.string.review_comment_14,
            helpfulCount = 7,
            xpEarned = 25
        ),
        MyReview(
            id = "r10",
            spotId = null,
            spotName = "Filandia y su Mirador",
            category = "Filandia, Quindío",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_2_months,
            rating = 5.0,
            comment = "",
            commentRes = R.string.review_comment_10,
            helpfulCount = 10,
            xpEarned = 25
        ),
        MyReview(
            id = "r11",
            spotId = null,
            spotName = "Jardín Botánico del Quindío",
            category = "Calarcá, Quindío",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_3_months,
            rating = 4.5,
            comment = "",
            commentRes = R.string.review_comment_11,
            helpfulCount = 5,
            xpEarned = 25
        ),
        MyReview(
            id = "r12",
            spotId = null,
            spotName = "Mirador de Chipre",
            category = "Manizales, Caldas",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_3_months,
            rating = 4.9,
            comment = "",
            commentRes = R.string.review_comment_12,
            helpfulCount = 14,
            xpEarned = 25
        ),
        MyReview(
            id = "r13",
            spotId = null,
            spotName = "Café Quindío Museo",
            category = "Armenia, Quindío",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_4_months,
            rating = 4.3,
            comment = "",
            commentRes = R.string.review_comment_18,
            helpfulCount = 2,
            xpEarned = 25
        ),
        MyReview(
            id = "r14",
            spotId = null,
            spotName = "Ruta del Café de Calarcá",
            category = "Calarcá, Quindío",
            dateLabel = "",
            dateLabelRes = R.string.time_ago_4_months,
            rating = 4.7,
            comment = "",
            commentRes = R.string.review_comment_17,
            helpfulCount = 6,
            xpEarned = 25
        )
    )

    val proposals = listOf(
        ProposedSpot(
            id = "p1",
            name = "Cascada La Cristalina",
            location = "Salento, Quindío",
            submittedLabelRes = R.string.profile_proposal_1_submitted,
            status = ProposalStatus.APPROVED,
            xpEarned = 100,
            noteRes = R.string.profile_proposal_1_note,
            spotId = "16"
        ),
        ProposedSpot(
            id = "p2",
            name = "Mirador El Silencio",
            location = "Pereira, Risaralda",
            submittedLabelRes = R.string.profile_proposal_2_submitted,
            status = ProposalStatus.PENDING,
            xpEarned = 0,
            noteRes = R.string.profile_proposal_2_note,
            spotId = "13"
        ),
        ProposedSpot(
            id = "p3",
            name = "Cascada Los Frailes",
            location = "Pereira, Risaralda",
            submittedLabelRes = R.string.profile_proposal_3_submitted,
            status = ProposalStatus.APPROVED,
            xpEarned = 100,
            noteRes = R.string.profile_proposal_3_note,
            spotId = "4"
        )
    )

    val settings = ProfileSettings()

    val permissions = listOf(
        AppPermission(
            id = "location",
            emoji = "📍",
            titleRes = R.string.profile_permission_location_title,
            descriptionRes = R.string.profile_permission_location_description,
            isGranted = true
        ),
        AppPermission(
            id = "camera",
            emoji = "📷",
            titleRes = R.string.profile_permission_camera_title,
            descriptionRes = R.string.profile_permission_camera_description,
            isGranted = true
        ),
        AppPermission(
            id = "notifications",
            emoji = "🔔",
            titleRes = R.string.profile_permission_notifications_title,
            descriptionRes = R.string.profile_permission_notifications_description,
            isGranted = true
        ),
        AppPermission(
            id = "microphone",
            emoji = "🎙️",
            titleRes = R.string.profile_permission_microphone_title,
            descriptionRes = R.string.profile_permission_microphone_description,
            isGranted = false
        )
    )

    /**
     * Idiomas disponibles.
     *
     * Solo se listan los que tienen traducción real en `res/values/` y
     * `res/values-en/`; los demás todavía no están soportados.
     */
    val languages = listOf(
        AppLanguage("es-CO", "🇨🇴", "Español", "Español (Colombia)"),
        AppLanguage("en-US", "🇺🇸", "English", "English (US)")
    )

    val moderationQueue = listOf(
        ModerationReport(
            id = "m1",
            spotName = "Cascada La Cristalina",
            reasonRes = R.string.profile_moderation_1_reason,
            reporter = "@senderista_quindio",
            timeLabelRes = R.string.time_ago_20_min,
            spotId = "16"
        ),
        ModerationReport(
            id = "m2",
            spotName = "Mirador El Silencio",
            reasonRes = R.string.profile_moderation_2_reason,
            reporter = "@viajero_risaralda",
            timeLabelRes = R.string.time_ago_1_hour,
            spotId = "13"
        ),
        ModerationReport(
            id = "m3",
            spotName = "Café Especial La Montaña",
            reasonRes = R.string.profile_moderation_3_reason,
            reporter = "@cafe_lover",
            timeLabelRes = R.string.time_ago_3_hours,
            spotId = "2"
        ),
        ModerationReport(
            id = "m4",
            spotName = "Sendero Casa de los Colibríes",
            reasonRes = R.string.profile_moderation_4_reason,
            reporter = "@aventurero_caldas",
            timeLabelRes = R.string.time_ago_5_hours,
            spotId = "1"
        ),
        ModerationReport(
            id = "m5",
            spotName = "Piscinas Termales El Otoño",
            reasonRes = R.string.profile_moderation_5_reason,
            reporter = "@turista_armenia",
            timeLabelRes = R.string.time_yesterday,
            spotId = "5"
        ),
        ModerationReport(
            id = "m6",
            spotName = "Parque del Café",
            reasonRes = R.string.profile_moderation_6_reason,
            reporter = "@plan_cafetero",
            timeLabelRes = R.string.time_yesterday,
            spotId = "6"
        )
    )
}
