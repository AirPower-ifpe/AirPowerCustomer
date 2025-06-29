package com.ifpe.edu.br.view.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius
import com.ifpe.edu.br.model.repository.remote.dto.NotificationItem
import com.ifpe.edu.br.view.ui.theme.app_default_solid_background_light
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            .background(getCardColor(item.isNew))
            .clickable { onClick() },
        layouts = listOf {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    CustomText(
                        color = tb_primary_light,
                        alignment = TextAlign.Left,
                        fontSize = 18.sp,
                        text = item.label
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    CustomText(
                        color = tb_primary_light,
                        alignment = TextAlign.Left,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        text = item.message
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    CustomText(
                        color = tb_primary_light,
                        fontSize = 10.sp,
                        alignment = TextAlign.Right,
                        fontWeight = FontWeight.Thin,
                        text = formatTimestamp(item.timestamp)
                    )
                }
            }
        }
    )
}

@Composable
private fun getCardColor(isNew: Boolean): Color {
    return if (isNew) {
        Color.White
    } else {
        app_default_solid_background_light
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("MM/yy HH:mm", Locale.getDefault())
    return formatter.format(date)
}