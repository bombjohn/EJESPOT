package com.example.ejespot.domain.repository

import com.example.ejespot.domain.model.ModerationReport
import com.example.ejespot.domain.model.MyReview
import com.example.ejespot.domain.model.ProposedSpot
import com.example.ejespot.domain.model.UserProfile

/**
 * Contrato de acceso a los datos de perfil, propuestas y cola de moderación.
 */
interface ProfileRepository {
    fun getProfile(profileId: String): UserProfile
    fun getProposals(profileId: String): List<ProposedSpot>
    fun getReviews(profileId: String): List<MyReview>
    fun getModerationQueue(): List<ModerationReport>
    fun removeProfile(profileId: String)
}
