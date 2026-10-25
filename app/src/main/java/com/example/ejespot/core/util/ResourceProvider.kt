package com.example.ejespot.core.util

import android.content.Context

/**
 * Puente para que los ViewModels accedan a los textos de `strings.xml`
 * sin depender de un `Context` (guía 14).
 *
 * `stringResource()` solo funciona dentro de un `@Composable`, así que los
 * mensajes de error y de resultado que arma un ViewModel se obtienen aquí.
 *
 * La implementación se inicializa una sola vez desde [EjeSpotApplication]
 * con el contexto de la aplicación.
 */
interface ResourceProvider {
    fun getString(id: Int): String

    /** Variante con argumentos de formato (`%1$s`, `%1$d`, ...). */
    fun getString(id: Int, vararg formatArgs: Any): String
}

/**
 * Implementación basada en el contexto de la aplicación.
 * Se registra en [ResourceProviderHolder] al arrancar la app.
 */
class ResourceProviderImpl(private val context: Context) : ResourceProvider {
    override fun getString(id: Int): String = context.getString(id)

    override fun getString(id: Int, vararg formatArgs: Any): String =
        context.getString(id, *formatArgs)
}

/**
 * Proveedor de respaldo: devuelve el identificador del recurso en vez de
 * fallar, para que un ViewModel creado antes de la inicialización no rompa
 * la app (el texto se reemplaza en el primer recomposición).
 */
private object FallbackResourceProvider : ResourceProvider {
    override fun getString(id: Int): String = "res:$id"

    override fun getString(id: Int, vararg formatArgs: Any): String = "res:$id"
}

/**
 * Punto de acceso estático al [ResourceProvider] (equivalente a la inyección
 * de dependencias, pero adaptado a un proyecto sin Hilt).
 */
object ResourceProviderHolder {

    private var instance: ResourceProvider? = null

    fun init(context: Context) {
        if (instance == null) {
            instance = ResourceProviderImpl(context.applicationContext)
        }
    }

    /**
     * Proveedor de textos. Nunca lanza: si la app aún no inicializó el
     * proveedor, devuelve un respaldo seguro.
     */
    val resources: ResourceProvider
        get() = instance ?: FallbackResourceProvider
}
