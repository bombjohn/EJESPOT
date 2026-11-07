package com.example.ejespot.data.repository.local

import com.example.ejespot.domain.model.ModerationReport
import com.example.ejespot.domain.model.MyReview
import com.example.ejespot.domain.model.ProposedSpot
import com.example.ejespot.domain.model.UserProfile
import com.example.ejespot.domain.repository.ProfileRepository
import com.example.ejespot.features.dashboard.profile.ProfileSampleData

/**
 * Implementación local en memoria del repositorio de Perfil y Moderación.
 */
class LocalProfileRepository : ProfileRepository {
    override fun getProfile(profileId: String): UserProfile = ProfileSampleData.profileFor(profileId)
    override fun getProposals(profileId: String): List<ProposedSpot> = ProfileSampleData.proposalsFor(profileId)
    override fun getReviews(profileId: String): List<MyReview> = ProfileSampleData.reviewsFor(profileId)
    override fun getModerationQueue(): List<ModerationReport> = ProfileSampleData.moderationQueue
    override fun removeProfile(profileId: String) = ProfileSampleData.removeProfile(profileId)
}
