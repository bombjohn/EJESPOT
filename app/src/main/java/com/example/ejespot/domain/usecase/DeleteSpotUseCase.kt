package com.example.ejespot.domain.usecase

import com.example.ejespot.domain.repository.SpotRepository

/**
 * Caso de uso: Elimina un spot de la base de datos y de las listas de guardados/visitados.
 */
class DeleteSpotUseCase(
    private val spotRepository: SpotRepository
) {
    operator fun invoke(id: String) {
        spotRepository.deleteSpot(id)
    }
}
