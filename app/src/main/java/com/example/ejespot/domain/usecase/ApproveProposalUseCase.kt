package com.example.ejespot.domain.usecase

import com.example.ejespot.domain.repository.SpotRepository

/**
 * Caso de uso: Aprueba una propuesta de spot y publica el lugar en la lista oficial.
 */
class ApproveProposalUseCase(
    private val spotRepository: SpotRepository
) {
    operator fun invoke(spotId: String) {
        spotRepository.approvePendingSpot(spotId)
    }
}
