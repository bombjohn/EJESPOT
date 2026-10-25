package com.example.ejespot.core.session

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ejespot.R
import com.example.ejespot.core.util.ResourceProviderHolder
import com.example.ejespot.data.repository.local.LocalSessionRepository
import com.example.ejespot.domain.model.User
import com.example.ejespot.domain.model.UserRole
import com.example.ejespot.domain.model.UserSession
import com.example.ejespot.domain.repository.DemoUsers
import com.example.ejespot.domain.repository.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Sesión de la app (Fase 2).
 *
 * Mantiene el usuario activo en memoria para que toda la UI lo consulte de
 * forma síncrona, pero **persiste la sesión con Data Store** (guía 13) para
 * que sobreviva a cerrar la aplicación.
 *
 * Flujo:
 * 1. [restore] se llama al arrancar y reconstruye el usuario guardado.
 * 2. [signIn] guarda al usuario en memoria y en Data Store.
 * 3. [signOut] limpia ambos.
 */
object SessionManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /** Repositorio de sesión. Se inicializa en [init]. */
    private lateinit var sessionRepository: SessionRepository

    var currentUser: User? by mutableStateOf(null)
        private set

    /** `true` una vez que se intentó restaurar la sesión (para el splash). */
    var isRestored: Boolean by mutableStateOf(false)
        private set

    /** Se completa cuando la sesión guardada ya está disponible en memoria. */
    private val restoreSignal = CompletableDeferred<Unit>()

    /** Evita restaurar dos veces (la app puede recrear la Activity). */
    private var restoreStarted = false

    val role: UserRole
        get() = currentUser?.role ?: UserRole.USER

    val profileId: String
        get() = currentUser?.profileId ?: "invitado"

    val isGuest: Boolean
        get() = currentUser?.isGuest ?: true

    val displayName: String
        get() = currentUser?.name
            ?: ResourceProviderHolder.resources.getString(R.string.profile_role_guest)

    /**
     * Etiqueta del rol ya traducida (guía 14).
     * Se resuelve desde `strings.xml` según el idioma del dispositivo.
     */
    val roleLabel: String
        get() = currentUser?.let {
            ResourceProviderHolder.resources.getString(it.roleLabelRes)
        } ?: ResourceProviderHolder.resources.getString(R.string.profile_role_guest)

    /** Prepara la sesión con el contexto de la aplicación. Idempotente. */
    fun init(context: Context) {
        if (::sessionRepository.isInitialized) return
        sessionRepository = LocalSessionRepository(context.applicationContext)
    }

    /**
     * Reconstruye la sesión guardada, si existe. Se invoca una sola vez al
     * arrancar; al terminar marca [isRestored] para que el splash decida.
     */
    fun restore() {
        if (restoreStarted) return
        restoreStarted = true

        if (!::sessionRepository.isInitialized) {
            isRestored = true
            restoreSignal.complete(Unit)
            return
        }
        scope.launch {
            val saved = runCatching { sessionRepository.session.first() }.getOrNull()
            currentUser = saved?.let { resolveUser(it) }
            isRestored = true
            restoreSignal.complete(Unit)
        }
    }

    /**
     * Espera a que la sesión guardada se haya restaurado.
     *
     * El splash la usa para no decidir la ruta inicial antes de tiempo: sin
     * esta espera, una sesión válida podría pasarse por alto y el usuario
     * vería el login aunque ya estuviera conectado.
     */
    suspend fun awaitRestore() {
        restoreSignal.await()
    }

    fun signIn(user: User) {
        currentUser = user
        if (::sessionRepository.isInitialized) {
            scope.launch {
                runCatching {
                    sessionRepository.saveSession(
                        UserSession(
                            userId = user.id,
                            profileId = user.profileId,
                            isGuest = user.isGuest
                        )
                    )
                }
            }
        }
    }

    fun signOut() {
        currentUser = null
        if (::sessionRepository.isInitialized) {
            scope.launch {
                runCatching { sessionRepository.clearSession() }
            }
        }
    }

    /**
     * Convierte la sesión persistida en el [User] completo.
     * Se apoya en las cuentas demo quemadas; si el usuario ya no existe
     * (o es invitado) se construye un usuario mínimo válido.
     */
    private fun resolveUser(session: UserSession): User {
        if (session.isGuest) return DemoUsers.guest

        DemoUsers.accounts.firstOrNull { it.id == session.userId }?.let { return it }

        return User(
            id = session.userId,
            name = ResourceProviderHolder.resources.getString(R.string.session_default_user_name),
            role = UserRole.USER,
            profileId = session.profileId
        )
    }
}
