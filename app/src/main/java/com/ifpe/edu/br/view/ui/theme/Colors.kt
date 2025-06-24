package com.ifpe.edu.br.view.ui.theme
/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale

val tb_primary_light = Color(0xFF305680)
val tb_secondary_light = Color(0xFFFF5722)
val tb_tertiary_light = Color(0xFFEEEEEE)
val app_default_solid_background_light = Color(0xD7DAD6D6)
val app_default_solid_background_dark = Color(0xD79A9696)
val app_default_solid_background_dark_variant = Color(0xD7C4C0C0)

val a = Color(0xE9FAF9F9)
val b = Color(0xD7C2BEBE)

private val transparentGradient = listOf(
    a, b
)

val appBackgroundGradientLight = listOf(
    Color.White, app_default_solid_background_light
)

val appBackgroundGradientDark = listOf(
    app_default_solid_background_dark_variant, app_default_solid_background_dark
)

@Composable
fun DefaultTransparentGradient(
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Canvas(
        modifier = modifier
    ) {
        scale(scaleX = 1f, scaleY = 1f) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = transparentGradient,
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.width * 0.8f
                )
            )
        }
    }
}