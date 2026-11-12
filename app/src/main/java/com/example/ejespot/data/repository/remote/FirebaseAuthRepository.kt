package com.example.ejespot.data.repository.remote

import com.example.ejespot.domain.model.User
import com.example.ejespot.domain.model.UserRole
import com.example.ejespot.domain.repository.AuthRepository
import com.example.ejespot.domain.repository.DemoAuthRepository
import com.example.ejespot.domain.repository.DemoUsers
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Repositorio de autenticación que intenta usar Firebase Auth primero.
 * Si Firebase no está configurado o falla la conexión, delega al
 * [DemoAuthRepository] local para que la app siga siendo 100% funcional
 * en modo offline o de demostración.
 */
class FirebaseAuthRepository : AuthRepository {

    private val firebaseAuth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (_: Exception) {
        null
    }

    private val localFallback = DemoAuthRepository()

    override suspend fun login(email: String, password: String): Result<User> {
        // Intentar con Firebase Auth primero
        if (firebaseAuth != null) {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    // Verificar si es moderador precargado
                    val role = if (email.equals("admin@ejespot.com", ignoreCase = true) ||
                        email.equals("moderador@ejespot.co", ignoreCase = true)
                    ) UserRole.ADMIN else UserRole.USER

                    return Result.success(
                        User(
                            id = firebaseUser.uid,
                            email = firebaseUser.email ?: email,
                            name = firebaseUser.displayName ?: email.substringBefore("@"),
                            password = "",
                            role = role,
                            profileId = firebaseUser.uid
                        )
                    )
                }
            } catch (_: Exception) {
                // Firebase no configurado o sin conexión: usar fallback local
            }
        }
        return localFallback.login(email, password)
    }

    override suspend fun loginAsGuest(): Result<User> = localFallback.loginAsGuest()

    /**
     * Registra un nuevo usuario en Firebase Auth.
     * Retorna el [User] creado con su UID de Firebase.
     */
    suspend fun register(email: String, password: String, displayName: String): Result<User> {
        if (firebaseAuth != null) {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    // Actualizar el displayName en Firebase
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    firebaseUser.updateProfile(profileUpdates).await()

                    val user = User(
                        id = firebaseUser.uid,
                        email = email,
                        name = displayName,
                        password = "",
                        role = UserRole.USER,
                        profileId = firebaseUser.uid
                    )
                    // También registrar localmente para el fallback
                    DemoUsers.register(user.copy(password = password))
                    return Result.success(user)
                }
            } catch (e: Exception) {
                // Si Firebase falla, registrar solo localmente
                val localUser = User(
                    id = "u_${email.lowercase()}",
                    email = email,
                    name = displayName,
                    password = password,
                    role = UserRole.USER,
                    profileId = email.lowercase()
                )
                DemoUsers.register(localUser)
                return Result.success(localUser)
            }
        }
        // Sin Firebase: registro solo local
        val localUser = User(
            id = "u_${email.lowercase()}",
            email = email,
            name = displayName,
            password = password,
            role = UserRole.USER,
            profileId = email.lowercase()
        )
        DemoUsers.register(localUser)
        return Result.success(localUser)
    }

    /**
     * Envía un correo de restablecimiento de contraseña vía Firebase Auth.
     * Si Firebase no está configurado, simula el envío exitoso.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        if (firebaseAuth != null) {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                return Result.success(Unit)
            } catch (_: Exception) {
                // Firebase no disponible: simular éxito para la demo
            }
        }
        return Result.success(Unit)
    }

    /** Cierra la sesión de Firebase Auth. */
    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (_: Exception) { /* ignorar */ }
    }

    /** Retorna el usuario actual de Firebase, o null si no hay sesión activa. */
    fun getCurrentUser() = try {
        firebaseAuth?.currentUser
    } catch (_: Exception) {
        null
    }
}
