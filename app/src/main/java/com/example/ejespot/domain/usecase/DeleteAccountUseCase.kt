package com.example.ejespot.domain.usecase

import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.domain.repository.DemoUsers
import com.example.ejespot.domain.repository.ProfileRepository
import com.example.ejespot.domain.repository.SpotRepository

/**
 * Caso de uso: Elimina la cuenta de usuario, limpia sus datos personales,
 * sus favoritos, visitados y propuestas, y cierra la sesión activa.
 */
class DeleteAccountUseCase(
    private val spotRepository: SpotRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profileId: String, onFinished: () -> Unit) {
        spotRepository.clearUserData(profileId)
        DemoUsers.removeUser(profileId)
        profileRepository.removeProfile(profileId)
        SessionManager.signOut()
        onFinished()
    }
}
