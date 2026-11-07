package com.example.ejespot.domain.usecase

import com.example.ejespot.domain.repository.SpotRepository

/**
 * Caso de uso: Actualiza la información básica de un spot.
 */
class UpdateSpotUseCase(
    private val spotRepository: SpotRepository
) {
    operator fun invoke(
        id: String,
        name: String,
        category: String,
        municipality: String,
        hours: String,
        priceRange: String,
        description: String
    ) {
        spotRepository.updateSpot(id, name, category, municipality, hours, priceRange, description)
    }
}
