package com.ifpe.edu.br.view.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.ImageIcon
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius
import com.ifpe.edu.br.model.repository.remote.dto.NotificationItem
import com.ifpe.edu.br.view.ui.theme.tb_primary_light

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
@Composable
fun NotificationCard(
    item: NotificationItem,
    onClick: () -> Unit
) {
    CustomCard(
        modifier = Modifier
            .clip(RoundedCornerShape(cardCornerRadius))
            .wrapContentHeight()
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick() },
        layouts = listOf {
            Column(
                modifier = Modifier.wrapContentSize()
            ) {
                ImageIcon(
                    description = "device icon",
                    iconResId = R.drawable.generic_device_icon,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    )
}

@Composable
private fun getCardColor(status: String): Color {
    val isDark = isSystemInDarkTheme()
    return when (status.lowercase()) {
        "online" -> if (isDark) Color(0xFF66BB6A) else Color(0xFF388E3C)
        "offline" -> if (isDark) Color(0xFFEF5350) else Color(0xFFD32F2F)
        else -> tb_primary_light
    }
}