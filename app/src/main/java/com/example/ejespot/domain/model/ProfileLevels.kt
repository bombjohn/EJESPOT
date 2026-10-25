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
object ProfileLevels {

    const val TURISTA = "Turista"
    const val AVENTURERO = "Aventurero"
    const val AVENTURERA = "Aventurera"
    const val EXPLORADOR_CAFETERO = "Explorador Cafetero"
    const val EMBAJADOR_LOCAL = "Embajador Local"
    const val LEYENDA_EJE = "Leyenda del Eje"

    /** Recurso con el nombre del nivel traducido. */
    @StringRes
    fun labelRes(name: String): Int = when (name) {
        TURISTA -> R.string.level_turista
        AVENTURERO, AVENTURERA -> R.string.level_aventurero
        EXPLORADOR_CAFETERO -> R.string.level_explorador_cafetero
        EMBAJADOR_LOCAL -> R.string.level_embajador_local
        LEYENDA_EJE -> R.string.level_leyenda_eje
        else -> R.string.level_turista
    }
}
