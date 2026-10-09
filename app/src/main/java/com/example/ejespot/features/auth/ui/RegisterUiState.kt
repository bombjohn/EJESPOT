package com.example.ejespot.features.auth.ui

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val department: String = "Risaralda (Pereira)",
    val password: String = "",
    val acceptedTerms: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registerSuccess: Boolean = false
)
