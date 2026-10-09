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
            description = "Hogar de la majestuosa Palma de Cera, el árbol nacional de Colombia. Senderos ecológicos entre montañas nubosas y miradores espectaculares.",
            addedBy = "@caminante_rosa",
            imageResId = R.drawable.spot_valle_cocora
        ),
        Spot(
            id = "2",
            name = "Finca Café de la Sierra",
            category = "Gastronomía",
            location = "Filandia, Quindío",
            rating = 4.8,
            distance = "5.4 km",
            isVerified = true,
            priceBadge = "Moderado",
            hours = "8:00 AM - 7:00 PM",
            description = "Experiencia cafetera integral con barismo profesional, maridajes locales y vista panorámica a la cordillera central.",
            addedBy = "@barista_juan",
            imageResId = R.drawable.spot_cafe_sierra
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
            description = "Centro histórico de la ciudad con la imponente Catedral Basílica y el icónico monumento a Bolívar Cóndor de Rodrigo Arenas.",
            addedBy = "@manizales_vivo",
            imageResId = R.drawable.spot_plaza_bolivar
        )
    )

    private val samplePendingSpots = listOf(
        Spot(
            id = "4",
            name = "Cascada El Bosque, Pereira",
            category = "Naturaleza",
            location = "Pereira, Risaralda",
            rating = 4.7,
            distance = "12 km",
            isVerified = false,
            priceBadge = "Gratuito",
            hours = "7:00 AM - 5:00 PM",
            description = "Caída de agua natural escondida en la reserva La Pastora. Ideal para senderismo y baño recreativo.",
            addedBy = "@caminante_rosa",
            imageResId = R.drawable.spot_cascada_bosque,
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
