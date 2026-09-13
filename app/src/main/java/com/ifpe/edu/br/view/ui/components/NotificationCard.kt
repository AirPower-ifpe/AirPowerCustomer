/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
package com.ifpe.edu.br.view.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifpe.edu.br.common.ui.theme.AirPowerTheme
import com.ifpe.edu.br.model.repository.remote.dto.AirPowerNotificationItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationCard(
    item: AirPowerNotificationItem,
    onClick: () -> Unit
) {
    val isUnread = item.status != "READ"
    val cardCornerRadius = 14.dp

    val originatorName = item.alarmOriginatorName?.takeIf { it.isNotBlank() } ?: "Dispositivo"
    val subjectTitle = item.subject.takeIf { it.isNotBlank() }
        ?: item.alarmType?.takeIf { it.isNotBlank() }
        ?: "Notificação de Alarme"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cardCornerRadius))
            .border(
                width = if (isUnread) 1.5.dp else 0.5.dp,
                color = if (isUnread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(cardCornerRadius)
            )
            .clickable { onClick() },
        color = if (isUnread) AirPowerTheme.color.primaryContainer else AirPowerTheme.color.secondaryContainer.copy(alpha = 0.5f),
        shape = RoundedCornerShape(cardCornerRadius),
        tonalElevation = if (isUnread) 3.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isUnread) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = originatorName,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = AirPowerTheme.color.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = formatTimestamp(item.createdTime),
                        style = MaterialTheme.typography.labelSmall,
                        color = AirPowerTheme.color.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subjectTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                        lineHeight = 18.sp
                    ),
                    color = AirPowerTheme.color.onPrimaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SeverityBadge(severity = item.alarmSeverity)

                    item.alarmType?.takeIf { it.isNotBlank() }?.let { alarmType ->
                        TagBadge(text = alarmType)
                    }
                }
            }
        }
    }
}

@Composable
private fun SeverityBadge(severity: String?) {
    val safeSeverity = severity?.uppercase()?.takeIf { it.isNotBlank() } ?: "INFO"

    val (bgColor, textColor) = when (safeSeverity) {
        "CRITICAL" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "MAJOR", "HIGH" -> Color(0xFFFFE0B2) to Color(0xFFE65100)
        "MINOR", "WARNING" -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = safeSeverity,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
            color = textColor
        )
    }
}

@Composable
private fun TagBadge(text: String) {
    Box(
        modifier = Modifier
            .background(AirPowerTheme.color.secondaryContainer, RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = AirPowerTheme.color.onSecondaryContainer
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
    return formatter.format(date)
}