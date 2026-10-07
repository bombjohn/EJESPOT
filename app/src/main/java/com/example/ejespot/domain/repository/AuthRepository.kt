package com.example.ejespot.domain.repository

import com.example.ejespot.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun loginAsGuest(): Result<User>
}
