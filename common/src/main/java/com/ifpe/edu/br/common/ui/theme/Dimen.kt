/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
package com.ifpe.edu.br.common.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val cardCornerRadius = 15.dp

data class AppDimens(
    val paddingSmall: Dp,
    val paddingMedium: Dp,
    val paddingLarge: Dp,
    val iconSizeSmall: Dp,
    val iconSizeMedium: Dp,
    val iconSizeLarge: Dp,
    val cardCornerRadius: Dp,
    val chartHeight: Dp
)

val compactDimens = AppDimens(
    paddingSmall = 8.dp,
    paddingMedium = 16.dp,
    paddingLarge = 24.dp,
    iconSizeSmall = 24.dp,
    iconSizeMedium = 32.dp,
    iconSizeLarge = 48.dp,
    cardCornerRadius = 12.dp,
    chartHeight = 250.dp
)

val expandedDimens = AppDimens(
    paddingSmall = 12.dp,
    paddingMedium = 24.dp,
    paddingLarge = 32.dp,
    iconSizeSmall = 32.dp,
    iconSizeMedium = 48.dp,
    iconSizeLarge = 64.dp,
    cardCornerRadius = 16.dp,
    chartHeight = 350.dp
)

val LocalAppDimens = compositionLocalOf { compactDimens }

fun AppDimens.withScale(scaleFactor: Float): AppDimens {
    return AppDimens(
        paddingSmall = this.paddingSmall * scaleFactor,
        paddingMedium = this.paddingMedium * scaleFactor,
        paddingLarge = this.paddingLarge * scaleFactor,
        iconSizeSmall = this.iconSizeSmall * scaleFactor,
        iconSizeMedium = this.iconSizeMedium * scaleFactor,
        iconSizeLarge = this.iconSizeLarge * scaleFactor,
        cardCornerRadius = this.cardCornerRadius,
        chartHeight = this.chartHeight * scaleFactor
    )
}