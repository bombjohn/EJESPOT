package com.example.ejespot.domain.usecase

import com.example.ejespot.domain.repository.SpotRepository

/**
 * Caso de uso: Rechaza una propuesta de spot retirándola de la cola de pendientes.
 */
class RejectProposalUseCase(
    private val spotRepository: SpotRepository
) {
    operator fun invoke(spotId: String, reason: String = "No cumple con las pautas de la comunidad") {
        spotRepository.rejectPendingSpot(spotId, reason)
    }
}
