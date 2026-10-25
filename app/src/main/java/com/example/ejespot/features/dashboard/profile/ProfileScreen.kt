package com.example.ejespot.features.dashboard.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.domain.model.LevelStatus
import com.example.ejespot.domain.model.ProfileLevels
import com.example.ejespot.features.dashboard.profile.components.*
import com.example.ejespot.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    padding: PaddingValues = PaddingValues(),
    viewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState? = null,
    onEditProfile: () -> Unit,
    onNotifications: () -> Unit,
    onSettings: () -> Unit,
    onGamification: () -> Unit,
    onMyReviews: () -> Unit,
    onMyProposals: () -> Unit,
    onNavigateToVisited: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showModerationDialog by remember { mutableStateOf(false) }
    val c = ejeSpotColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar + acción editar perfil
        Box(
            modifier = Modifier.size(96.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(c.avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.profile.initials,
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = c.successText
                )
            }

            Surface(
                onClick = onEditProfile,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(30.dp),
                shape = CircleShape,
                color = EjeSpotPrimary,
                border = BorderStroke(2.dp, c.background),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.profile_edit_avatar_description),
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = uiState.profile.name,
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            color = c.text,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(7.dp))

        Surface(
            shape = RoundedCornerShape(100.dp),
            color = EjeSpotPrimaryContainer,
            border = BorderStroke(1.dp, c.successBg)
        ) {
            Text(
                text = stringResource(
                    R.string.profile_level_pill,
                    uiState.profile.level,
                    stringResource(ProfileLevels.labelRes(uiState.profile.levelName))
                ),
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                color = EjeSpotPrimary,
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.profile_location_username, uiState.profile.location, uiState.profile.username),
            fontFamily = ManropeFontFamily,
            fontSize = 11.5.sp,
            color = c.placeholder,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progreso de experiencia
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = c.surface,
            border = BorderStroke(1.dp, c.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.profile_xp_progress, formatXp(uiState.profile.xp), formatXp(uiState.profile.nextLevelXp)),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.5.sp,
                        color = EjeSpotPrimary
                    )
                    Text(
                        text = stringResource(
                            R.string.profile_xp_to_next,
                            stringResource(ProfileLevels.labelRes(uiState.profile.nextLevelName))
                        ),
                        fontFamily = ManropeFontFamily,
                        fontSize = 10.5.sp,
                        color = c.placeholder
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { uiState.profile.xpProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(100.dp)),
                    color = EjeSpotPrimary,
                    trackColor = c.surfaceVariant
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = stringResource(R.string.profile_xp_hint),
                    fontFamily = ManropeFontFamily,
                    fontSize = 10.5.sp,
                    color = c.placeholder,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Estadísticas principales
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = c.surface,
            border = BorderStroke(1.dp, c.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileStat(
                    value = uiState.stats.visited.toString(),
                    label = stringResource(R.string.profile_stats_visited),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToVisited
                )
                VerticalDivider(color = c.surfaceVariant, modifier = Modifier.height(52.dp))
                ProfileStat(
                    value = uiState.stats.reviews.toString(),
                    label = stringResource(R.string.profile_stats_reviews),
                    modifier = Modifier.weight(1f),
                    onClick = onMyReviews
                )
                VerticalDivider(color = c.surfaceVariant, modifier = Modifier.height(52.dp))
                ProfileStat(
                    value = uiState.stats.proposals.toString(),
                    label = stringResource(R.string.profile_stat_proposals),
                    modifier = Modifier.weight(1f),
                    onClick = onMyProposals
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Niveles de la comunidad
        ProfileSectionHeader(title = stringResource(R.string.profile_section_levels))
        Spacer(modifier = Modifier.height(9.dp))
        ProfileCard {
            uiState.levels.forEachIndexed { index, level ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                color = when (level.status) {
                                    LevelStatus.ACHIEVED -> c.successBg
                                    LevelStatus.CURRENT -> EjeSpotPrimaryContainer
                                    LevelStatus.LOCKED -> c.surfaceVariant
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = level.emoji, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(ProfileLevels.labelRes(level.name)),
                            fontFamily = ManropeFontFamily,
                            fontWeight = if (level.status == LevelStatus.CURRENT) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = if (level.status == LevelStatus.CURRENT) EjeSpotPrimary else c.text
                        )
                        Text(
                            text = stringResource(level.rangeLabelRes),
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            color = c.placeholder
                        )
                    }

                    when (level.status) {
                        LevelStatus.ACHIEVED -> Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.profile_level_achieved),
                            tint = EjeSpotPrimary,
                            modifier = Modifier.size(17.dp)
                        )

                        LevelStatus.CURRENT -> Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = c.successBg
                        ) {
                            Text(
                                text = stringResource(R.string.profile_level_current),
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.5.sp,
                                color = c.successText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        LevelStatus.LOCKED -> Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = stringResource(R.string.profile_level_locked),
                            tint = c.starIdle,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (index < uiState.levels.lastIndex) {
                    HorizontalDivider(
                        color = c.surfaceVariant,
                        modifier = Modifier.padding(start = 54.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actividad del usuario
        ProfileSectionHeader(title = stringResource(R.string.profile_section_activity))
        Spacer(modifier = Modifier.height(9.dp))
        ProfileCard {
            uiState.activities.forEachIndexed { index, activity ->
                ProfileMenuRow(
                    emoji = activity.emoji,
                    title = stringResource(activity.labelRes),
                    value = activity.valueLabelRes?.let { stringResource(it, uiState.profile.level) }
                        ?: activity.valueLabel,
                    onClick = {
                        when (activity.id) {
                            "visited" -> onNavigateToVisited()
                            "reviews" -> onMyReviews()
                            "proposals" -> onMyProposals()
                            else -> onGamification()
                        }
                    }
                )
                if (index < uiState.activities.lastIndex) {
                    HorizontalDivider(color = c.surfaceVariant, modifier = Modifier.padding(start = 59.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rol y permisos especiales
        if (uiState.isModerator) {
            ProfileSectionHeader(title = stringResource(R.string.profile_section_role))
            Spacer(modifier = Modifier.height(9.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF2C5640),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.profile_role_label, stringResource(R.string.profile_role_moderator)),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = c.successBg
                        ) {
                            Text(
                                text = stringResource(R.string.profile_role_active),
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.5.sp,
                                color = c.successText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = stringResource(R.string.profile_role_description),
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.86f),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(11.dp))

                    Surface(
                        onClick = { showModerationDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        shape = RoundedCornerShape(100.dp),
                        color = Color.White.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.profile_open_moderation),
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = c.successBg
                            ) {
                                Text(
                                    text = "${uiState.pendingModeration}",
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.5.sp,
                                    color = c.successText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Preferencias y cuenta
        ProfileSectionHeader(title = stringResource(R.string.profile_section_account))
        Spacer(modifier = Modifier.height(9.dp))
        ProfileCard {
            ProfileMenuRow(
                emoji = "🔔",
                title = stringResource(R.string.profile_notifications_center),
                badge = if (uiState.unreadNotifications > 0) stringResource(R.string.profile_notifications_new, uiState.unreadNotifications) else stringResource(R.string.profile_notifications_up_to_date),
                badgeColor = if (uiState.unreadNotifications > 0) EjeSpotSecondary else EjeSpotPrimary,
                badgeBackground = if (uiState.unreadNotifications > 0) EjeSpotSecondaryContainer else EjeSpotPrimaryContainer,
                onClick = onNotifications
            )
            HorizontalDivider(color = c.surfaceVariant, modifier = Modifier.padding(start = 59.dp))
            ProfileMenuRow(
                emoji = "⚙️",
                title = stringResource(R.string.profile_settings_privacy),
                onClick = onSettings
            )
            HorizontalDivider(color = c.surfaceVariant, modifier = Modifier.padding(start = 59.dp))
            ProfileMenuRow(
                emoji = "🚪",
                title = stringResource(R.string.profile_logout),
                titleColor = c.dangerText,
                showChevron = false,
                onClick = { showLogoutDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Footer de marca
        Image(
            painter = painterResource(id = R.drawable.ic_logo_ejespot),
            contentDescription = stringResource(R.string.profile_logo_description),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(112.dp)
        )
        Text(
            text = stringResource(R.string.profile_tagline),
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = c.text
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.profile_version, "1.0.0"),
            fontFamily = ManropeFontFamily,
            fontSize = 10.sp,
            color = c.placeholder,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = c.surface,
            title = {
                Text(
                    text = stringResource(R.string.profile_logout),
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = c.text
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.profile_logout_question),
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.sp,
                    color = c.textSoft,
                    lineHeight = 19.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = c.dangerText)
                ) {
                    Text(stringResource(R.string.profile_logout_confirm), fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.common_cancel), fontFamily = ManropeFontFamily, color = c.textSoft)
                }
            }
        )
    }

    if (showModerationDialog) {
        // Mensajes resueltos aquí (contexto @Composable) para poder mostrarlos
        // desde la corrutina del snackbar.
        val moderationResolvedMessages: Map<String, String> =
            uiState.moderationQueue.associate { report ->
                report.id to stringResource(
                    R.string.profile_moderation_resolved_snackbar,
                    report.spotName
                )
            }
        ModerationQueueDialog(
            reports = uiState.moderationQueue,
            onResolve = { report ->
                viewModel.resolveModerationReport(report.id)
                coroutineScope.launch {
                    snackbarHostState?.showSnackbar(
                        moderationResolvedMessages[report.id] ?: report.spotName
                    )
                }
            },
            onDismiss = { showModerationDialog = false }
        )
    }
}

@Composable
private fun ProfileStat(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = ejeSpotColors()
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = EjeSpotPrimary
        )
        Text(
            text = label,
            fontFamily = ManropeFontFamily,
            fontSize = 11.sp,
            color = c.textSoft
        )
    }
}

@Composable
private fun ModerationQueueDialog(
    reports: List<com.example.ejespot.domain.model.ModerationReport>,
    onResolve: (com.example.ejespot.domain.model.ModerationReport) -> Unit,
    onDismiss: () -> Unit
) {
    val c = ejeSpotColors()
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = c.surface,
        title = {
            Column {
                Text(
                    text = stringResource(R.string.profile_moderation_title),
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = c.text
                )
                Text(
                    text = stringResource(R.string.profile_moderation_subtitle),
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.5.sp,
                    color = c.placeholder
                )
            }
        },
        text = {
            if (reports.isEmpty()) {
                Text(
                    text = stringResource(R.string.profile_moderation_empty),
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.sp,
                    color = c.successText,
                    lineHeight = 19.sp
                )
            } else {
                Column(
                    modifier = Modifier
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    reports.forEach { report ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = c.surfaceRaised,
                            border = BorderStroke(1.dp, c.outlineSoft),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(11.dp)) {
                                Text(
                                    text = report.spotName,
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = c.text
                                )
                                Text(
                                    text = stringResource(report.reasonRes),
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.sp,
                                    color = c.dangerText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${report.reporter} · ${stringResource(report.timeLabelRes)}",
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 10.sp,
                                    color = c.placeholder
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TextButton(
                                        onClick = { onResolve(report) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.profile_moderation_resolve),
                                            fontFamily = ManropeFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = EjeSpotPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
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
