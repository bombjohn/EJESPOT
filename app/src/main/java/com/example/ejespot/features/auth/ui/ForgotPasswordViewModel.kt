package com.example.ejespot.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ejespot.R
import com.example.ejespot.core.util.ResourceProviderHolder
import com.example.ejespot.data.repository.remote.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val authRepo = FirebaseAuthRepository()

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

        viewModelScope.launch {
            val result = authRepo.sendPasswordResetEmail(email)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, isSuccess = true, errorMessage = null)
                } else {
                    it.copy(
                        isLoading = false,
                        errorMessage = resources.getString(errorMessageFor(result.exceptionOrNull()))
                    )
                }
            }
        }
    }

    fun onResendEmail() {
        val email = _uiState.value.email.trim()
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepo.sendPasswordResetEmail(email)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, isSuccess = true, errorMessage = null)
                } else {
                    it.copy(
                        isLoading = false,
                        errorMessage = resources.getString(errorMessageFor(result.exceptionOrNull()))
                    )
                }
            }
        }
    }

    private fun errorMessageFor(error: Throwable?): Int = when (error) {
        is com.google.firebase.auth.FirebaseAuthInvalidUserException -> R.string.forgot_email_not_found
        else -> R.string.forgot_send_error
    }

    fun resetState() {
        _uiState.update { ForgotPasswordUiState() }
    }
}
