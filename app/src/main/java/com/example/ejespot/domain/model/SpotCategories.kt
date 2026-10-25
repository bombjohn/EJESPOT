package com.example.ejespot.domain.model

import androidx.annotation.StringRes
import com.example.ejespot.R

/**
 * Categorías oficiales de EjeSpot (Fase 2).
 *
 * Punto único de verdad para el feed, el formulario de creación, el detalle
 * y los datos quemados: así ninguna categoría queda vacía ni desincronizada.
 *
 * Las constantes son **identificadores de datos** (se comparan con
 * `Spot.category`), por eso no se traducen. Para mostrar el nombre al usuario
 * se usa [labelRes], que resuelve el texto según el idioma del dispositivo
 * (guía 14).
 */
object SpotCategories {

    /** Valor del filtro "sin filtro" en el feed. */
    const val ALL = "Todos"

    const val NATURALEZA = "Naturaleza"
    const val CAFE = "Café"
    const val GASTRONOMIA = "Gastronomía"
    const val HISTORIA = "Historia"
    const val CULTURA = "Cultura"
    const val OCIO = "Ocio"

    /** Orden en el que se muestran las 6 categorías reales. */
    val all: List<String> = listOf(
        NATURALEZA,
        CAFE,
        GASTRONOMIA,
        HISTORIA,
        CULTURA,
        OCIO
    )

    /** Lista lista para los chips del feed (incluye "Todos" al inicio). */
    val filterOptions: List<String> = listOf(ALL) + all

    /** Emoji representativo de cada categoría. */
    val emojis: Map<String, String> = mapOf(
        NATURALEZA to "🏞️",
        CAFE to "☕",
        GASTRONOMIA to "🍽️",
        HISTORIA to "🏛️",
        CULTURA to "🎭",
        OCIO to "🎡"
    )

    fun emoji(category: String): String = emojis[category] ?: "📍"

    /**
     * Recurso con el nombre traducido de la categoría.
     * Si la categoría no está registrada, se devuelve [R.string.category_all].
     */
    @StringRes
    fun labelRes(category: String): Int = when (category) {
        ALL -> R.string.category_all
        NATURALEZA -> R.string.category_naturaleza
        CAFE -> R.string.category_cafe
        GASTRONOMIA -> R.string.category_gastronomia
        HISTORIA -> R.string.category_historia
        CULTURA -> R.string.category_cultura
        OCIO -> R.string.category_ocio
        else -> R.string.category_all
    }

    /** Etiqueta con emoji para mostrar en la ficha del lugar. */
    fun labeled(category: String): String = "${emoji(category)} $category"
}
