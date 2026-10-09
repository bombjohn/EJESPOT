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

    fun onDepartmentChanged(dept: String) {
        _uiState.update { it.copy(department = dept, errorMessage = null) }
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

    fun onSubmit() {
        val state = _uiState.value
        if (state.name.isBlank() || state.description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor completa el nombre y la descripción") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
    }

    fun resetState() {
        _uiState.update { CreateSpotUiState() }
    }
}
