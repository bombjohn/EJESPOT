package com.example.ejespot.features.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ejespot.data.repository.remote.OpenRouterAiRepository
import com.example.ejespot.domain.model.AiMessage
import com.example.ejespot.domain.repository.AiAssistantRepository
import com.example.ejespot.domain.usecase.AskCoffeeGuideUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AiAssistantUiState(
    val messages: List<AiMessage> = emptyList(),
    val isLoading: Boolean = false,
    val inputText: String = "",
    val spotContext: String? = null,
    val errorMessage: String? = null
)

class AiAssistantViewModel(
    private val aiRepository: AiAssistantRepository = OpenRouterAiRepository(),
    private val askGuideUseCase: AskCoffeeGuideUseCase = AskCoffeeGuideUseCase(aiRepository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AiAssistantUiState(
            messages = listOf(
                AiMessage(
                    text = "¡Hola, caminante! ☕🌿 Soy tu **Guía Cafetero IA**, impulsado por inteligencia artificial.\n\nPuedo armarte itinerarios, recomendarte rutas de senderismo, fincas cafeteras tradicionales o darte consejos de transporte y comida típica.\n\n¿Qué plan tienes en mente para hoy?",
                    isUser = false
                )
            )
        )
    )
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    val quickPrompts = listOf(
        "🌴 Itinerario 1 día en Salento",
        "☕ Mejores cafés especiales",
        "♨️ Tips para los termales",
        "🎒 ¿Qué ropa empacar?"
    )

    fun onInputTextChanged(newText: String) {
        _uiState.update { it.copy(inputText = newText) }
    }

    fun updateApiKey(key: String) {
        (aiRepository as? OpenRouterAiRepository)?.updateApiKey(key)
    }

    fun setSpotContext(context: String?) {
        if (!context.isNullOrBlank()) {
            _uiState.update { state ->
                val contextGreeting = AiMessage(
                    text = "Veo que estás consultando sobre **$context**. 📍 ¿Deseas que te dé tips de visita, mejor hora para ir o lugares cercanos?",
                    isUser = false
                )
                state.copy(
                    spotContext = context,
                    messages = state.messages + contextGreeting
                )
            }
        }
    }

    fun sendMessage(customPrompt: String? = null) {
        val promptToSend = (customPrompt ?: _uiState.value.inputText).trim()
        if (promptToSend.isBlank() || _uiState.value.isLoading) return

        val userMessage = AiMessage(text = promptToSend, isUser = true)
        val currentHistory = _uiState.value.messages + userMessage

        _uiState.update {
            it.copy(
                messages = currentHistory,
                inputText = "",
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = askGuideUseCase(
                prompt = promptToSend,
                history = currentHistory,
                spotContext = _uiState.value.spotContext
            )

            result.onSuccess { aiResponse ->
                val botMessage = AiMessage(text = aiResponse, isUser = false)
                _uiState.update {
                    it.copy(
                        messages = it.messages + botMessage,
                        isLoading = false
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se pudo conectar con el Guía IA: ${err.message}"
                    )
                }
            }
        }
    }
}
