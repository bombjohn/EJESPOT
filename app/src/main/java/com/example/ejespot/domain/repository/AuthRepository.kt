package com.example.ejespot.domain.repository

import com.example.ejespot.domain.model.User
import com.example.ejespot.domain.model.UserRole

/**
 * Contrato de autenticación usado por la app.
 * En Fase 2 lo implementa [DemoAuthRepository] con cuentas quemadas.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun loginAsGuest(): Result<User>
}

/**
 * Cuentas demo quemadas en memoria para probar la app sin backend.
 *
 * Las credenciales aparecen también en la pantalla de Login para que
 * cualquiera del grupo pueda entrar a probar cada rol.
 */
object DemoUsers {

    val renata = User(
        id = "u_renata",
        email = "renata@ejespot.co",
        name = "Renata Castaño",
        password = "123456",
        role = UserRole.USER,
        profileId = "renata"
    )

    val mateo = User(
        id = "u_mateo",
        email = "moderador@ejespot.co",
        name = "Mateo Salazar",
        password = "admin123",
        role = UserRole.ADMIN,
        profileId = "mateo"
    )

    val camila = User(
        id = "u_camila",
        email = "camila@ejespot.co",
        name = "Camila Herrera",
        password = "123456",
        role = UserRole.USER,
        profileId = "camila"
    )

    /** Invitado: explora el feed, el mapa y el perfil sin cuenta. */
    val guest = User(
        id = "u_guest",
        email = "",
        name = "Invitado",
        role = UserRole.USER,
        profileId = "invitado",
        isGuest = true
    )

    val accounts: List<User> = listOf(renata, mateo, camila)

    fun findByEmail(email: String): User? =
        accounts.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }

    /** Entrada directa desde los botones de "acceso rápido" del Login. */
    val quickAccess: List<User> = accounts
}

/**
 * Implementación en memoria del [AuthRepository].
 * En Fase 2 la sesión vive mientras la app está abierta.
 */
class DemoAuthRepository : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        val user = DemoUsers.findByEmail(email)
            ?: return Result.failure(IllegalArgumentException("No existe una cuenta con ese correo."))

        if (user.password != password) {
            return Result.failure(IllegalArgumentException("La contraseña no es correcta."))
        }
        return Result.success(user)
    }

    override suspend fun loginAsGuest(): Result<User> = Result.success(DemoUsers.guest)
}
