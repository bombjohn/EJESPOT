package com.example.ejespot.features.splash.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.ui.theme.EjeSpotGold
import com.example.ejespot.ui.theme.EjeSpotPrimary
import com.example.ejespot.ui.theme.EjeSpotSplashDark
import com.example.ejespot.ui.theme.EjeSpotSplashMid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "splashAlpha"
    )

    // Espera la restauración de la sesión (Data Store) antes de decidir la
    // ruta inicial: si el usuario ya estaba conectado, debe entrar directo.
    val finish: () -> Unit = {
        coroutineScope.launch {
            SessionManager.awaitRestore()
            if (!finished) {
                finished = true
                onSplashFinished()
            }
        }
    }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        EjeSpotPrimary,
                        EjeSpotSplashMid,
                        EjeSpotSplashDark
                    )
                )
            )
            .clickable { finish() }, // Permite saltar al tocar
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Oficial
            Image(
                painter = painterResource(id = R.drawable.ic_logo_ejespot),
                contentDescription = stringResource(R.string.splash_logo_description),
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp)
            )

            // Nombre de Marca "Eje Spot"
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("Eje ")
                    }
                    withStyle(style = SpanStyle(color = EjeSpotGold)) {
                        append("Spot")
                    }
                },
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtítulo
            Text(
                text = stringResource(R.string.splash_tagline),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Dots indicadores
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(EjeSpotGold, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.White.copy(alpha = 0.35f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.White.copy(alpha = 0.35f), CircleShape)
                )
            }
        }

        // Región al pie
        Text(
            text = stringResource(R.string.splash_region),
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        )
    }
}
