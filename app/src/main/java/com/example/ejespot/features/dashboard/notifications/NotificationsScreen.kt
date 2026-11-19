package com.example.ejespot.features.dashboard.notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.core.data.SpotStore
import com.example.ejespot.core.session.SessionManager
import com.example.ejespot.domain.model.AppNotification
import com.example.ejespot.domain.model.NotificationType
import com.example.ejespot.ui.theme.*

@Composable
fun NotificationsScreen(
    padding: PaddingValues = PaddingValues(),
    onNavigateBack: () -> Unit,
    onNavigateToSpot: (String) -> Unit
) {
    val c = ejeSpotColors()
    val allNotifications by SpotStore.notifications.collectAsState()
    val currentUserId = SessionManager.profileId

    val userNotifications = remember(allNotifications, currentUserId) {
        SpotStore.getNotificationsForUser(currentUserId)
    }

    var selectedFilter by remember { mutableStateOf("Todas") }

    val filteredNotifications = remember(userNotifications, selectedFilter) {
        when (selectedFilter) {
            "No leídas" -> userNotifications.filter { !it.isRead }
            else -> userNotifications
        }
    }

    val unreadCount = remember(userNotifications) {
        userNotifications.count { !it.isRead }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.background)
            .padding(padding)
    ) {
        // --- Header con botón Atrás y Acción "Marcar todas como leídas" ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onNavigateBack,
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = c.surfaceVariant,
                border = BorderStroke(1.dp, c.outline)
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

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Notificaciones",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = c.text
                )
                Text(
                    text = if (unreadCount > 0) "$unreadCount no leída${if (unreadCount > 1) "s" else ""}" else "Al día",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.sp,
                    color = if (unreadCount > 0) Color(0xFFE65100) else c.textSoft,
                    fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (unreadCount > 0) {
                TextButton(
                    onClick = { SpotStore.markAllNotificationsAsRead(currentUserId) }
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = EjeSpotPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Leer todas",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = EjeSpotPrimary
                    )
                }
            }
        }

        // --- Filtros (Todas / No leídas) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todas", "No leídas").forEach { filter ->
                val isSelected = selectedFilter == filter
                val count = if (filter == "No leídas") unreadCount else userNotifications.size
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isSelected) EjeSpotPrimary else c.chipIdle,
                    border = BorderStroke(1.dp, if (isSelected) EjeSpotPrimary else c.outline),
                    modifier = Modifier.clickable { selectedFilter = filter }
                ) {
                    Text(
                        text = "$filter ($count)",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else c.textMuted,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Lista de Notificaciones o Estado Vacío ---
        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🔔",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (selectedFilter == "No leídas") "No tienes notificaciones pendientes" else "Sin notificaciones aún",
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = c.text
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Aquí recibirás avisos sobre comentarios en tus spots, revisiones de moderadores y nuevos logros.",
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = c.placeholder,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNotifications, key = { it.id }) { notif ->
                    NotificationCard(
                        notification = notif,
                        onClick = {
                            SpotStore.markNotificationAsRead(notif.id)
                            notif.relatedSpotId?.let { spotId ->
                                onNavigateToSpot(spotId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val c = ejeSpotColors()
    val isUnread = !notification.isRead

    val iconEmoji = when (notification.type) {
        NotificationType.COMMENT -> "💬"
        NotificationType.APPROVAL -> "✅"
        NotificationType.REJECTION -> "⚠️"
        NotificationType.ACHIEVEMENT -> "🏆"
        NotificationType.VOTE -> "🔥"
        NotificationType.SYSTEM -> "☕"
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isUnread) Color(0xFFF9F5EC) else c.surface,
        border = BorderStroke(
            1.dp,
            if (isUnread) EjeSpotPrimary.copy(alpha = 0.4f) else c.outlineSoft
        ),
        shadowElevation = if (isUnread) 2.dp else 0.5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono del tipo de notificación
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnread) EjeSpotPrimaryContainer else c.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontFamily = FrauncesFontFamily,
                        fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = c.text,
                        modifier = Modifier.weight(1f)
                    )
                    if (isUnread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EjeSpotPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    color = if (isUnread) c.text else c.textSoft,
                    lineHeight = 17.sp
                )

                if (notification.relatedSpotId != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Toca para ver la publicación →",
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EjeSpotPrimary
                    )
                }
            }
        }
    }
}
