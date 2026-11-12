package com.example.ejespot.domain.usecase

import com.example.ejespot.domain.model.AiMessage
import com.example.ejespot.domain.repository.AiAssistantRepository

/**
 * Caso de uso: Consulta al Guía Cafetero IA recomendaciones turísticas personalizadas.
 */
class AskCoffeeGuideUseCase(
    private val aiRepository: AiAssistantRepository
) {
    suspend operator fun invoke(
        prompt: String,
        history: List<AiMessage> = emptyList(),
        spotContext: String? = null
    ): Result<String> {
        return aiRepository.askGuide(prompt, history, spotContext)
    }
}
