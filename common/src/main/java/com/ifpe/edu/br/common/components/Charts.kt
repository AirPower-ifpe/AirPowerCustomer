package com.ifpe.edu.br.common.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ifpe.edu.br.common.contracts.ChartDataWrapper
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
@Composable
fun CustomBarChart(
    paddingStart: Dp = 0.dp,
    paddingTop: Dp = 0.dp,
    paddingEnd: Dp = 0.dp,
    paddingBottom: Dp = 0.dp,
    height: Dp = 250.dp,
    thickNes: Dp = 10.dp,
    spacing: Dp = 5.dp,
    dataWrapper: ChartDataWrapper
) {
    val properties = BarProperties(thickNes, spacing)
    ColumnChart(
        modifier = Modifier
            .height(height)
            .padding(
                start = paddingStart,
                top = paddingTop,
                end = paddingEnd,
                bottom = paddingBottom
            ),
        barProperties = properties,
        data = remember {
            dataWrapper.getDataSet().toBar()
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
}