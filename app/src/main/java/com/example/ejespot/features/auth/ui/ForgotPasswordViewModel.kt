package com.example.ejespot.features.auth.ui

import androidx.lifecycle.ViewModel
import com.example.ejespot.R
import com.example.ejespot.core.util.ResourceProviderHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    /** Textos de error tomados de `strings.xml` (guía 14). */
    private val resources get() = ResourceProviderHolder.resources

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onSendRecoveryLink() {
        val email = _uiState.value.email.trim()
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = resources.getString(R.string.error_empty_email)) }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = resources.getString(R.string.error_invalid_email)) }
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
