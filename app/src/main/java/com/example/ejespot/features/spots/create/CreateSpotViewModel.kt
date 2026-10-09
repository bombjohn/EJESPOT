package com.example.ejespot.features.spots.create

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreateSpotViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateSpotUiState())
    val uiState: StateFlow<CreateSpotUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, errorMessage = null) }
    }

    fun onCategoryChanged(category: String) {
        _uiState.update { it.copy(category = category, errorMessage = null) }
    }

    fun onLocationChanged(loc: String) {
        _uiState.update { it.copy(location = loc, errorMessage = null) }
    }

    fun onDescriptionChanged(desc: String) {
        _uiState.update { it.copy(description = desc, errorMessage = null) }
    }

    fun onHoursChanged(hours: String) {
        _uiState.update { it.copy(hours = hours, errorMessage = null) }
    }

    fun onPriceTypeChanged(price: String) {
        _uiState.update { it.copy(priceType = price, errorMessage = null) }
    }

    fun onSubmit(onSuccessSpot: ((name: String, cat: String, loc: String, hours: String, price: String, desc: String) -> Unit)? = null) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor ingresa el nombre del lugar") }
            return
        }
        val spotName = state.name.trim()
        val cat = state.category
        val loc = state.location
        val hours = state.hours
        val price = state.priceType
        val desc = state.description.ifBlank { "Nuevo punto turístico y cultural sugerido por la comunidad en $loc." }

        onSuccessSpot?.invoke(spotName, cat, loc, hours, price, desc)

        _uiState.update {
            it.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null,
                submittedSpotName = spotName,
                submittedCategory = cat,
                submittedLocation = loc
            )
        }
    }

    fun resetState() {
        _uiState.update { CreateSpotUiState() }
    }
}

