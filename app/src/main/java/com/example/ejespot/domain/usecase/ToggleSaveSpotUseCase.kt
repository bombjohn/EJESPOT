package com.example.ejespot.domain.usecase

import com.example.ejespot.domain.repository.SpotRepository

/**
 * Caso de uso: Alterna el estado de guardado/favorito de un spot para el usuario actual.
 */
class ToggleSaveSpotUseCase(
    private val spotRepository: SpotRepository
) {
    operator fun invoke(spotId: String): Boolean {
        return spotRepository.toggleSaved(spotId)
    }
}
