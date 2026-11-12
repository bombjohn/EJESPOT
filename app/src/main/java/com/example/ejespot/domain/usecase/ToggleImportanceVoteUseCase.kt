package com.example.ejespot.domain.usecase

import com.example.ejespot.domain.repository.SpotRepository

/**
 * Caso de uso: Alterna el voto 'Es importante / Me interesa' en una publicación.
 */
class ToggleImportanceVoteUseCase(
    private val spotRepository: SpotRepository
) {
    operator fun invoke(spotId: String): Boolean {
        return spotRepository.toggleImportanceVote(spotId)
    }
}
