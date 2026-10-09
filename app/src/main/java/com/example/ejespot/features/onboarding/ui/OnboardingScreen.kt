package com.example.ejespot.features.onboarding.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLocationAlt
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.ui.theme.EjeSpotBackground
import com.example.ejespot.ui.theme.EjeSpotOutline
import com.example.ejespot.ui.theme.EjeSpotPrimary
import com.example.ejespot.ui.theme.EjeSpotSecondary
import com.example.ejespot.ui.theme.EjeSpotTertiary
import com.example.ejespot.ui.theme.EjeSpotText
import com.example.ejespot.ui.theme.EjeSpotTextMuted
import kotlinx.coroutines.launch

data class OnboardingStep(
    val title: String,
    val description: String,
    val icon: ImageVector? = null,
    val useLogo: Boolean = false,
    val accentColor: Color
)

private val onboardingSteps = listOf(
    OnboardingStep(
        title = "Descubre Joyas\nOcultas",
        description = "Explora el Eje Cafetero y encuentra lugares recomendados por la comunidad, desde miradores hasta cafés secretos.",
        useLogo = true,
        accentColor = EjeSpotPrimary
    ),
    OnboardingStep(
        title = "Comparte tus\nLugares Favoritos",
        description = "¿Conoces un rincón mágico en Caldas, Quindío o Risaralda? Publica fotos, recomendaciones y guía a otros viajeros.",
        icon = Icons.Rounded.AddLocationAlt,
        accentColor = EjeSpotSecondary
    ),
    OnboardingStep(
        title = "Únete a la\nComunidad Cafetera",
        description = "Deja reseñas auténticas, guarda tus spots preferidos y suma puntos de experiencia como explorador local.",
        icon = Icons.Rounded.Stars,
        accentColor = EjeSpotTertiary
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingSteps.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingSteps.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.5f))

        // Pager deslizable
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(3.5f)
        ) { page ->
            val step = onboardingSteps[page]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Ilustración circular
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(step.accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (step.useLogo) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo_ejespot),
                            contentDescription = "Logo EjeSpot",
                            modifier = Modifier
                                .size(190.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                    } else if (step.icon != null) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = step.accentColor,
                            modifier = Modifier.size(110.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Título
                Text(
                    text = step.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = EjeSpotText,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Descripción
                Text(
                    text = step.description,
                    fontSize = 15.sp,
                    color = EjeSpotTextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        // Indicadores (dots interactivos)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(onboardingSteps.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(if (isSelected) 26.dp else 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isSelected) onboardingSteps[pagerState.currentPage].accentColor
                            else EjeSpotOutline
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón "Continuar" / "Comenzar ahora"
        Button(
            onClick = {
                if (isLastPage) {
                    onContinue()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = onboardingSteps[pagerState.currentPage].accentColor
            )
        ) {
            Text(
                text = if (isLastPage) "Comenzar ahora" else "Continuar",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Enlace "Omitir tutorial"
        Text(
            text = if (isLastPage) "Ya tengo cuenta" else "Omitir tutorial",
            color = onboardingSteps[pagerState.currentPage].accentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable { onSkip() }
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
