package com.example.ejespot.features.auth.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onSendRecoveryLink() {
        val email = _uiState.value.email.trim()
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor ingresa tu correo electrónico") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = "Ingresa un correo electrónico válido") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
    }

    fun onResendEmail() {
        _uiState.update { it.copy(isLoading = true) }
        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
    }

    fun resetState() {
        _uiState.update { ForgotPasswordUiState() }
    }
}
