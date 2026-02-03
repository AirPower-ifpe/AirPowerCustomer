// Trabalho de conclusão de curso - IFPE 2026
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.package com.ifpe.edu.br.common.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifpe.edu.br.common.contracts.ChartDataWrapper
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount

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
    val allValues: List<Double> = dataWrapper.getDataSet().data.map { it.verticalValue }
    val realMin = allValues.minOrNull() ?: 0.0
    val realMax = allValues.maxOrNull() ?: 0.0
    val range = realMax - realMin
    val chartMax = realMax + range
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
        maxValue = chartMax,
        barProperties = properties,
        data = remember {
            dataWrapper.getDataSet().toBar()
        },
        animationSpec = tween(durationMillis = 300),
        animationDelay = 100,
        animationMode = AnimationMode.Together { 100L },
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            textStyle = TextStyle.Default.copy(fontSize = 10.sp),
            count = IndicatorCount.CountBased(count = 10)
        ),
        gridProperties = GridProperties(
            xAxisProperties = AxisProperties(
                enabled = true,
                lineCount = 10,
            ),
            yAxisProperties = AxisProperties(
                enabled = true,
                lineCount = 5,
            )
        )
    )
}
