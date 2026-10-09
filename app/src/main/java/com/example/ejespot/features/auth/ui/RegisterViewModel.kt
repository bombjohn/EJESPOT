package com.example.ejespot.features.auth.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChanged(name: String) {
        _uiState.update { it.copy(fullName = name, errorMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onDepartmentChanged(dept: String) {
        _uiState.update { it.copy(department = dept, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onToggleTerms(accepted: Boolean) {
        _uiState.update { it.copy(acceptedTerms = accepted, errorMessage = null) }
    }

    fun onRegister() {
        val state = _uiState.value
        if (state.fullName.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor completa todos los campos") }
            return
        }
        if (!state.acceptedTerms) {
            _uiState.update { it.copy(errorMessage = "Debes aceptar los Términos y la Política de Privacidad") }
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(registerSuccess = false) }
    }
}
