package com.example.ejespot.domain.model

import androidx.annotation.StringRes
import com.example.ejespot.R

/**
 * Niveles de la comunidad de EjeSpot.
 *
 * Igual que las categorías, el nombre del nivel es un **identificador de
 * datos** (se compara y se guarda), por eso no se traduce directamente. Para
 * mostrarlo al usuario se usa [labelRes], que devuelve el recurso con el
 * nombre ya traducido (guía 14).
 */
/**
 * Los 4 niveles oficiales de la comunidad de EjeSpot según el enunciado.
 */
object ProfileLevels {

    const val TURISTA = "Turista"
    const val AVENTURERO = "Aventurero"
    const val EXPLORADOR_CAFETERO = "Explorador Cafetero"
    const val EMBAJADOR_LOCAL = "Embajador Local"

    /** Los 4 niveles oficiales en orden de progreso. */
    val all = listOf(TURISTA, AVENTURERO, EXPLORADOR_CAFETERO, EMBAJADOR_LOCAL)

    /** Recurso con el nombre del nivel traducido. */
    @StringRes
    fun labelRes(name: String): Int = when (name) {
        TURISTA -> R.string.level_turista
        AVENTURERO, "Aventurera" -> R.string.level_aventurero
        EXPLORADOR_CAFETERO -> R.string.level_explorador_cafetero
        EMBAJADOR_LOCAL, "Leyenda del Eje" -> R.string.level_embajador_local
        else -> R.string.level_turista
    }
}
