package com.example.ejespot.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ejespot.R
import com.example.ejespot.core.util.ResourceProviderHolder
import com.example.ejespot.domain.model.User
import com.example.ejespot.domain.repository.DemoAuthRepository
import com.example.ejespot.domain.repository.DemoUsers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: DemoAuthRepository = DemoAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /** Cuentas demo listas para entrar con un solo toque. */
    val demoAccounts: List<User> = DemoUsers.quickAccess

    /** Textos de error tomados de `strings.xml` (guía 14). */
    private val resources get() = ResourceProviderHolder.resources

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLogin() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = resources.getString(R.string.error_empty_fields)) }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            authRepository.login(state.email, state.password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(isLoading = false, loginSuccess = true, authenticatedUser = user)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                                ?: resources.getString(R.string.error_generic_login)
                        )
                    }
                }
        }
    }

    /** Entra directo con una de las cuentas demo, sin teclear credenciales. */
    fun onQuickLogin(user: User) {
        _uiState.update {
            it.copy(
                email = user.email,
                password = user.password,
                errorMessage = null,
                isLoading = false,
                loginSuccess = true,
                authenticatedUser = user
            )
        }
    }

    fun onLoginAsGuest() {
        viewModelScope.launch {
            authRepository.loginAsGuest().onSuccess { guest ->
                _uiState.update {
                    it.copy(loginSuccess = true, authenticatedUser = guest, errorMessage = null)
                }
            }
        }
    }
}
