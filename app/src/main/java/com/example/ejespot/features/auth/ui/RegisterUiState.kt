package com.example.ejespot.features.auth.ui

import com.example.ejespot.domain.model.User

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val department: String = "Risaralda (Pereira)",
    val password: String = "",
    val acceptedTerms: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registerSuccess: Boolean = false,
    /** Cuenta creada al registrarse, para iniciar su sesión. */
    val registeredUser: User? = null,
    /** Indica si el diálogo de confirmación de salida está visible. */
    val showExitDialog: Boolean = false
)
