package com.example.ejespot.features.dashboard.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.features.dashboard.profile.components.ProfileTopBar
import com.example.ejespot.ui.theme.*

enum class LegalDocument(
    val titleRes: Int,
    val introRes: Int,
    val lastUpdateRes: Int
) {
    TERMS(
        titleRes = R.string.legal_terms,
        introRes = R.string.legal_terms_intro,
        lastUpdateRes = R.string.legal_last_update
    ),
    PRIVACY(
        titleRes = R.string.legal_privacy,
        introRes = R.string.legal_privacy_intro,
        lastUpdateRes = R.string.legal_last_update
    )
}

private data class LegalSectionRes(
    val headingRes: Int,
    val bodyRes: Int
)

private val termsSections = listOf(
    LegalSectionRes(
        headingRes = R.string.legal_terms_sec1_heading,
        bodyRes = R.string.legal_terms_sec1_body
    ),
    LegalSectionRes(
        headingRes = R.string.legal_terms_sec2_heading,
        bodyRes = R.string.legal_terms_sec2_body
    ),
    LegalSectionRes(
        headingRes = R.string.legal_terms_sec3_heading,
        bodyRes = R.string.legal_terms_sec3_body
    ),
    LegalSectionRes(
        headingRes = R.string.legal_terms_sec4_heading,
        bodyRes = R.string.legal_terms_sec4_body
    ),
    LegalSectionRes(
        headingRes = R.string.legal_terms_sec5_heading,
        bodyRes = R.string.legal_terms_sec5_body
    )
)

private val privacySections = listOf(
    LegalSectionRes(
        headingRes = R.string.legal_privacy_sec1_heading,
        bodyRes = R.string.legal_privacy_sec1_body
    ),
    LegalSectionRes(
        headingRes = R.string.legal_privacy_sec2_heading,
        bodyRes = R.string.legal_privacy_sec2_body
    ),
    LegalSectionRes(
        headingRes = R.string.legal_privacy_sec3_heading,
        bodyRes = R.string.legal_privacy_sec3_body
    ),
    LegalSectionRes(
        headingRes = R.string.legal_privacy_sec4_heading,
        bodyRes = R.string.legal_privacy_sec4_body
    ),
    LegalSectionRes(
        headingRes = R.string.legal_privacy_sec5_heading,
        bodyRes = R.string.legal_privacy_sec5_body
    )
)

@Composable
fun LegalScreen(
    document: LegalDocument,
    padding: PaddingValues = PaddingValues(),
    onNavigateBack: () -> Unit
) {
    val sections = if (document == LegalDocument.TERMS) termsSections else privacySections
    val c = ejeSpotColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
    ) {
        ProfileTopBar(title = stringResource(document.titleRes), onBack = onNavigateBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(bottom = 4.dp)) {
                    Text(
                        text = stringResource(document.lastUpdateRes),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.5.sp,
                        color = c.placeholder
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(document.introRes),
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.5.sp,
                        color = c.textMuted,
                        lineHeight = 18.sp
                    )
                }
            }

            itemsIndexed(sections) { index, section ->
                Surface(
                    shape = RoundedCornerShape(15.dp),
                    color = c.surface,
                    border = BorderStroke(1.dp, c.outlineSoft),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = EjeSpotPrimaryContainer
                            ) {
                                Box(
                                    modifier = Modifier.size(23.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontFamily = ManropeFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.5.sp,
                                        color = EjeSpotPrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(9.dp))
                            Text(
                                text = stringResource(section.headingRes),
                                fontFamily = FrauncesFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = c.text,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(section.bodyRes),
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            color = c.textSoft,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = c.surfaceVariant,
                    border = BorderStroke(1.dp, c.outlineSoft),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(13.dp)) {
                        Text(
                            text = stringResource(R.string.legal_help_title),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = c.text
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = stringResource(R.string.legal_help_message),
                            fontFamily = ManropeFontFamily,
                            fontSize = 11.5.sp,
                            color = c.textSoft,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary)
                ) {
                    Text(
                        text = stringResource(R.string.legal_understood),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.legal_footer),
                    fontFamily = ManropeFontFamily,
                    fontSize = 10.sp,
                    color = c.placeholder,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
