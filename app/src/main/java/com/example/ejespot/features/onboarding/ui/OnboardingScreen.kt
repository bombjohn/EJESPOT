package com.example.ejespot.features.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ejespot.ui.theme.EjeSpotText
import com.example.ejespot.ui.theme.EjeSpotTextMuted

@Composable
fun OnboardingScreen(
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.7f))

        // Imagen circular con marco estilizado
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(EjeSpotPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_ejespot),
                contentDescription = "Ilustración Eje Cafetero",
                modifier = Modifier
                    .size(190.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Título Serif como el prototipo
        Text(
            text = "Descubre Joyas\nOcultas",
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
            text = "Explora el Eje Cafetero y encuentra lugares recomendados por la comunidad, desde miradores hasta cafés secretos.",
            fontSize = 15.sp,
            color = EjeSpotTextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Indicadores (dots)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(26.dp)
                    .height(8.dp)
                    .background(EjeSpotPrimary, RoundedCornerShape(4.dp))
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(EjeSpotOutline, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(EjeSpotOutline, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Botón "Continuar"
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary)
        ) {
            Text(
                text = "Continuar",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Enlace "Omitir tutorial"
        Text(
            text = "Omitir tutorial",
            color = EjeSpotPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable { onSkip() }
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
