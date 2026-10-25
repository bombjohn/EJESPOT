package com.example.ejespot.core.data

import com.example.ejespot.R
import com.example.ejespot.domain.model.Location
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.domain.model.SpotHighlight
import com.example.ejespot.domain.model.SpotReview
import com.example.ejespot.domain.model.SpotSpecialty
import com.example.ejespot.domain.model.SpotTechnicalInfo

/**
 * Fuente única de puntos de interés quemados (Fase 2).
 *
 * Los lugares sin foto se dibujan con cabecera de color + emoji,
 * así que se pueden agregar tantos como haga falta sin archivos nuevos.
 *
 * El contenido visible al usuario (descripciones, horarios, precios, ficha
 * técnica, especialidades, atractivos y reseñas) se guarda como recursos de
 * `strings.xml` para que también exista en inglés (guía 14).
 *
 * Se conservan en texto plano porque son identificadores o nombres propios:
 * - `category`: identificador comparado con [com.example.ejespot.domain.model.SpotCategories].
 * - `name`, `location`, `department`: nombres reales de lugares de Colombia.
 * - `addedBy`: alias de usuario (@caminante_rosa).
 * - `distance`: solo la medida ("2.1 km"); el preposición se traduce en la UI.
 */
object SpotSampleData {

    val spots: List<Spot> = listOf(
        Spot(
            id = "1",
            name = "Mirador Valle de Cocora",
            category = "Naturaleza",
            municipality = "Salento, Quindío",
            rating = 4.9,
            reviewCount = 312,
            distance = "2.1 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_free,
            hours = "",
            hoursRes = R.string.spot_1_hours,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_free,
            description = "",
            descriptionRes = R.string.spot_1_description,
            addedBy = "@caminante_rosa",
            imageResId = R.drawable.spot_valle_cocora,
            emoji = "🌴",
            accentColor = 0xFF2C5640,
            department = "Quindío",
            mapRelX = 0.50f,
            mapRelY = 0.22f,
            location = Location(4.6391, -75.4890),
            technicalTitleRes = R.string.spot_technical_title_hiking,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_difficulty,
                item1Value = R.string.spot_tech_value_medium_11km,
                item2Label = R.string.spot_tech_label_altitude,
                item2Value = R.string.spot_tech_value_alt_2400,
                item3Label = R.string.spot_tech_label_climate,
                item3Value = R.string.spot_tech_value_mild_15,
                item4Label = R.string.spot_tech_label_gear,
                item4Value = R.string.spot_tech_value_boots_raincoat
            ),
            highlightsTitleRes = R.string.spot_highlights_title_featured,
            highlights = listOf(
                SpotHighlight(
                    titleRes = R.string.spot_1_highlight_1_title,
                    subtitleRes = R.string.spot_1_highlight_1_subtitle,
                    iconEmoji = "🌴"
                ),
                SpotHighlight(
                    titleRes = R.string.spot_1_highlight_2_title,
                    subtitleRes = R.string.spot_1_highlight_2_subtitle,
                    iconEmoji = "🦜"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_1",
                    authorName = "María José Restrepo",
                    authorBadgeRes = R.string.review_badge_ambassador_local_f,
                    avatarInitials = "MJ",
                    rating = 5,
                    commentRes = R.string.review_comment_1
                ),
                SpotReview(
                    id = "rev_2",
                    authorName = "Daniel Toro",
                    authorBadgeRes = R.string.review_badge_adventurer,
                    avatarInitials = "DT",
                    rating = 5,
                    commentRes = R.string.review_comment_2
                ),
                SpotReview(
                    id = "rev_renata_1",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 5,
                    commentRes = R.string.review_comment_renata_1,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "2",
            name = "Simón Simón Campestre",
            category = "Gastronomía",
            municipality = "Pereira, Risaralda",
            rating = 4.8,
            reviewCount = 184,
            distance = "5.4 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_moderate,
            hours = "",
            hoursRes = R.string.spot_2_hours,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_15000_45000,
            description = "",
            descriptionRes = R.string.spot_2_description,
            addedBy = "@gourmet_pereira",
            imageResId = R.drawable.spot_simon_simon,
            emoji = "☕",
            accentColor = 0xFFB35A2B,
            department = "Risaralda",
            mapRelX = 0.65f,
            mapRelY = 0.42f,
            location = Location(4.8143, -75.6946),
            technicalTitleRes = R.string.spot_technical_title_gastronomy,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_schedule,
                item1Value = R.string.spot_tech_value_schedule_8am_7pm,
                item2Label = R.string.spot_tech_label_range,
                item2Value = R.string.spot_price_range_15000_45000,
                item3Label = R.string.spot_tech_label_type,
                item3Value = R.string.spot_tech_value_traditional_coffee,
                item4Label = R.string.spot_tech_label_booking,
                item4Value = R.string.spot_tech_value_recommended
            ),
            specialtiesTitleRes = R.string.spot_specialties_title_house,
            specialties = listOf(
                SpotSpecialty(
                    nameRes = R.string.spot_2_specialty_1_name,
                    descriptionRes = R.string.spot_2_specialty_1_description,
                    priceRes = R.string.spot_price_38000,
                    iconEmoji = "🐟"
                ),
                SpotSpecialty(
                    nameRes = R.string.spot_2_specialty_2_name,
                    descriptionRes = R.string.spot_2_specialty_2_description,
                    priceRes = R.string.spot_price_25000,
                    iconEmoji = "☕"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_3",
                    authorName = "Valentina Ospina",
                    authorBadgeRes = R.string.review_badge_coffee_lover,
                    avatarInitials = "VO",
                    rating = 5,
                    commentRes = R.string.review_comment_3
                ),
                SpotReview(
                    id = "rev_4",
                    authorName = "Felipe Arango",
                    authorBadgeRes = R.string.review_badge_gourmand,
                    avatarInitials = "FA",
                    rating = 5,
                    commentRes = R.string.review_comment_4
                ),
                SpotReview(
                    id = "rev_renata_2",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 5,
                    commentRes = R.string.review_comment_renata_2,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "3",
            name = "Plaza de Bolívar, Manizales",
            category = "Historia",
            municipality = "Manizales, Caldas",
            rating = 4.7,
            reviewCount = 240,
            distance = "12 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_free,
            hours = "",
            hoursRes = R.string.spot_hours_24h,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_free,
            description = "",
            descriptionRes = R.string.spot_3_description,
            addedBy = "@manizales_vivo",
            imageResId = R.drawable.spot_plaza_bolivar,
            emoji = "🏛️",
            accentColor = 0xFF8C5C26,
            department = "Caldas",
            mapRelX = 0.60f,
            mapRelY = 0.56f,
            location = Location(5.0683, -75.5177),
            technicalTitleRes = R.string.spot_technical_title_heritage,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_altitude,
                item1Value = R.string.spot_tech_value_alt_2150,
                item2Label = R.string.spot_tech_label_style,
                item2Value = R.string.spot_tech_value_neogothic_republican,
                item3Label = R.string.spot_tech_label_climate,
                item3Value = R.string.spot_tech_value_mild_cold_17,
                item4Label = R.string.spot_tech_label_access,
                item4Value = R.string.spot_tech_value_central_pedestrian
            ),
            highlightsTitleRes = R.string.spot_highlights_title_surroundings,
            highlights = listOf(
                SpotHighlight(
                    titleRes = R.string.spot_3_highlight_1_title,
                    subtitleRes = R.string.spot_3_highlight_1_subtitle,
                    iconEmoji = "⛪"
                ),
                SpotHighlight(
                    titleRes = R.string.spot_3_highlight_2_title,
                    subtitleRes = R.string.spot_3_highlight_2_subtitle,
                    iconEmoji = "🦅"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_5",
                    authorName = "Sebastián Henao",
                    authorBadgeRes = R.string.review_badge_heritage_guide,
                    avatarInitials = "SH",
                    rating = 5,
                    commentRes = R.string.review_comment_5
                ),
                SpotReview(
                    id = "rev_renata_3",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 5,
                    commentRes = R.string.review_comment_renata_3,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "5",
            name = "Finca Cafetera El Ocaso",
            category = "Café",
            municipality = "Salento, Quindío",
            rating = 4.8,
            reviewCount = 156,
            distance = "6.2 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_moderate,
            hours = "",
            hoursRes = R.string.spot_hours_9am_6pm,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_20000_45000,
            description = "",
            descriptionRes = R.string.spot_5_description,
            addedBy = "@ruta_cafe",
            emoji = "🌱",
            accentColor = 0xFF3E7A50,
            department = "Quindío",
            mapRelX = 0.44f,
            mapRelY = 0.30f,
            location = Location(4.6420, -75.5620),
            technicalTitleRes = R.string.spot_technical_title_farm,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_duration,
                item1Value = R.string.spot_tech_value_1h30,
                item2Label = R.string.spot_tech_label_altitude,
                item2Value = R.string.spot_tech_value_alt_1890,
                item3Label = R.string.spot_tech_label_includes,
                item3Value = R.string.spot_tech_value_tasting,
                item4Label = R.string.spot_tech_label_booking,
                item4Value = R.string.spot_tech_value_not_required
            ),
            specialtiesTitleRes = R.string.spot_specialties_title_farm,
            specialties = listOf(
                SpotSpecialty(
                    nameRes = R.string.spot_5_specialty_1_name,
                    descriptionRes = R.string.spot_5_specialty_1_description,
                    priceRes = R.string.spot_price_32000,
                    iconEmoji = "☕"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_8",
                    authorName = "Daniela Ríos",
                    authorBadgeRes = R.string.review_badge_coffee_lover,
                    avatarInitials = "DR",
                    rating = 5,
                    commentRes = R.string.review_comment_8,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "6",
            name = "Termales de Santa Rosa de Cabal",
            category = "Naturaleza",
            municipality = "Santa Rosa de Cabal, Risaralda",
            rating = 4.9,
            reviewCount = 421,
            distance = "24 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_moderate,
            hours = "",
            hoursRes = R.string.spot_6_hours,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_30000_60000,
            description = "",
            descriptionRes = R.string.spot_6_description,
            addedBy = "@termales_y_vida",
            emoji = "♨️",
            accentColor = 0xFF38637E,
            department = "Risaralda",
            mapRelX = 0.30f,
            mapRelY = 0.44f,
            location = Location(4.8650, -75.6100),
            technicalTitleRes = R.string.detail_technical_default,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_temperature,
                item1Value = R.string.spot_tech_value_temp_38_42,
                item2Label = R.string.spot_tech_label_altitude,
                item2Value = R.string.spot_tech_value_alt_2000,
                item3Label = R.string.spot_tech_label_services,
                item3Value = R.string.spot_tech_value_changing_rooms,
                item4Label = R.string.spot_tech_label_recommendation,
                item4Value = R.string.spot_tech_value_bring_towel
            ),
            highlights = listOf(
                SpotHighlight(
                    titleRes = R.string.spot_6_highlight_1_title,
                    subtitleRes = R.string.spot_6_highlight_1_subtitle,
                    iconEmoji = "♨️"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_9",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 5,
                    commentRes = R.string.review_comment_9,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "7",
            name = "Mirador de Filandia",
            category = "Historia",
            municipality = "Filandia, Quindío",
            rating = 4.8,
            reviewCount = 268,
            distance = "18 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_free,
            hours = "",
            hoursRes = R.string.spot_7_hours,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_free,
            description = "",
            descriptionRes = R.string.spot_7_description,
            addedBy = "@filandia_raiz",
            emoji = "🌅",
            accentColor = 0xFFB08968,
            department = "Quindío",
            mapRelX = 0.55f,
            mapRelY = 0.38f,
            location = Location(4.6750, -75.6380),
            technicalTitleRes = R.string.spot_technical_title_heritage,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_altitude,
                item1Value = R.string.spot_tech_value_alt_1923,
                item2Label = R.string.spot_tech_label_type,
                item2Value = R.string.spot_tech_value_viewpoint_architecture,
                item3Label = R.string.spot_tech_label_best_time,
                item3Value = R.string.spot_tech_value_5pm_6pm,
                item4Label = R.string.spot_tech_label_access,
                item4Value = R.string.spot_tech_value_pedestrian_town
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_10",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 5,
                    commentRes = R.string.review_comment_10,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "8",
            name = "Jardín Botánico del Quindío",
            category = "Naturaleza",
            municipality = "Calarcá, Quindío",
            rating = 4.6,
            reviewCount = 132,
            distance = "9.4 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_cheap,
            hours = "",
            hoursRes = R.string.spot_hours_9am_5pm,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_12000_18000,
            description = "",
            descriptionRes = R.string.spot_8_description,
            addedBy = "@calarca_natural",
            emoji = "🦋",
            accentColor = 0xFF6A994E,
            department = "Quindío",
            mapRelX = 0.40f,
            mapRelY = 0.50f,
            location = Location(4.5330, -75.6440),
            technicalTitleRes = R.string.spot_technical_title_general,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_area,
                item1Value = R.string.spot_tech_value_area_14ha,
                item2Label = R.string.spot_tech_label_altitude,
                item2Value = R.string.spot_tech_value_alt_1520,
                item3Label = R.string.spot_tech_label_duration,
                item3Value = R.string.spot_tech_value_2hours,
                item4Label = R.string.spot_tech_label_guide,
                item4Value = R.string.spot_tech_value_guide_included
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_11",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 4,
                    commentRes = R.string.review_comment_11,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "9",
            name = "Mirador de Chipre",
            category = "Historia",
            municipality = "Manizales, Caldas",
            rating = 4.9,
            reviewCount = 187,
            distance = "14 km",
            isVerified = false,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_free,
            hours = "",
            hoursRes = R.string.spot_hours_24h,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_free,
            description = "",
            descriptionRes = R.string.spot_9_description,
            addedBy = "@vista_caldas",
            emoji = "🌃",
            accentColor = 0xFF4A5B7A,
            department = "Caldas",
            mapRelX = 0.66f,
            mapRelY = 0.50f,
            location = Location(5.0530, -75.4900),
            reviews = listOf(
                SpotReview(
                    id = "rev_12",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 5,
                    commentRes = R.string.review_comment_12,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "10",
            name = "Café Jesús Martín",
            category = "Café",
            municipality = "Salento, Quindío",
            rating = 4.9,
            reviewCount = 302,
            distance = "12 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_cheap,
            hours = "",
            hoursRes = R.string.spot_hours_8am_7pm,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_6000_18000,
            description = "",
            descriptionRes = R.string.spot_10_description,
            addedBy = "@cafe_salento",
            emoji = "🌿",
            accentColor = 0xFF8A6D3B,
            department = "Quindío",
            mapRelX = 0.52f,
            mapRelY = 0.26f,
            location = Location(4.6380, -75.5710),
            specialtiesTitleRes = R.string.spot_specialties_title_recommended,
            specialties = listOf(
                SpotSpecialty(
                    nameRes = R.string.spot_10_specialty_1_name,
                    descriptionRes = R.string.spot_10_specialty_1_description,
                    priceRes = R.string.spot_price_9000,
                    iconEmoji = "☕"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_13",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 5,
                    commentRes = R.string.review_comment_13,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "11",
            name = "Salento Centro Histórico",
            category = "Historia",
            municipality = "Salento, Quindío",
            rating = 4.8,
            reviewCount = 214,
            distance = "11 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_free,
            hours = "",
            hoursRes = R.string.spot_hours_24h,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_free,
            description = "",
            descriptionRes = R.string.spot_11_description,
            addedBy = "@salento_vive",
            emoji = "🏘️",
            accentColor = 0xFFC08B5C,
            department = "Quindío",
            mapRelX = 0.62f,
            mapRelY = 0.58f,
            location = Location(4.6370, -75.5700),
            reviews = listOf(
                SpotReview(
                    id = "rev_14",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 5,
                    commentRes = R.string.review_comment_14,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "12",
            name = "Parque del Café",
            category = "Café",
            municipality = "Montenegro, Quindío",
            rating = 4.7,
            reviewCount = 512,
            distance = "22 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_moderate,
            hours = "",
            hoursRes = R.string.spot_hours_9am_6pm,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_45000_120000,
            description = "",
            descriptionRes = R.string.spot_12_description,
            addedBy = "@parque_cafe",
            emoji = "🎡",
            accentColor = 0xFF9C6644,
            department = "Quindío",
            mapRelX = 0.35f,
            mapRelY = 0.36f,
            location = Location(4.5430, -75.7700),
            technicalTitleRes = R.string.spot_technical_title_general,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_area,
                item1Value = R.string.spot_tech_value_area_126ha,
                item2Label = R.string.spot_tech_label_altitude,
                item2Value = R.string.spot_tech_value_alt_1450,
                item3Label = R.string.spot_tech_label_duration,
                item3Value = R.string.spot_tech_value_full_day,
                item4Label = R.string.spot_tech_label_attractions,
                item4Value = R.string.spot_tech_value_included_ticket
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_15",
                    authorName = "Andrés Mejía",
                    authorBadgeRes = R.string.review_badge_family_tourist,
                    avatarInitials = "AM",
                    rating = 5,
                    commentRes = R.string.review_comment_15
                )
            )
        ),
        Spot(
            id = "13",
            name = "Mirador El Silencio",
            category = "Naturaleza",
            municipality = "Pereira, Risaralda",
            rating = 4.5,
            reviewCount = 88,
            distance = "7.6 km",
            isVerified = false,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_free,
            hours = "",
            hoursRes = R.string.spot_13_hours,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_free,
            description = "",
            descriptionRes = R.string.spot_13_description,
            addedBy = "@pereira_senderos",
            emoji = "🌄",
            accentColor = 0xFF7A9E7E,
            department = "Risaralda",
            mapRelX = 0.28f,
            mapRelY = 0.30f,
            location = Location(4.8130, -75.7300),
            reviews = listOf(
                SpotReview(
                    id = "rev_16",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 4,
                    commentRes = R.string.review_comment_16,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "14",
            name = "Ruta del Café de Calarcá",
            category = "Café",
            municipality = "Calarcá, Quindío",
            rating = 4.7,
            reviewCount = 96,
            distance = "10 km",
            isVerified = false,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_cheap,
            hours = "",
            hoursRes = R.string.spot_hours_8am_5pm,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_18000_35000,
            description = "",
            descriptionRes = R.string.spot_14_description,
            addedBy = "@ruta_calarca",
            emoji = "🚜",
            accentColor = 0xFF7F5539,
            department = "Quindío",
            mapRelX = 0.38f,
            mapRelY = 0.62f,
            location = Location(4.5320, -75.6420),
            reviews = listOf(
                SpotReview(
                    id = "rev_17",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 5,
                    commentRes = R.string.review_comment_17,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "15",
            name = "Café Quindío Museo",
            category = "Café",
            municipality = "Armenia, Quindío",
            rating = 4.4,
            reviewCount = 118,
            distance = "26 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_cheap,
            hours = "",
            hoursRes = R.string.spot_hours_9am_6pm,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_10000_15000,
            description = "",
            descriptionRes = R.string.spot_15_description,
            addedBy = "@armenia_cultural",
            emoji = "🏛️",
            accentColor = 0xFF8C5C26,
            department = "Quindío",
            mapRelX = 0.30f,
            mapRelY = 0.66f,
            location = Location(4.5340, -75.6810),
            reviews = listOf(
                SpotReview(
                    id = "rev_18",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 4,
                    commentRes = R.string.review_comment_18,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "23",
            name = "Restaurante Los Nogales",
            category = "Gastronomía",
            municipality = "Manizales, Caldas",
            rating = 4.6,
            reviewCount = 127,
            distance = "13 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_moderate,
            hours = "",
            hoursRes = R.string.spot_23_hours,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_25000_60000,
            description = "",
            descriptionRes = R.string.spot_23_description,
            addedBy = "@gourmet_caldas",
            emoji = "🍽️",
            accentColor = 0xFF8C4A2F,
            department = "Caldas",
            mapRelX = 0.62f,
            mapRelY = 0.62f,
            location = Location(5.0560, -75.5030),
            technicalTitleRes = R.string.spot_technical_title_gastronomy,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_cuisine,
                item1Value = R.string.spot_tech_value_regional_cuisine,
                item2Label = R.string.spot_tech_label_range,
                item2Value = R.string.spot_price_range_25000_60000,
                item3Label = R.string.spot_tech_label_atmosphere,
                item3Value = R.string.spot_tech_value_family_country,
                item4Label = R.string.spot_tech_label_booking,
                item4Value = R.string.spot_tech_value_recommended
            ),
            specialtiesTitleRes = R.string.spot_specialties_title_house,
            specialties = listOf(
                SpotSpecialty(
                    nameRes = R.string.spot_23_specialty_1_name,
                    descriptionRes = R.string.spot_23_specialty_1_description,
                    priceRes = R.string.spot_price_42000,
                    iconEmoji = "🐟"
                ),
                SpotSpecialty(
                    nameRes = R.string.spot_23_specialty_2_name,
                    descriptionRes = R.string.spot_23_specialty_2_description,
                    priceRes = R.string.spot_price_16000,
                    iconEmoji = "🍮"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_26",
                    authorName = "Paula Andrea Cardona",
                    authorBadgeRes = R.string.review_badge_foodie,
                    avatarInitials = "PC",
                    rating = 5,
                    commentRes = R.string.review_comment_26,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "17",
            name = "Teatro Coliseo El Cable",
            category = "Cultura",
            municipality = "Manizales, Caldas",
            rating = 4.7,
            reviewCount = 143,
            distance = "12 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_cheap,
            hours = "",
            hoursRes = R.string.spot_hours_by_schedule,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_25000_70000,
            description = "",
            descriptionRes = R.string.spot_17_description,
            addedBy = "@cultura_caldas",
            emoji = "🎭",
            accentColor = 0xFF6A3D8F,
            department = "Caldas",
            mapRelX = 0.58f,
            mapRelY = 0.46f,
            location = Location(5.0640, -75.5140),
            technicalTitleRes = R.string.spot_technical_title_cultural,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_capacity,
                item1Value = R.string.spot_tech_value_capacity_1100,
                item2Label = R.string.spot_tech_label_type,
                item2Value = R.string.spot_tech_value_theater_concerts,
                item3Label = R.string.spot_tech_label_ticketing,
                item3Value = R.string.spot_tech_value_box_office_online,
                item4Label = R.string.spot_tech_label_parking,
                item4Value = R.string.spot_tech_value_nearby_zones
            ),
            highlightsTitleRes = R.string.spot_highlights_title_venue,
            highlights = listOf(
                SpotHighlight(
                    titleRes = R.string.spot_17_highlight_1_title,
                    subtitleRes = R.string.spot_17_highlight_1_subtitle,
                    iconEmoji = "🎭"
                ),
                SpotHighlight(
                    titleRes = R.string.spot_17_highlight_2_title,
                    subtitleRes = R.string.spot_17_highlight_2_subtitle,
                    iconEmoji = "🏛️"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_20",
                    authorName = "Laura Sofía Cardona",
                    authorBadgeRes = R.string.review_badge_theatre_lover,
                    avatarInitials = "LC",
                    rating = 5,
                    commentRes = R.string.review_comment_20,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "18",
            name = "Museo del Oro Quimbaya",
            category = "Cultura",
            municipality = "Armenia, Quindío",
            rating = 4.8,
            reviewCount = 197,
            distance = "27 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_free,
            hours = "",
            hoursRes = R.string.spot_18_hours,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_free,
            description = "",
            descriptionRes = R.string.spot_18_description,
            addedBy = "@armenia_cultural",
            emoji = "🏺",
            accentColor = 0xFF9C6B1E,
            department = "Quindío",
            mapRelX = 0.34f,
            mapRelY = 0.70f,
            location = Location(4.5450, -75.6650),
            technicalTitleRes = R.string.spot_technical_title_museum,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_collection,
                item1Value = R.string.spot_tech_value_quimbaya_silver,
                item2Label = R.string.spot_tech_label_entry,
                item2Value = R.string.spot_tech_value_free_entry,
                item3Label = R.string.spot_tech_label_duration,
                item3Value = R.string.spot_tech_value_1hour,
                item4Label = R.string.spot_tech_label_guided_tours,
                item4Value = R.string.spot_tech_value_every_2hours
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_21",
                    authorName = "Andrés Felipe Muñoz",
                    authorBadgeRes = R.string.review_badge_history_fan,
                    avatarInitials = "AM",
                    rating = 5,
                    commentRes = R.string.review_comment_21,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "19",
            name = "Casa de la Cultura de Salento",
            category = "Cultura",
            municipality = "Salento, Quindío",
            rating = 4.5,
            reviewCount = 76,
            distance = "11 km",
            isVerified = false,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_free,
            hours = "",
            hoursRes = R.string.spot_hours_9am_6pm,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_free,
            description = "",
            descriptionRes = R.string.spot_19_description,
            addedBy = "@salento_vive",
            emoji = "🎨",
            accentColor = 0xFFB5652F,
            department = "Quindío",
            mapRelX = 0.64f,
            mapRelY = 0.50f,
            location = Location(4.6365, -75.5705),
            reviews = listOf(
                SpotReview(
                    id = "rev_22",
                    authorName = "Camila Restrepo",
                    authorBadgeRes = R.string.review_badge_local_artist,
                    avatarInitials = "CR",
                    rating = 4,
                    commentRes = R.string.review_comment_22,
                    authorProfileId = "camila"
                )
            )
        ),
        Spot(
            id = "20",
            name = "Parque Recreacional del Café",
            category = "Ocio",
            municipality = "Pereira, Risaralda",
            rating = 4.6,
            reviewCount = 231,
            distance = "6.8 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_cheap,
            hours = "",
            hoursRes = R.string.spot_20_hours,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_8000_20000,
            description = "",
            descriptionRes = R.string.spot_20_description,
            addedBy = "@pereira_recrea",
            emoji = "🎡",
            accentColor = 0xFF2E7D6B,
            department = "Risaralda",
            mapRelX = 0.32f,
            mapRelY = 0.38f,
            location = Location(4.8060, -75.7200),
            technicalTitleRes = R.string.spot_technical_title_recreational,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_area,
                item1Value = R.string.spot_tech_value_area_12ha,
                item2Label = R.string.spot_tech_label_activities,
                item2Value = R.string.spot_tech_value_playground_lake_bike,
                item3Label = R.string.spot_tech_label_suitable_for,
                item3Value = R.string.spot_tech_value_families_kids,
                item4Label = R.string.spot_tech_label_food,
                item4Value = R.string.spot_tech_value_food_court
            ),
            highlightsTitleRes = R.string.spot_highlights_title_activities,
            highlights = listOf(
                SpotHighlight(
                    titleRes = R.string.spot_20_highlight_1_title,
                    subtitleRes = R.string.spot_20_highlight_1_subtitle,
                    iconEmoji = "⛵"
                ),
                SpotHighlight(
                    titleRes = R.string.spot_20_highlight_2_title,
                    subtitleRes = R.string.spot_20_highlight_2_subtitle,
                    iconEmoji = "🚴"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_23",
                    authorName = "Julián Andrés Marín",
                    authorBadgeRes = R.string.review_badge_family_plan,
                    avatarInitials = "JM",
                    rating = 5,
                    commentRes = R.string.review_comment_23,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "21",
            name = "Finca de Aventura San Vicente",
            category = "Ocio",
            municipality = "Santa Rosa de Cabal, Risaralda",
            rating = 4.7,
            reviewCount = 158,
            distance = "25 km",
            isVerified = true,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_moderate,
            hours = "",
            hoursRes = R.string.spot_hours_9am_5pm,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_40000_90000,
            description = "",
            descriptionRes = R.string.spot_21_description,
            addedBy = "@aventura_risaralda",
            emoji = "🪂",
            accentColor = 0xFF38637E,
            department = "Risaralda",
            mapRelX = 0.26f,
            mapRelY = 0.58f,
            location = Location(4.8580, -75.6180),
            technicalTitleRes = R.string.spot_technical_title_adventure,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_duration,
                item1Value = R.string.spot_tech_value_half_day,
                item2Label = R.string.spot_tech_label_activities,
                item2Value = R.string.spot_tech_value_canopy_zipline,
                item3Label = R.string.spot_tech_label_minimum_age,
                item3Value = R.string.spot_tech_value_8years,
                item4Label = R.string.spot_tech_label_booking,
                item4Value = R.string.spot_tech_value_recommended
            ),
            specialtiesTitleRes = R.string.spot_specialties_title_adventure,
            specialties = listOf(
                SpotSpecialty(
                    nameRes = R.string.spot_21_specialty_1_name,
                    descriptionRes = R.string.spot_21_specialty_1_description,
                    priceRes = R.string.spot_price_65000,
                    iconEmoji = "🪂"
                ),
                SpotSpecialty(
                    nameRes = R.string.spot_21_specialty_2_name,
                    descriptionRes = R.string.spot_21_specialty_2_description,
                    priceRes = R.string.spot_price_45000,
                    iconEmoji = "🐎"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_24",
                    authorName = "Daniel Toro",
                    authorBadgeRes = R.string.review_badge_adventurer,
                    avatarInitials = "DT",
                    rating = 5,
                    commentRes = R.string.review_comment_24,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "22",
            name = "Mirador y Café Nocturno La Colina",
            category = "Ocio",
            municipality = "Filandia, Quindío",
            rating = 4.4,
            reviewCount = 92,
            distance = "19 km",
            isVerified = false,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_cheap,
            hours = "",
            hoursRes = R.string.spot_22_hours,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_10000_30000,
            description = "",
            descriptionRes = R.string.spot_22_description,
            addedBy = "@filandia_raiz",
            emoji = "🌙",
            accentColor = 0xFF4A3C7A,
            department = "Quindío",
            mapRelX = 0.56f,
            mapRelY = 0.34f,
            location = Location(4.6745, -75.6400),
            reviews = listOf(
                SpotReview(
                    id = "rev_25",
                    authorName = "Mateo Gómez",
                    authorBadgeRes = R.string.review_badge_night_plan,
                    avatarInitials = "MG",
                    rating = 4,
                    commentRes = R.string.review_comment_25,
                    authorProfileId = "mateo"
                )
            )
        )
    )

    /**
     * Puntos propuestos por la comunidad que todavía no están verificados.
     * El usuario puede verlos y marcarlos como visitados igual que los verificados.
     */
    val pendingSpots: List<Spot> = listOf(
        Spot(
            id = "4",
            name = "Cascada Los Frailes, Pereira",
            category = "Naturaleza",
            municipality = "Pereira, Risaralda",
            rating = 4.8,
            reviewCount = 210,
            distance = "8.3 km",
            isVerified = false,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_free,
            hours = "",
            hoursRes = R.string.spot_4_hours,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_free,
            description = "",
            descriptionRes = R.string.spot_4_description,
            addedBy = "@eco_explorador",
            imageResId = R.drawable.spot_cascada_frailes,
            emoji = "🌊",
            accentColor = 0xFF38637E,
            department = "Risaralda",
            mapRelX = 0.35f,
            mapRelY = 0.62f,
            location = Location(4.8250, -75.6900),
            isPending = true,
            technicalTitleRes = R.string.spot_technical_title_hiking,
            technicalInfo = SpotTechnicalInfo(
                item1Label = R.string.spot_tech_label_difficulty,
                item1Value = R.string.spot_tech_value_moderate_25km,
                item2Label = R.string.spot_tech_label_altitude,
                item2Value = R.string.spot_tech_value_alt_1850,
                item3Label = R.string.spot_tech_label_climate,
                item3Value = R.string.spot_tech_value_mild_19,
                item4Label = R.string.spot_tech_label_gear,
                item4Value = R.string.spot_tech_value_grippy_footwear_water
            ),
            highlightsTitleRes = R.string.spot_highlights_title_featured,
            highlights = listOf(
                SpotHighlight(
                    titleRes = R.string.spot_4_highlight_1_title,
                    subtitleRes = R.string.spot_4_highlight_1_subtitle,
                    iconEmoji = "🌊"
                ),
                SpotHighlight(
                    titleRes = R.string.spot_4_highlight_2_title,
                    subtitleRes = R.string.spot_4_highlight_2_subtitle,
                    iconEmoji = "🦜"
                )
            ),
            reviews = listOf(
                SpotReview(
                    id = "rev_6",
                    authorName = "Carolina Gómez",
                    authorBadgeRes = R.string.review_badge_hiker,
                    avatarInitials = "CG",
                    rating = 5,
                    commentRes = R.string.review_comment_6
                ),
                SpotReview(
                    id = "rev_renata_4",
                    authorName = "Renata Castaño",
                    authorBadgeRes = R.string.review_badge_explorer_f,
                    avatarInitials = "RC",
                    rating = 5,
                    commentRes = R.string.review_comment_renata_4,
                    authorProfileId = "renata"
                )
            )
        ),
        Spot(
            id = "16",
            name = "Cascada La Cristalina",
            category = "Naturaleza",
            municipality = "Salento, Quindío",
            rating = 4.7,
            reviewCount = 41,
            distance = "9.8 km",
            isVerified = false,
            priceBadge = "",
            priceBadgeRes = R.string.spot_price_badge_free,
            hours = "",
            hoursRes = R.string.spot_16_hours,
            priceRange = "",
            priceRangeRes = R.string.spot_price_range_free,
            description = "",
            descriptionRes = R.string.spot_16_description,
            addedBy = "@senderista_quindio",
            emoji = "💧",
            accentColor = 0xFF4E8098,
            department = "Quindío",
            mapRelX = 0.48f,
            mapRelY = 0.44f,
            location = Location(4.6400, -75.5500),
            isPending = true,
            reviews = listOf(
                SpotReview(
                    id = "rev_19",
                    authorName = "Julián Marín",
                    authorBadgeRes = R.string.review_badge_hiker,
                    avatarInitials = "JM",
                    rating = 5,
                    commentRes = R.string.review_comment_19,
                    authorProfileId = "renata"
                )
            )
        )
    )

    /** Todos los lugares disponibles (verificados + propuestos). */
    val allSpots: List<Spot> get() = spots + pendingSpots

    fun findById(id: String): Spot? = allSpots.firstOrNull { it.id == id }
}
