/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
package com.ifpe.edu.br.common.ui.theme


import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class AppTypography(
    val displayLarge: TextStyle, // Títulos grandes (ex: TopBar principal)
    val displayMedium: TextStyle, // Títulos de seções (ex: Cards)
    val bodyLarge: TextStyle,    // Texto padrão
    val bodySmall: TextStyle,    // Detalhes, labels menores
    val button: TextStyle        // Texto de botões
)

val compactTypography = AppTypography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
)

val expandedTypography = AppTypography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
)

val LocalAppTypography = staticCompositionLocalOf { compactTypography }

fun AppTypography.withScale(scaleFactor: Float): AppTypography {
    return AppTypography(
        displayLarge = this.displayLarge.copy(fontSize = this.displayLarge.fontSize * scaleFactor),
        displayMedium = this.displayMedium.copy(fontSize = this.displayMedium.fontSize * scaleFactor),
        bodyLarge = this.bodyLarge.copy(fontSize = this.bodyLarge.fontSize * scaleFactor),
        bodySmall = this.bodySmall.copy(fontSize = this.bodySmall.fontSize * scaleFactor),
        button = this.button.copy(fontSize = this.button.fontSize * scaleFactor)
    )
}
