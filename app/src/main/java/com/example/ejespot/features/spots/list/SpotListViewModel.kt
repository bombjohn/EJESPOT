package com.example.ejespot.features.spots.list

import androidx.lifecycle.ViewModel
import com.example.ejespot.R
import com.example.ejespot.domain.model.Spot
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
            distance = "2.1 km",
            isVerified = true,
            priceBadge = "Gratuito",
            hours = "6:00 AM - 4:00 PM",
            description = "Hogar de la majestuosa Palma de Cera, el árbol nacional de Colombia. Senderos ecológicos entre montañas verdes, miradores espectaculares y mirador de colibríes.",
            addedBy = "@caminante_rosa",
            imageResId = R.drawable.spot_valle_cocora
        ),
        Spot(
            id = "2",
            name = "Simón Simón Campestre",
            category = "Gastronomía",
            location = "Pereira, Risaralda",
            rating = 4.8,
            distance = "4.2 km",
            isVerified = true,
            priceBadge = "Moderado",
            hours = "11:30 AM - 9:00 PM",
            description = "Restaurante campestre con arquitectura típica cafetera en guadua, amplias zonas verdes y gastronomía tradicional fusión con hermosa vista panorámica.",
            addedBy = "@gourmet_pereira",
            imageResId = R.drawable.spot_simon_simon
        ),
        Spot(
            id = "3",
            name = "Plaza de Bolívar, Manizales",
            category = "Historia",
            location = "Manizales, Caldas",
            rating = 4.7,
            distance = "12 km",
            isVerified = true,
            priceBadge = "Gratuito",
            hours = "Abierto 24 Horas",
            description = "Centro histórico y cultural de Manizales con la imponente Catedral Basílica Metropolitana y el icónico monumento al Bolívar Cóndor de Rodrigo Arenas Betancourt.",
            addedBy = "@manizales_vivo",
            imageResId = R.drawable.spot_plaza_bolivar
        )
    )

    private val samplePendingSpots = listOf(
        Spot(
            id = "4",
            name = "Cascada Los Frailes, Pereira",
            category = "Naturaleza",
            location = "Pereira, Risaralda",
            rating = 4.8,
            distance = "15 km",
            isVerified = false,
            priceBadge = "Gratuito",
            hours = "7:00 AM - 4:00 PM",
            description = "Impresionante caída de agua natural de más de 70 metros ubicada en la cuenca alta del río Otún, dentro del Santuario de Flora y Fauna Otún Quimbaya.",
            addedBy = "@eco_explorador",
            imageResId = R.drawable.spot_cascada_frailes,
            isPending = true
        )
    )

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
                spots = if (category == "Todos") sampleSpots else sampleSpots.filter { it.category == category }
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                spots = if (query.isBlank()) sampleSpots else sampleSpots.filter {
                    it.name.contains(query, ignoreCase = true) || it.location.contains(query, ignoreCase = true)
                }
            )
        }
    }

    fun getSpotById(id: String): Spot? {
        return (sampleSpots + samplePendingSpots).find { it.id == id }
    }
}
