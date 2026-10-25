package com.example.ejespot.features.dashboard.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.features.dashboard.profile.components.ProfileCard
import com.example.ejespot.features.dashboard.profile.components.ProfileMenuRow
import com.example.ejespot.features.dashboard.profile.components.ProfileSwitchRow
import com.example.ejespot.features.dashboard.profile.components.ProfileTopBar
import com.example.ejespot.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    padding: PaddingValues = PaddingValues(),
    viewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState? = null,
    onNavigateBack: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit,
    onPermissions: () -> Unit,
    onLanguage: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val c = ejeSpotColors()
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val darkModeEnabledMessage = stringResource(R.string.settings_dark_mode_enabled)
    val darkModeDisabledMessage = stringResource(R.string.settings_dark_mode_disabled)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
            .verticalScroll(rememberScrollState())
    ) {
        ProfileTopBar(title = stringResource(R.string.settings_title), onBack = onNavigateBack)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SettingsSectionLabel(stringResource(R.string.settings_notifications))
            ProfileCard {
                ProfileSwitchRow(
                    emoji = "📍",
                    title = stringResource(R.string.settings_notif_nearby_title),
                    subtitle = stringResource(R.string.settings_notif_nearby_subtitle),
                    checked = uiState.settings.nearbyNotifications,
                    onCheckedChange = { viewModel.toggleNearbyNotifications() }
                )
                HorizontalDivider(color = c.surfaceVariant, modifier = Modifier.padding(start = 59.dp))
                ProfileSwitchRow(
                    emoji = "✅",
                    title = stringResource(R.string.settings_notif_proposal_title),
                    subtitle = stringResource(R.string.settings_notif_proposal_subtitle),
                    checked = uiState.settings.proposalNotifications,
                    onCheckedChange = { viewModel.toggleProposalNotifications() }
                )
                HorizontalDivider(color = c.surfaceVariant, modifier = Modifier.padding(start = 59.dp))
                ProfileSwitchRow(
                    emoji = "🏆",
                    title = stringResource(R.string.settings_notif_reward_title),
                    subtitle = stringResource(R.string.settings_notif_reward_subtitle),
                    checked = uiState.settings.rewardNotifications,
                    onCheckedChange = { viewModel.toggleRewardNotifications() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SettingsSectionLabel(stringResource(R.string.settings_section_privacy))
            ProfileCard {
                ProfileMenuRow(
                    emoji = "🛡️",
                    title = stringResource(R.string.settings_privacy_policy),
                    onClick = onPrivacyPolicy
                )
                HorizontalDivider(color = c.surfaceVariant, modifier = Modifier.padding(start = 59.dp))
                ProfileMenuRow(
                    emoji = "📄",
                    title = stringResource(R.string.settings_terms),
                    onClick = onTerms
                )
                HorizontalDivider(color = c.surfaceVariant, modifier = Modifier.padding(start = 59.dp))
                ProfileMenuRow(
                    emoji = "🔐",
                    title = stringResource(R.string.settings_app_permissions),
                    onClick = onPermissions
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SettingsSectionLabel(stringResource(R.string.settings_section_app))
            ProfileCard {
                ProfileMenuRow(
                    emoji = "🌐",
                    title = stringResource(R.string.settings_language),
                    value = uiState.selectedLanguage.name,
                    onClick = onLanguage
                )
                HorizontalDivider(color = c.surfaceVariant, modifier = Modifier.padding(start = 59.dp))
                ProfileSwitchRow(
                    emoji = "🌙",
                    title = stringResource(R.string.settings_dark_mode),
                    subtitle = stringResource(R.string.settings_dark_mode_subtitle),
                    checked = uiState.settings.darkMode,
                    onCheckedChange = {
                        val enabled = !uiState.settings.darkMode
                        viewModel.toggleDarkMode()
                        coroutineScope.launch {
                            snackbarHostState?.showSnackbar(
                                if (enabled) darkModeEnabledMessage else darkModeDisabledMessage
                            )
                        }
                    }
                )
                HorizontalDivider(color = c.surfaceVariant, modifier = Modifier.padding(start = 59.dp))
                ProfileMenuRow(
                    emoji = "📱",
                    title = stringResource(R.string.settings_app_version),
                    value = "1.0.0",
                    showChevron = false,
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SettingsSectionLabel(stringResource(R.string.settings_danger_zone), color = c.dangerText)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = c.surface,
                border = BorderStroke(1.dp, Color(0xFFE8C9C3)),
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileMenuRow(
                    emoji = "🗑️",
                    title = stringResource(R.string.settings_delete_account),
                    subtitle = stringResource(R.string.settings_delete_subtitle),
                    titleColor = c.dangerText,
                    onClick = { showDeleteDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.settings_footer),
                fontFamily = ManropeFontFamily,
                fontSize = 10.sp,
                color = c.placeholder,
                lineHeight = 15.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = c.surface,
            title = {
                Text(
                    text = stringResource(R.string.settings_delete_account),
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = c.dangerText
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.settings_delete_message, uiState.stats.reviews, uiState.stats.proposals),
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.sp,
                    color = c.textSoft,
                    lineHeight = 19.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAccount()
                    },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = c.dangerText)
                ) {
                    Text(
                        text = stringResource(R.string.settings_delete_account),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        text = stringResource(R.string.common_cancel),
                        fontFamily = ManropeFontFamily,
                        color = c.textSoft
                    )
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionLabel(text: String, color: Color = Color.Unspecified) {
    val c = ejeSpotColors()
    Text(
        text = text,
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 10.5.sp,
        letterSpacing = 0.6.sp,
        color = if (color == Color.Unspecified) c.textSoft else color,
        modifier = Modifier.padding(start = 2.dp, bottom = 7.dp)
    )
}
