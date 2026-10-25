package com.example.ejespot.features.spots.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.core.ui.SpotImage
import com.example.ejespot.domain.model.Spot
import com.example.ejespot.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ReportSpotScreen(
    spot: Spot?,
    padding: PaddingValues = PaddingValues(),
    snackbarHostState: SnackbarHostState? = null,
    onNavigateBack: () -> Unit
) {
    val c = ejeSpotColors()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val reasons = listOf(
        stringResource(R.string.report_reason_wrong_info),
        stringResource(R.string.report_reason_gone),
        stringResource(R.string.report_reason_inappropriate),
        stringResource(R.string.report_reason_duplicate),
        stringResource(R.string.report_reason_other)
    )

    var selectedReason by remember(reasons) { mutableStateOf(reasons.first()) }
    var detailsText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    if (spot == null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.detail_spot_not_found), fontFamily = ManropeFontFamily)
        }
        return
    }

    Scaffold(
        containerColor = EjeSpotBackground,
        modifier = Modifier.padding(padding)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Barra Superior: Botón Volver + Título en Terracota "Reportar contenido"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = c.surface,
                    border = BorderStroke(1.dp, c.outlineSoft),
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = c.text,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.detail_report),
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    color = c.dangerText // Terracota oficial
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Sección 1: ¿QUÉ DESEAS REPORTAR?
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.report_what),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    color = c.textSoft
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mini tarjeta del spot
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = c.surface,
                    border = BorderStroke(1.dp, c.outlineSoft),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SpotImage(
                            spot = spot,
                            emojiSize = 22.dp,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = spot.name,
                                fontFamily = FrauncesFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = c.text
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.report_spot_subtitle, spot.municipality),
                                fontFamily = ManropeFontFamily,
                                fontSize = 11.5.sp,
                                color = c.placeholder
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sección 2: MOTIVO
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.report_reason_header),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    color = c.textSoft
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    reasons.forEach { reason ->
                        val isSelected = selectedReason == reason

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) Color(0xFFA85A32) else c.surface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFA85A32) else c.outlineSoft
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                        ) {
                            Text(
                                text = reason,
                                fontFamily = ManropeFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else c.text,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sección 3: CUÉNTANOS MÁS (OPCIONAL)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.report_details_label),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    color = c.textSoft
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .background(c.surface, RoundedCornerShape(14.dp))
                        .border(1.dp, c.outline, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    if (detailsText.isEmpty()) {
                        Text(
                            text = stringResource(R.string.report_details_placeholder),
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            color = c.placeholder
                        )
                    }
                    BasicTextField(
                        value = detailsText,
                        onValueChange = { detailsText = it },
                        textStyle = TextStyle(
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            color = c.text
                        ),
                        cursorBrush = SolidColor(EjeSpotPrimary),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta informativa: Un Embajador Local revisará su reporte
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = c.surfaceVariant,
                border = BorderStroke(1.dp, c.outlineSoft),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "ℹ️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.report_ambassador_note),
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.5.sp,
                        color = c.textSoft,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón: Enviar reporte
            val reportSuccessMessage = stringResource(R.string.report_success)
            Button(
                onClick = {
                    isSubmitting = true
                    coroutineScope.launch {
                        snackbarHostState?.showSnackbar(reportSuccessMessage)
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E382B)),
                enabled = !isSubmitting
            ) {
                Text(
                    text = stringResource(R.string.report_submit),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
