package com.example.ejespot.features.dashboard.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
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
import com.example.ejespot.domain.model.NotificationCategory
import com.example.ejespot.domain.model.NotificationDayGroup
import com.example.ejespot.domain.model.ProfileNotification
import com.example.ejespot.features.dashboard.profile.components.ProfileChip
import com.example.ejespot.features.dashboard.profile.components.ProfileEmptyState
import com.example.ejespot.features.dashboard.profile.components.ProfileTopBar
import com.example.ejespot.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    padding: PaddingValues = PaddingValues(),
    viewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState? = null,
    onNavigateBack: () -> Unit
) {
    val c = ejeSpotColors()
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var selectedFilter by remember { mutableStateOf("Todas") }
    var selectedNotification by remember { mutableStateOf<ProfileNotification?>(null) }
    val markAllReadMessage = stringResource(R.string.notifications_mark_all_snackbar)

    val filtered = remember(uiState.notifications, selectedFilter) {
        if (selectedFilter == "No leídas") {
            uiState.notifications.filter { !it.isRead }
        } else {
            uiState.notifications
        }
    }

    val groups = remember(filtered) {
        listOf(NotificationDayGroup.TODAY, NotificationDayGroup.YESTERDAY).mapNotNull { day ->
            val items = filtered.filter { it.dayGroup == day }
            if (items.isEmpty()) null else day to items
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
    ) {
        ProfileTopBar(
            title = stringResource(R.string.notifications_title),
            onBack = onNavigateBack,
            trailing = {
                Surface(
                    onClick = {
                        viewModel.markAllNotificationsRead()
                        coroutineScope.launch {
                            snackbarHostState?.showSnackbar(markAllReadMessage)
                        }
                    },
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    color = c.surface,
                    border = BorderStroke(1.dp, c.outlineSoft)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = stringResource(R.string.notifications_mark_all_description),
                            tint = c.primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileChip(
                        text = stringResource(R.string.notifications_all_count, uiState.notifications.size),
                        selected = selectedFilter == "Todas",
                        onClick = { selectedFilter = "Todas" }
                    )
                    ProfileChip(
                        text = stringResource(R.string.notifications_unread_count, uiState.unreadNotifications),
                        selected = selectedFilter == "No leídas",
                        onClick = { selectedFilter = "No leídas" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (uiState.unreadNotifications > 0) {
                    stringResource(R.string.notifications_unread_summary, uiState.unreadNotifications)
                } else {
                    stringResource(R.string.notifications_summary_up_to_date)
                },
                fontFamily = ManropeFontFamily,
                fontSize = 11.sp,
                color = c.placeholder
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filtered.isEmpty()) {
            ProfileEmptyState(
                emoji = "🎉",
                title = stringResource(R.string.notifications_empty_title),
                message = stringResource(R.string.notifications_empty_message)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                groups.forEach { (day, dayNotifications) ->
                    item(key = "header_${day.name}") {
                        Text(
                            text = stringResource(day.labelRes()),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.5.sp,
                            letterSpacing = 0.7.sp,
                            color = c.placeholder,
                            modifier = Modifier.padding(top = 10.dp, bottom = 1.dp, start = 2.dp)
                        )
                    }

                    items(dayNotifications, key = { it.id }) { notification ->
                        NotificationRow(
                            notification = notification,
                            onClick = {
                                viewModel.markNotificationRead(notification.id)
                                selectedNotification = notification.copy(isRead = true)
                            }
                        )
                    }
                }
            }
        }
    }

    selectedNotification?.let { notification ->
        AlertDialog(
            onDismissRequest = { selectedNotification = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = c.surface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(notification.category.containerColor(), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = notification.category.emoji(), fontSize = 17.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(notification.titleRes),
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = c.text,
                        lineHeight = 21.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = stringResource(notification.messageRes),
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(notification.timeLabelRes),
                        fontFamily = ManropeFontFamily,
                        fontSize = 10.5.sp,
                        color = c.placeholder
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedNotification = null }) {
                    Text(
                        text = stringResource(R.string.common_accept),
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
private fun NotificationRow(
    notification: ProfileNotification,
    onClick: () -> Unit
) {
    val c = ejeSpotColors()
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (notification.isRead) c.surface else c.surfaceRaised,
        border = BorderStroke(
            1.dp,
            if (notification.isRead) c.outlineSoft else c.successBg
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(notification.category.containerColor(), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = notification.category.emoji(), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(11.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(notification.titleRes),
                        fontFamily = ManropeFontFamily,
                        fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.ExtraBold,
                        fontSize = 12.5.sp,
                        color = c.text,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(c.primary, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = stringResource(notification.messageRes),
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.5.sp,
                    color = c.textSoft,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = stringResource(notification.timeLabelRes),
                    fontFamily = ManropeFontFamily,
                    fontSize = 10.sp,
                    color = c.placeholder
                )
            }
        }
    }
}

private fun NotificationCategory.emoji(): String = when (this) {
    NotificationCategory.XP -> "⭐"
    NotificationCategory.REVIEW -> "✍️"
    NotificationCategory.COMMENT -> "💬"
    NotificationCategory.LEVEL -> "🏆"
    NotificationCategory.NEARBY -> "📍"
}

private fun NotificationCategory.containerColor(): Color = when (this) {
    NotificationCategory.XP -> Color(0xFFF6DEC9)
    NotificationCategory.REVIEW -> Color(0xFFD7E8DC)
    NotificationCategory.COMMENT -> Color(0xFFD7E8DC)
    NotificationCategory.LEVEL -> Color(0xFFF5E3B8)
    NotificationCategory.NEARBY -> Color(0xFFF6DEC9)
}
