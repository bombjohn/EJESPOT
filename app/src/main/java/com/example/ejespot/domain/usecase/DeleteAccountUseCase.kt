package com.example.ejespot.domain.usecase

import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.data.repository.remote.FirebaseAuthRepository
import com.example.ejespot.domain.repository.DemoUsers
import com.example.ejespot.domain.repository.ProfileRepository
import com.example.ejespot.domain.repository.SpotRepository

/**
 * Caso de uso: Elimina la cuenta de usuario, limpia sus datos personales,
 * sus favoritos, visitados y propuestas, la elimina de Firebase Auth y cierra la sesión activa.
 */
class DeleteAccountUseCase(
    private val spotRepository: SpotRepository,
    private val profileRepository: ProfileRepository,
    private val firebaseAuthRepo: FirebaseAuthRepository = FirebaseAuthRepository()
) {
    suspend operator fun invoke(profileId: String, onFinished: () -> Unit) {
        // Solo se borran los datos locales si la cuenta remota se eliminó (o
        // Firebase no está configurado). Antes se borraba todo aunque fallara.
        val remoteResult = runCatching { firebaseAuthRepo.deleteCurrentUser() }.getOrNull()
        val remoteOk = remoteResult?.isSuccess != false
        if (remoteOk) {
            spotRepository.clearUserData(profileId)
            DemoUsers.removeUser(profileId)
            profileRepository.removeProfile(profileId)
        }
        SessionManager.signOut()
        onFinished()
    }
}
