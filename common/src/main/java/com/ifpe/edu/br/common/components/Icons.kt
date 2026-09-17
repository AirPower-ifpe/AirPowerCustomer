package com.ifpe.edu.br.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ifpe.edu.br.common.ui.theme.AirPowerTheme
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

@Composable
fun RoundedImageIcon(
    description: String,
    iconResId: Int,
    modifier: Modifier = Modifier
        .size(50.dp)
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(cardCornerRadius))
            .wrapContentSize()
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = description,
            modifier = modifier,
            alignment = Alignment.Center
        )
    }
}

@Composable
fun ImageIcon(
    description: String,
    iconResId: Int,
    iconTint: Color = AirPowerTheme.color.primary,
    modifier: Modifier = Modifier
        .size(50.dp)
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = description,
            modifier = modifier,
            alignment = Alignment.Center,
            colorFilter = ColorFilter.tint(iconTint),
        )
    }
}