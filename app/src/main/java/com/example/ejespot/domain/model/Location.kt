package com.example.ejespot.domain.model

/**
 * Coordenadas geográficas de un punto de interés (guía 09).
 *
 * Es un *value object*: dos ubicaciones con las mismas coordenadas son
 * iguales. La entidad principal (`Spot`) la usa como propiedad `location`, en
 * vez de tener los campos de latitud y longitud sueltos.
 */
data class Location(
    val latitude: Double,
    val longitude: Double
)
