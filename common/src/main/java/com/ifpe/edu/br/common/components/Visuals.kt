package com.ifpe.edu.br.common.components
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
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientLight

@Composable
fun GradientBackground(
    gradientColors: List<Color> = defaultBackgroundGradientLight
) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        scale(scaleX = 1f, scaleY = 1f) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = gradientColors,
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.width * 0.8f
                )
            )
        }
    }
}