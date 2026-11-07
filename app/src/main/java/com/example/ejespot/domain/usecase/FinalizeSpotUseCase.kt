package com.example.ejespot.domain.usecase

import com.example.ejespot.domain.repository.SpotRepository

/**
 * Caso de uso: Finaliza / cierra o reactiva un spot.
 */
class FinalizeSpotUseCase(
    private val spotRepository: SpotRepository
) {
    operator fun invoke(id: String, finalized: Boolean = true) {
        spotRepository.finalizeSpot(id, finalized)
    }
}
