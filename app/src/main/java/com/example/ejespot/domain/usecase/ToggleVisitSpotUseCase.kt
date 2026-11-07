package com.example.ejespot.domain.usecase

import com.example.ejespot.domain.repository.SpotRepository

/**
 * Caso de uso: Alterna el estado de visitado de un spot para el usuario actual.
 */
class ToggleVisitSpotUseCase(
    private val spotRepository: SpotRepository
) {
    operator fun invoke(spotId: String): Boolean {
        return spotRepository.toggleVisited(spotId)
    }
}
