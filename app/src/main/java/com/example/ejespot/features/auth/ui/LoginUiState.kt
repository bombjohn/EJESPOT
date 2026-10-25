package com.example.ejespot.features.auth.ui

import com.example.ejespot.domain.model.User

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    /** Usuario autenticado; se entrega a la sesión al navegar al dashboard. */
    val authenticatedUser: User? = null
)
