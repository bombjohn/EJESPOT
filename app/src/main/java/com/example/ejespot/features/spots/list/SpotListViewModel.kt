package com.example.ejespot.features.spots.list

import androidx.lifecycle.ViewModel
import com.example.ejespot.R
import com.example.ejespot.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SpotListViewModel : ViewModel() {

    private val sampleSpots = listOf(
        Spot(
            id = "1",
            name = "Mirador Valle de Cocora",
            category = "Naturaleza",
            location = "Salento, Quindío",
            rating = 4.9,
            reviewCount = 312,
            distance = "A 2.1 km",
            isVerified = true,
            priceBadge = "Gratuito",
            hours = "6:00 a.m. - 4:00 p.m.",
            priceRange = "Gratuito",
            description = "Sendero de 11 km entre las palmas de cera más altas del mundo, símbolo del Eje Cafetero. Ideal para caminata de dificultad media. Llevar ropa de abrigo e impermeable para la niebla.",
            addedBy = "@caminante_rosa",
            imageResId = R.drawable.spot_valle_cocora,
            technicalTitle = "FICHA TÉCNICA DE SENDERISMO",
            technicalInfo = SpotTechnicalInfo(
                item1Label = "Dificultad",
                item1Value = "Media (11 km)",
                item2Label = "Altitud",
                item2Value = "2.400 m.s.n.m.",
                item3Label = "Clima",
                item3Value = "Templado (15°C)",
                item4Label = "Equipamiento",
                item4Value = "Botas y capa impermeable"
            ),
            highlightsTitle = "Atractivos Destacados",
            highlights = listOf(
                SpotHighlight(
                    title = "Bosque de Palmas de Cera",
                    subtitle = "Palmas de hasta 60m de altura en laderas verdes",
                    iconEmoji = "🌴"
                ),
                SpotHighlight(
                    title = "Casa de los Colibríes (Acaime)",
                    subtitle = "Refugio de aves silvestres y chocolate caliente",
                    iconEmoji = "🦜"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_1",
                    authorName = "María José Restrepo",
                    authorBadge = "👑 Embajadora Local",
                    avatarInitials = "MJ",
                    rating = 5,
                    comment = "El amanecer entre las palmas de cera es imperdible. Fui temprano (6:30 a.m.) y casi no había turistas. La luz sobre la cordillera es mágica."
                ),
                SpotReview(
                    id = "rev_2",
                    authorName = "Daniel Toro",
                    authorBadge = "🥾 Aventurero",
                    avatarInitials = "DT",
                    rating = 5,
                    comment = "El sendero completo toma casi 5 horas hasta la casa de los colibríes. Totalmente recomendado llevar botas de montaña y efectivo local."
                )
            )
        ),
        Spot(
            id = "2",
            name = "Simón Simón Campestre",
            category = "Gastronomía",
            location = "Pereira, Risaralda",
            rating = 4.8,
            reviewCount = 184,
            distance = "A 5.4 km",
            isVerified = true,
            priceBadge = "Moderado ($$)",
            hours = "8:00 AM - 7:00 PM",
            priceRange = "$15.000 - $45.000",
            description = "Experiencia gastronómica y cafetera integral. Disfruta de gastronomía típica del Quindío con insumos locales y recorrido guiado de catación de café de origen.",
            addedBy = "@gourmet_pereira",
            imageResId = R.drawable.spot_simon_simon,
            technicalTitle = "INFORMACIÓN GASTRONÓMICA",
            technicalInfo = SpotTechnicalInfo(
                item1Label = "Horario",
                item1Value = "8:00 AM - 7:00 PM",
                item2Label = "Rango",
                item2Value = "$15.000 - $45.000",
                item3Label = "Tipo",
                item3Value = "Tradicional & Café",
                item4Label = "Reserva",
                item4Value = "Recomendada"
            ),
            specialtiesTitle = "Especialidades de la Casa",
            specialties = listOf(
                SpotSpecialty(
                    name = "Trucha al Ajillo con Patacón Gigante",
                    description = "Plato insignia de la zona · Salento & Pereira",
                    price = "$38.000",
                    iconEmoji = "🐟"
                ),
                SpotSpecialty(
                    name = "Tour + Catación de Café Especial",
                    description = "Recorrido de 45 min por el cafetal con barista",
                    price = "$25.000",
                    iconEmoji = "☕"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_3",
                    authorName = "Valentina Ospina",
                    authorBadge = "☕ Coffee Lover",
                    avatarInitials = "VO",
                    rating = 5,
                    comment = "La trucha con patacón es increíble y la vista a los cafetales insuperable. El café de origen al final fue el toque perfecto."
                ),
                SpotReview(
                    id = "rev_4",
                    authorName = "Felipe Arango",
                    authorBadge = "🍲 Sibarita Caldense",
                    avatarInitials = "FA",
                    rating = 5,
                    comment = "Muy buen servicio al cliente, comida abundante y fresca. Ambiente 100% campestre ideal para ir en familia."
                )
            )
        ),
        Spot(
            id = "3",
            name = "Plaza de Bolívar, Manizales",
            category = "Historia",
            location = "Manizales, Caldas",
            rating = 4.7,
            reviewCount = 240,
            distance = "A 12 km",
            isVerified = true,
            priceBadge = "Gratuito",
            hours = "Abierto 24 Horas",
            priceRange = "Gratuito",
            description = "Centro histórico y cultural de Manizales con la imponente Catedral Basílica Metropolitana (la más alta de Colombia con 106 m) y el icónico monumento al Bolívar Cóndor de Rodrigo Arenas Betancourt.",
            addedBy = "@manizales_vivo",
            imageResId = R.drawable.spot_plaza_bolivar,
            technicalTitle = "INFORMACIÓN PATRIMONIAL",
            technicalInfo = SpotTechnicalInfo(
                item1Label = "Altitud",
                item1Value = "2.150 m.s.n.m.",
                item2Label = "Estilo",
                item2Value = "Neogótico y Republicano",
                item3Label = "Clima",
                item3Value = "Templado-Frío (17°C)",
                item4Label = "Acceso",
                item4Value = "Peatonal céntrico"
            ),
            highlightsTitle = "Atractivos del Entorno",
            highlights = listOf(
                SpotHighlight(
                    title = "Catedral Basílica Metropolitana",
                    subtitle = "Tour al Corredor Polaco a más de 100 metros de altura",
                    iconEmoji = "⛪"
                ),
                SpotHighlight(
                    title = "Monumento Bolívar Cóndor",
                    subtitle = "Obra maestra en bronce del escultor Rodrigo Arenas Betancourt",
                    iconEmoji = "🦅"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_5",
                    authorName = "Sebastián Henao",
                    authorBadge = "🏛️ Guía Patrimonial",
                    avatarInitials = "SH",
                    rating = 5,
                    comment = "Subir al Corredor Polaco de la Catedral es una experiencia obligatoria. La panorámica de Manizales y los nevados no tiene comparación."
                )
            )
        )
    )

    private val samplePendingSpots = listOf(
        Spot(
            id = "4",
            name = "Cascada Los Frailes, Pereira",
            category = "Naturaleza",
            location = "Pereira, Risaralda",
            rating = 4.8,
            reviewCount = 210,
            distance = "A 8.3 km",
            isVerified = false,
            priceBadge = "Gratuito",
            hours = "7:00 AM - 4:00 PM",
            priceRange = "Gratuito",
            description = "Reserva natural protegida con ruta de senderismo ecológico entre bosque de niebla que culmina en una impresionante caída de agua natural de 70 metros de altura.",
            addedBy = "@eco_explorador",
            imageResId = R.drawable.spot_cascada_frailes,
            isPending = true,
            technicalTitle = "FICHA TÉCNICA DE SENDERISMO",
            technicalInfo = SpotTechnicalInfo(
                item1Label = "Dificultad",
                item1Value = "Moderada (2.5 km)",
                item2Label = "Altitud",
                item2Value = "1.850 m.s.n.m.",
                item3Label = "Clima",
                item3Value = "Templado (19°C)",
                item4Label = "Equipamiento",
                item4Value = "Calzado de agarre y agua"
            ),
            highlightsTitle = "Atractivos Destacados",
            highlights = listOf(
                SpotHighlight(
                    title = "Caída de Agua & Poza Natural",
                    subtitle = "Apta para baño recreativo supervisado",
                    iconEmoji = "🌊"
                ),
                SpotHighlight(
                    title = "Avistamiento de Aves Autóctonas",
                    subtitle = "Hábitat del Tucán Esmeralda y Colibríes",
                    iconEmoji = "🦜"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_6",
                    authorName = "Carolina Gómez",
                    authorBadge = "🌿 Senderista",
                    avatarInitials = "CG",
                    rating = 5,
                    comment = "El sonido del agua durante todo el camino te conecta con la naturaleza. Lleven calzado con buen agarre porque hay piedras húmedas."
                )
            )
        )
    )

    private val _allSpots = MutableStateFlow(sampleSpots)
    private val _allPendingSpots = MutableStateFlow(samplePendingSpots)

    private val _uiState = MutableStateFlow(
        SpotListUiState(
            spots = sampleSpots,
            pendingSpots = samplePendingSpots
        )
    )
    val uiState: StateFlow<SpotListUiState> = _uiState.asStateFlow()

    fun onCategorySelected(category: String) {
        _uiState.update { current ->
            current.copy(
                selectedCategory = category,
                spots = if (category == "Todos") _allSpots.value else _allSpots.value.filter { it.category == category }
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                spots = if (query.isBlank()) {
                    if (current.selectedCategory == "Todos") _allSpots.value else _allSpots.value.filter { it.category == current.selectedCategory }
                } else {
                    _allSpots.value.filter {
                        it.name.contains(query, ignoreCase = true) || it.location.contains(query, ignoreCase = true)
                    }
                }
            )
        }
    }

    fun getSpotById(id: String): Spot? {
        return (_allSpots.value + _allPendingSpots.value).find { it.id == id }
    }

    fun addReview(spotId: String, newReview: SpotReview) {
        val updatedSpots = _allSpots.value.map { spot ->
            if (spot.id == spotId) {
                val updatedReviews = listOf(newReview) + spot.reviews
                val newReviewCount = spot.reviewCount + 1
                spot.copy(reviews = updatedReviews, reviewCount = newReviewCount)
            } else spot
        }
        val updatedPending = _allPendingSpots.value.map { spot ->
            if (spot.id == spotId) {
                val updatedReviews = listOf(newReview) + spot.reviews
                val newReviewCount = spot.reviewCount + 1
                spot.copy(reviews = updatedReviews, reviewCount = newReviewCount)
            } else spot
        }
        _allSpots.value = updatedSpots
        _allPendingSpots.value = updatedPending
        _uiState.update { current ->
            current.copy(
                spots = if (current.selectedCategory == "Todos") updatedSpots else updatedSpots.filter { it.category == current.selectedCategory },
                pendingSpots = updatedPending
            )
        }
    }
}

