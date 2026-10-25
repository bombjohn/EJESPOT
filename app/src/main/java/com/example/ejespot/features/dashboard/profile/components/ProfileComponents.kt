package com.example.ejespot.features.dashboard.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.ui.theme.EjeSpotPrimary
import com.example.ejespot.ui.theme.FrauncesFontFamily
import com.example.ejespot.ui.theme.ManropeFontFamily
import com.example.ejespot.ui.theme.ejeSpotColors

/** Formatea cantidades de XP con separador de miles (ej. 1.200). */
fun formatXp(value: Int): String {
    val raw = value.toString()
    val builder = StringBuilder()
    raw.forEachIndexed { index, char ->
        if (index > 0 && (raw.length - index) % 3 == 0) builder.append('.')
        builder.append(char)
    }
    return builder.toString()
}

@Composable
fun ProfileTopBar(
    title: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    trailing: (@Composable RowScope.() -> Unit)? = null
) {
    val c = ejeSpotColors()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            Surface(
                onClick = onBack,
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = c.surface,
                border = BorderStroke(1.dp, c.outlineSoft),
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = c.text,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))
        }

        Text(
            text = title,
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            color = c.text,
            modifier = Modifier.weight(1f)
        )

        trailing?.invoke(this)
    }
}

@Composable
fun ProfileSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    val c = ejeSpotColors()
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = c.text
        )
        trailing?.invoke()
    }
}

@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val c = ejeSpotColors()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = c.surface,
        border = BorderStroke(1.dp, c.outline),
        shadowElevation = 1.dp
    ) {
        Column { content() }
    }
}

@Composable
fun ProfileMenuRow(
    emoji: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    value: String? = null,
    badge: String? = null,
    badgeColor: Color = Color(0xFF2C5640),
    badgeBackground: Color = Color(0xFFD7E8DC),
    titleColor: Color = Color(0xFF1F1A15),
    showChevron: Boolean = true
) {
    val c = ejeSpotColors()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
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

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = titleColor,
                lineHeight = 17.sp
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = ManropeFontFamily,
                    fontSize = 10.5.sp,
                    color = c.placeholder,
                    lineHeight = 14.sp
                )
            }
        }

        if (badge != null) {
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = badgeBackground
            ) {
                Text(
                    text = badge,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        if (value != null) {
            Text(
                text = value,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                color = c.placeholder
            )
            Spacer(modifier = Modifier.width(2.dp))
        }

        if (showChevron) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = c.placeholder,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ProfileSwitchRow(
    emoji: String,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    val c = ejeSpotColors()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
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

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = c.text,
                lineHeight = 17.sp
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = ManropeFontFamily,
                    fontSize = 10.5.sp,
                    color = c.placeholder,
                    lineHeight = 14.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EjeSpotPrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = c.starIdle
            )
        )
    }
}

@Composable
fun ProfileChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = ejeSpotColors()
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = if (selected) EjeSpotPrimary else c.chipIdle,
        border = BorderStroke(1.dp, if (selected) EjeSpotPrimary else c.outline),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = if (selected) Color.White else c.textMuted,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun ProfileEmptyState(
    emoji: String,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    val c = ejeSpotColors()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 42.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = c.text,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                fontFamily = ManropeFontFamily,
                fontSize = 12.5.sp,
                color = c.placeholder,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}
