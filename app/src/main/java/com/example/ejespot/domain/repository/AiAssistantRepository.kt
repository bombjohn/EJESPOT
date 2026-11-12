package com.example.ejespot.domain.repository

import com.example.ejespot.domain.model.AiMessage

/**
 * Contrato para el servicio de Inteligencia Artificial (OpenRouter).
 */
interface AiAssistantRepository {
    suspend fun askGuide(
        prompt: String,
        history: List<AiMessage> = emptyList(),
        spotContext: String? = null
    ): Result<String>
}
