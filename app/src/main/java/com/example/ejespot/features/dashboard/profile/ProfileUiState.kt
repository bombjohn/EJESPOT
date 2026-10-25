package com.example.ejespot.features.dashboard.profile

import com.example.ejespot.domain.model.*

data class ProfileUiState(
    val profile: UserProfile,
    val stats: UserStats,
    val levels: List<CommunityLevel>,
    val activities: List<ProfileActivity>,
    val badges: List<AchievementBadge>,
    val notifications: List<ProfileNotification>,
    val reviews: List<MyReview>,
    val proposals: List<ProposedSpot>,
    val settings: ProfileSettings,
    val permissions: List<AppPermission>,
    val selectedLanguage: AppLanguage,
    val moderationQueue: List<ModerationReport>,
    val draft: ProfileDraft,
    val draftError: String? = null,
    val isModerator: Boolean = true
) {
    val unreadNotifications: Int
        get() = notifications.count { !it.isRead }

    val pendingModeration: Int
        get() = moderationQueue.size

    val unlockedBadges: Int
        get() = badges.count { it.isUnlocked }
}
