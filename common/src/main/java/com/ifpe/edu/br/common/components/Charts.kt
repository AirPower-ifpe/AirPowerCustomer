package com.ifpe.edu.br.common.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.ifpe.edu.br.common.ui.theme.ColorPrimaryDark
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
@Composable
fun CustomBarChart(

) {
    ColumnChart(
        modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
        data = remember {
            listOf(
                Bars(
                    label = "1",
                    values = listOf(
                        Bars.Data(label = "aaa", value = 70.0, color = SolidColor(ColorPrimaryDark))
                    ),
                ),
                Bars(
                    label = "2",
                    values = listOf(
                        Bars.Data(label = "aaa", value = 60.0, color = SolidColor(ColorPrimaryDark))
                    ),
                )
            )
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
    )
}