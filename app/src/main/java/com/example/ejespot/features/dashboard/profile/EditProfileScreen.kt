package com.example.ejespot.features.dashboard.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.features.dashboard.profile.components.ProfileTopBar
import com.example.ejespot.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    padding: PaddingValues = PaddingValues(),
    viewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState? = null,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showLocationDialog by remember { mutableStateOf(false) }
    val c = ejeSpotColors()
    val photoSnackbarMessage = stringResource(R.string.edit_profile_photo_snackbar)
    val saveSnackbarMessage = stringResource(R.string.edit_profile_save_snackbar)

    LaunchedEffect(Unit) {
        viewModel.startEditing()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
            .verticalScroll(rememberScrollState())
    ) {
        ProfileTopBar(
            title = stringResource(R.string.edit_profile_title),
            onBack = onNavigateBack,
            trailing = {
                TextButton(
                    onClick = { viewModel.startEditing() },
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.edit_profile_reset),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = EjeSpotPrimary
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Avatar editable (sin carga de imágenes en esta fase)
            Box(
                modifier = Modifier.size(104.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(c.avatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.draft.name.trim().takeIf { it.isNotBlank() }?.let { name ->
                            val words = name.split(Regex("\\s+")).filter { it.isNotBlank() }
                            when {
                                words.isEmpty() -> uiState.profile.initials
                                words.size == 1 -> words.first().take(2).uppercase()
                                else -> (words[0].take(1) + words[1].take(1)).uppercase()
                            }
                        } ?: uiState.profile.initials,
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 31.sp,
                        color = c.successText
                    )
                }

                Surface(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState?.showSnackbar(photoSnackbarMessage)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp),
                    shape = CircleShape,
                    color = EjeSpotPrimary,
                    border = BorderStroke(2.dp, c.background),
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = stringResource(R.string.edit_profile_photo_description),
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.edit_profile_photo_hint),
                fontFamily = ManropeFontFamily,
                fontSize = 10.5.sp,
                color = c.placeholder
            )

            Spacer(modifier = Modifier.height(22.dp))

            ProfileFormField(
                label = stringResource(R.string.edit_profile_name_label),
                value = uiState.draft.name,
                onValueChange = viewModel::onDraftNameChanged,
                placeholder = stringResource(R.string.edit_profile_name_placeholder)
            )

            Spacer(modifier = Modifier.height(15.dp))

            ProfileFormField(
                label = stringResource(R.string.edit_profile_username_label),
                value = uiState.draft.username,
                onValueChange = viewModel::onDraftUsernameChanged,
                placeholder = stringResource(R.string.edit_profile_username_placeholder)
            )

            Spacer(modifier = Modifier.height(15.dp))

            FieldLabel(stringResource(R.string.edit_profile_location_label))
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                onClick = { showLocationDialog = true },
                shape = RoundedCornerShape(14.dp),
                color = c.surface,
                border = BorderStroke(1.dp, c.outlineSoft),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📍",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.draft.location.ifBlank { stringResource(R.string.edit_profile_location_hint) },
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (uiState.draft.location.isBlank()) c.placeholder else c.text,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.edit_profile_location_open_description),
                        tint = c.placeholder,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Descripción breve con contador de caracteres
            FieldLabel(stringResource(R.string.edit_profile_bio_label))
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(108.dp)
                    .background(c.surface, RoundedCornerShape(14.dp))
                    .border(1.dp, c.outlineSoft, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                if (uiState.draft.bio.isEmpty()) {
                    Text(
                        text = stringResource(R.string.edit_profile_bio_placeholder),
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.5.sp,
                        color = c.placeholder,
                        lineHeight = 18.sp
                    )
                }
                BasicTextField(
                    value = uiState.draft.bio,
                    onValueChange = viewModel::onDraftBioChanged,
                    textStyle = TextStyle(
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = c.text,
                        lineHeight = 18.sp
                    ),
                    cursorBrush = SolidColor(EjeSpotPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(66.dp)
                )
                Text(
                    text = "${uiState.draft.bio.length}/160",
                    fontFamily = ManropeFontFamily,
                    fontSize = 9.5.sp,
                    color = c.placeholder,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }

            if (uiState.draftError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.edit_profile_error_prefix, uiState.draftError ?: ""),
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = c.dangerText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (viewModel.saveProfile()) {
                        coroutineScope.launch {
                            snackbarHostState?.showSnackbar(saveSnackbarMessage)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary)
            ) {
                Text(
                    text = stringResource(R.string.edit_profile_save),
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.edit_profile_memory_note),
                fontFamily = ManropeFontFamily,
                fontSize = 10.sp,
                color = c.placeholder,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = c.surface,
            title = {
                Text(
                    text = stringResource(R.string.edit_profile_location_dialog_title),
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = c.text
                )
            },
            text = {
                Column {
                    viewModel.availableLocations.forEach { location ->
                        val isSelected = location == uiState.draft.location
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onDraftLocationChanged(location)
                                    showLocationDialog = false
                                }
                                .padding(vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = location,
                                fontFamily = ManropeFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (isSelected) EjeSpotPrimary else c.text,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(R.string.edit_profile_location_selected_description),
                                    tint = EjeSpotPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text(
                        text = stringResource(R.string.common_close),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = c.textSoft
                    )
                }
            }
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    val c = ejeSpotColors()
    Text(
        text = text,
        fontFamily = ManropeFontFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        color = c.placeholder,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun ProfileFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    val c = ejeSpotColors()
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldLabel(label)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(c.surface, RoundedCornerShape(14.dp))
                .border(1.dp, c.outlineSoft, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.sp,
                    color = c.placeholder
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.5.sp,
                    color = c.text
                ),
                cursorBrush = SolidColor(EjeSpotPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
