package com.example.ejespot.features.dashboard.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.domain.model.AchievementBadge
import com.example.ejespot.domain.model.ProfileLevels
import com.example.ejespot.features.dashboard.profile.components.*
import com.example.ejespot.ui.theme.*

@Composable
fun GamificationScreen(
    padding: PaddingValues = PaddingValues(),
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onExplore: () -> Unit
) {
    val c = ejeSpotColors()
    val uiState by viewModel.uiState.collectAsState()
    var selectedBadge by remember { mutableStateOf<AchievementBadge?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
            .verticalScroll(rememberScrollState())
    ) {
        ProfileTopBar(title = stringResource(R.string.gamification_progress_title), onBack = onNavigateBack)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            // Tarjeta principal de nivel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF3D6B4F), Color(0xFF22472F))
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = EjeSpotGold,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = stringResource(R.string.gamification_level_label, uiState.profile.level),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.5.sp,
                        letterSpacing = 1.2.sp,
                        color = EjeSpotGold
                    )
                    Text(
                        text = stringResource(ProfileLevels.labelRes(uiState.profile.levelName)),
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { uiState.profile.xpProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = EjeSpotGold,
                        trackColor = Color.White.copy(alpha = 0.18f)
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(
                        text = stringResource(
                            R.string.gamification_xp_para,
                            formatXp(uiState.profile.xp),
                            formatXp(uiState.profile.nextLevelXp),
                            stringResource(ProfileLevels.labelRes(uiState.profile.nextLevelName))
                        ),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.5.sp,
                        color = Color.White.copy(alpha = 0.82f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                ProfileStatCard(uiState.stats.visited.toString(), stringResource(R.string.gamification_stat_places_visited), Modifier.weight(1f))
                ProfileStatCard(uiState.stats.reviews.toString(), stringResource(R.string.gamification_stat_reviews_written), Modifier.weight(1f))
                ProfileStatCard(uiState.stats.proposals.toString(), stringResource(R.string.gamification_stat_points_proposed), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(22.dp))

            ProfileSectionHeader(
                title = stringResource(R.string.gamification_badges_unlocked),
                trailing = {
                    Text(
                        text = "${uiState.unlockedBadges} / ${uiState.badges.size}",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = c.placeholder
                    )
                }
            )

            Spacer(modifier = Modifier.height(9.dp))

            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                uiState.badges.forEach { badge ->
                    BadgeRow(
                        badge = badge,
                        onClick = { selectedBadge = badge }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            ProfileSectionHeader(title = stringResource(R.string.gamification_section_next_level))
            Spacer(modifier = Modifier.height(9.dp))
            ProfileCard {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFF5E3B8), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 19.sp)
                        }
                        Spacer(modifier = Modifier.width(11.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(ProfileLevels.labelRes(uiState.profile.nextLevelName)),
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = c.text
                            )
                            Text(
                                text = stringResource(R.string.gamification_xp_missing, formatXp((uiState.profile.nextLevelXp - uiState.profile.xp).coerceAtLeast(0))),
                                fontFamily = ManropeFontFamily,
                                fontSize = 11.sp,
                                color = c.placeholder
                            )
                        }
                        Text(
                            text = "${(uiState.profile.xpProgress * 100).toInt()}%",
                            fontFamily = FrauncesFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = c.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(11.dp))

                    LinearProgressIndicator(
                        progress = { uiState.profile.xpProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = c.primary,
                        trackColor = c.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(13.dp))

                    Button(
                        onClick = onExplore,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = c.primary)
                    ) {
                        Text(
                            text = stringResource(R.string.gamification_explore),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            ProfileSectionHeader(title = stringResource(R.string.gamification_section_earn_xp))
            Spacer(modifier = Modifier.height(9.dp))
            ProfileCard {
                XpRow("✍️", stringResource(R.string.gamification_xp_review), stringResource(R.string.gamification_xp_review_amount))
                HorizontalDivider(color = c.surfaceVariant, modifier = Modifier.padding(start = 59.dp))
                XpRow("📍", stringResource(R.string.gamification_xp_propose), stringResource(R.string.gamification_xp_propose_amount))
                HorizontalDivider(color = c.surfaceVariant, modifier = Modifier.padding(start = 59.dp))
                XpRow("🥾", stringResource(R.string.gamification_xp_visited), stringResource(R.string.gamification_xp_visited_amount))
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    selectedBadge?.let { badge ->
        AlertDialog(
            onDismissRequest = { selectedBadge = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = c.surface,
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .background(
                                if (badge.isUnlocked) Color(0xFFF5E3B8) else c.surfaceVariant,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badge.emoji, fontSize = 30.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(badge.titleRes),
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = c.text,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(badge.descriptionRes),
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (badge.isUnlocked) c.successBg else Color(0xFFF5E3B8)
                    ) {
                        Text(
                            text = if (badge.isUnlocked) stringResource(R.string.gamification_badge_unlocked) else stringResource(R.string.gamification_badge_locked_progress, badge.progressLabelRes?.let { stringResource(it) } ?: stringResource(R.string.gamification_badge_in_progress)),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = if (badge.isUnlocked) c.successText else Color(0xFF8A6A17),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedBadge = null }) {
                    Text(
                        text = stringResource(R.string.common_close),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = c.primary
                    )
                }
            }
        )
    }
}

@Composable
private fun ProfileStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    val c = ejeSpotColors()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = c.surface,
        border = BorderStroke(1.dp, c.outline)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = c.primary
            )
            Text(
                text = label,
                fontFamily = ManropeFontFamily,
                fontSize = 10.sp,
                color = c.textSoft,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
private fun BadgeRow(badge: AchievementBadge, onClick: () -> Unit) {
    val c = ejeSpotColors()
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = c.surface,
        border = BorderStroke(1.dp, if (badge.isUnlocked) c.successBg else c.outlineSoft),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (badge.isUnlocked) Color(0xFFF5E3B8) else c.surfaceVariant,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge.emoji,
                    fontSize = 19.sp,
                    modifier = Modifier.then(
                        if (badge.isUnlocked) Modifier else Modifier.alpha(0.55f)
                    )
                )
            }

            Spacer(modifier = Modifier.width(11.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(badge.titleRes),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = c.text
                )
                Text(
                    text = stringResource(badge.descriptionRes),
                    fontFamily = ManropeFontFamily,
                    fontSize = 10.5.sp,
                    color = c.placeholder,
                    lineHeight = 14.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            if (badge.isUnlocked) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = c.successBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = c.successText,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = stringResource(R.string.gamification_badge_achieved),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            color = c.successText
                        )
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.End) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = stringResource(R.string.gamification_badge_locked_description),
                        tint = Color(0xFFB5A997),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = badge.progressLabelRes?.let { stringResource(it) }
                            ?: stringResource(R.string.gamification_badge_in_progress),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp,
                        color = c.placeholder
                    )
                }
            }
        }
    }
}

@Composable
private fun XpRow(emoji: String, title: String, xp: String) {
    val c = ejeSpotColors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(c.surfaceVariant, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.width(11.dp))
        Text(
            text = title,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.5.sp,
            color = c.text,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = xp,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.5.sp,
            color = c.primary
        )
    }
}
