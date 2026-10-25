package com.example.ejespot.features.dashboard.profile

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.domain.model.AppLanguage
import com.example.ejespot.features.dashboard.profile.components.ProfileTopBar
import com.example.ejespot.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LanguageScreen(
    padding: PaddingValues = PaddingValues(),
    viewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState? = null,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val languages = remember { ProfileSampleData.languages }
    val c = ejeSpotColors()
    val context = LocalContext.current

    // Mensaje de confirmación por cada idioma, resuelto en el contexto
    // @Composable para poder mostrarlo desde la corrutina del snackbar.
    val selectedMessages: Map<AppLanguage, String> = languages.associateWith { language ->
        stringResource(R.string.language_selected_snackbar, language.nativeName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
            .verticalScroll(rememberScrollState())
    ) {
        ProfileTopBar(title = stringResource(R.string.language_title), onBack = onNavigateBack)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(R.string.language_choose),
                fontFamily = ManropeFontFamily,
                fontSize = 12.5.sp,
                color = c.textSoft
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = c.surface,
                border = BorderStroke(1.dp, c.outline),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    languages.forEachIndexed { index, language ->
                        val isSelected = language.code == uiState.selectedLanguage.code
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectLanguage(language)
                                    coroutineScope.launch {
                                        snackbarHostState?.showSnackbar(
                                            selectedMessages[language] ?: language.nativeName
                                        )
                                    }
                                    // En Android 12 y anteriores el idioma se aplica
                                    // sobre el contexto de la Activity, así que hay
                                    // que recrearla para que cambien los textos.
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                        (context as? Activity)?.recreate()
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (isSelected) c.successBg else c.surfaceVariant,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = language.flag, fontSize = 17.sp)
                            }

                            Spacer(modifier = Modifier.width(11.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = language.name,
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = if (isSelected) EjeSpotPrimary else c.text
                                )
                                Text(
                                    text = language.nativeName,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.sp,
                                    color = c.placeholder
                                )
                            }

                            if (isSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = EjeSpotPrimary,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = stringResource(R.string.language_selected_description),
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(c.surface, CircleShape)
                                        .border(1.dp, c.outline, CircleShape)
                                )
                            }
                        }

                        if (index < languages.lastIndex) {
                            HorizontalDivider(
                                color = c.surfaceVariant,
                                modifier = Modifier.padding(start = 61.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.language_note),
                fontFamily = ManropeFontFamily,
                fontSize = 10.5.sp,
                color = c.placeholder,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
