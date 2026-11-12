package com.example.ejespot.domain.model

enum class NotificationType {
    COMMENT,     // Alguien comentó en tu publicación
    APPROVAL,    // Un moderador aprobó tu publicación
    REJECTION,   // Un moderador rechazó tu propuesta
    ACHIEVEMENT, // Desbloqueaste una nueva insignia o subiste de nivel
    VOTE,        // Alguien votó 'Me interesa' en tu publicación
    SYSTEM       // Notificación general del sistema
}

data class AppNotification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val relatedSpotId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
