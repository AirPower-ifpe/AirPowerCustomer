package com.ifpe.edu.br.view.ui.components


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.common.components.ImageIcon
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius
import com.ifpe.edu.br.model.repository.model.DeviceCardModel
import com.ifpe.edu.br.view.ui.theme.tb_primary_light

@Composable
fun DeviceCard(
    device: DeviceCardModel,
    onClick: (deviceId: String) -> Unit,
) {
    CustomCard(
        modifier = Modifier
            .clip(RoundedCornerShape(cardCornerRadius))
            .wrapContentSize()
            .background(Color.White)
            .clickable { onClick(device.id) },
        layouts = listOf {
            Column(
                modifier = Modifier.wrapContentSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomColumn(
                        modifier = Modifier.wrapContentSize(),
                        layouts = listOf {
                            ImageIcon(
                                description = "device icon",
                                iconResId = R.drawable.generic_device_icon,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    )

                    CustomColumn(
                        modifier = Modifier.wrapContentSize(),
                        layouts = listOf {
                            CustomText(
                                text = device.label,
                                alignment = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = tb_primary_light,
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomText(
                        text = device.status,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = getStatusColor(device.status),
                        alignment = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}

@Composable
fun getStatusColor(status: String): Color {
    val isDark = isSystemInDarkTheme()
    return when (status.lowercase()) {
        "online" -> if (isDark) Color(0xFF66BB6A) else Color(0xFF388E3C)
        "offline" -> if (isDark) Color(0xFFEF5350) else Color(0xFFD32F2F)
        else -> tb_primary_light
    }
}