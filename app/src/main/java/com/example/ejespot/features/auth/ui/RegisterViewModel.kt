package com.example.ejespot.features.auth.ui

import androidx.lifecycle.ViewModel
import com.example.ejespot.R
import com.example.ejespot.core.util.ResourceProviderHolder
import com.example.ejespot.domain.model.User
import com.example.ejespot.domain.model.UserRole
import com.example.ejespot.domain.repository.DemoUsers
import com.example.ejespot.features.dashboard.profile.ProfileSampleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /** Textos de error tomados de `strings.xml` (guía 14). */
    private val resources get() = ResourceProviderHolder.resources

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
            _uiState.update { it.copy(errorMessage = resources.getString(R.string.error_empty_fields)) }
            return
        }
        if (!state.acceptedTerms) {
            _uiState.update {
                it.copy(errorMessage = resources.getString(R.string.error_terms_required))
            }
            return
        }
        _uiState.update { it.copy(isLoading = true) }

        // Se crea la cuenta en memoria (Fase 2: no hay backend) y se devuelve
        // para que la navegación inicie su sesión y la persista en Data Store.
        val newUser = User(
            id = "u_${state.email.trim().lowercase()}",
            email = state.email.trim(),
            name = state.fullName.trim(),
            password = state.password,
            role = UserRole.USER,
            profileId = state.email.trim().lowercase()
        )

        DemoUsers.register(newUser)
        val usernamePart = state.email.trim().substringBefore("@")
        ProfileSampleData.registerProfile(newUser.profileId, newUser.name, usernamePart)

        _uiState.update {
            it.copy(
                isLoading = false,
                registerSuccess = true,
                registeredUser = newUser
            )
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(registerSuccess = false, registeredUser = null) }
    }

    // ---------------------------------------------------------------------
    // Botón "atrás" del dispositivo (guía 10)
    // ---------------------------------------------------------------------

    /** El usuario presionó el botón "atrás": se pide confirmar la salida. */
    fun onBackClick() {
        _uiState.update { it.copy(showExitDialog = true) }
    }

    /** El usuario confirmó que quiere salir: se borra el formulario. */
    fun onConfirmExit() {
        _uiState.value = RegisterUiState()
    }

    /** El usuario canceló el diálogo de salida. */
    fun onDismissExitDialog() {
        _uiState.update { it.copy(showExitDialog = false) }
    }
}
