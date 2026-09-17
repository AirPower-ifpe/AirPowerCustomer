// Trabalho de conclusão de curso - IFPE 2026
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.
package com.ifpe.edu.br.common.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.ifpe.edu.br.common.contracts.ChartDataWrapper
import com.ifpe.edu.br.common.ui.theme.AirPowerTheme
import com.ifpe.edu.br.common.ui.theme.tb_primary_light
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import kotlin.math.absoluteValue

@Composable
fun CustomColumnChart(
    paddingStart: Dp = 0.dp,
    paddingTop: Dp = 0.dp,
    paddingEnd: Dp = 0.dp,
    paddingBottom: Dp = 0.dp,
    height: Dp = 250.dp,
    dataWrapper: ChartDataWrapper
) {
    val chartScaleOffset = getChartScale(dataWrapper)
    val itemCount = dataWrapper.getDataSet().data.size
    val (dynamicThickness, dynamicSpacing) = remember(itemCount) {
        calculateDynamicDimensions(itemCount)
    }
    val radius = Bars.Data.Radius.Rectangle(topRight = 3.dp, topLeft = 3.dp)
    val properties = BarProperties(dynamicThickness, dynamicSpacing, cornerRadius = radius)

    val appColor = AirPowerTheme.color
    val appTypography = AirPowerTheme.typography

    val barColor = AirPowerTheme.color.primary
    val chartData = remember(dataWrapper, barColor) {
        dataWrapper.getDataSet().toBar().map { bar ->
            bar.copy(
                values = bar.values.map { data ->
                    data.copy(color = Brush.verticalGradient(listOf(barColor, barColor.copy(alpha = 0.6f))))
                }
            )
        }
    }

    ColumnChart(
        modifier = Modifier
            .background(Color.Transparent)
            .height(height)
            .padding(
                start = paddingStart,
                top = paddingTop,
                end = paddingEnd,
                bottom = paddingBottom
            ),
        barProperties = properties,
        data = chartData,
        maxValue = chartScaleOffset,
        animationSpec = tween(durationMillis = 300),
        animationDelay = 100,
        animationMode = AnimationMode.Together { 100L },
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            textStyle = TextStyle.Default.copy(
                fontSize = appTypography.bodySmall.fontSize,
                color = appColor.onSecondaryContainer
            ),
            count = IndicatorCount.CountBased(count = 5),
            padding = AirPowerTheme.dimens.paddingSmall
        ),
        gridProperties = GridProperties(
            xAxisProperties = AxisProperties(
                enabled = true,
                lineCount = itemCount.coerceAtMost(10),
            ),
            yAxisProperties = AxisProperties(
                enabled = true,
                lineCount = 5,
            )
        ),
        labelHelperProperties = LabelHelperProperties(
            enabled = true,textStyle = TextStyle.Default.copy(
                fontSize = appTypography.bodySmall.fontSize,
                color = AirPowerTheme.color.onPrimaryContainer,
                fontWeight = appTypography.bodyLarge.fontWeight
            )
        ),

        popupProperties = PopupProperties(
            enabled = true,
            textStyle = TextStyle.Default.copy(
                fontSize = appTypography.bodySmall.fontSize,
                color = appColor.onSecondary,
                fontWeight = appTypography.bodyLarge.fontWeight
            ),
            containerColor = appColor.secondary,
            cornerRadius = 8.dp,
            contentBuilder = { value ->
                "%.2f".format(value)
            }
        ),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = TextStyle.Default.copy(
                fontSize = appTypography.bodySmall.fontSize,
                color = appColor.onSurface
            ),
            padding = 4.dp
        )
    )
}

@Composable
fun CustomLineChart(
    paddingStart: Dp = 0.dp,
    paddingTop: Dp = 0.dp,
    paddingEnd: Dp = 0.dp,
    paddingBottom: Dp = 0.dp,
    height: Dp = 250.dp,
    dataWrapper: ChartDataWrapper
) {
    val chartValues = remember(dataWrapper.getDataSet()) {
        dataWrapper.getDataSet().data.map { it.verticalValue }
    }

    val appColors = AirPowerTheme.color
    val appSecondaryColor = AirPowerTheme.color.secondary
    val appTypography = AirPowerTheme.typography
    val chartScaleConfig = getChartScale(dataWrapper)

    val chartLabels = remember(dataWrapper) {
        dataWrapper.getDataSet().data .map { it.label }
    }

    val lineData = remember(chartValues) {
        listOf(
            Line(
                label = dataWrapper.getName(),
                values = chartValues,
                color = SolidColor(appColors.primary),
                firstGradientFillColor = appColors.primary,
                secondGradientFillColor = appColors.primary.copy(alpha = 0.4f),
                strokeAnimationSpec = tween(1000),
                gradientAnimationDelay = 500,
                drawStyle = DrawStyle.Stroke(width = 3.dp),
                curvedEdges = true,
                dotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(appSecondaryColor),
                    strokeWidth = 2.dp,
                    radius = calculateDynamicDotSize(chartValues.size)
                ),
                popupProperties = PopupProperties(
                    enabled = true,
                    textStyle = TextStyle.Default.copy(
                        fontSize = appTypography.bodySmall.fontSize,
                        color = appColors.onSecondary,
                        fontWeight = appTypography.bodyLarge.fontWeight
                    ),
                    containerColor = appSecondaryColor,
                    cornerRadius = 8.dp,
                    contentBuilder = { value ->
                        "%.2f".format(value)
                    }
                )
            )
        )
    }

    LineChart(
        labelHelperProperties = LabelHelperProperties(
            enabled = true,textStyle = TextStyle.Default.copy(
                fontSize = appTypography.bodySmall.fontSize,
                color = appColors.onPrimaryContainer,
                fontWeight = appTypography.bodyLarge.fontWeight
            )
        ),
        modifier = Modifier
            .height(height)
            .padding(
                start = paddingStart,
                top = paddingTop,
                end = paddingEnd,
                bottom = paddingBottom
            ),
        data = lineData,
        maxValue = chartScaleConfig,
        animationMode = AnimationMode.Together { it * 100L },
        gridProperties = GridProperties(
            xAxisProperties = AxisProperties(
                enabled = true,
                lineCount = chartValues.size.coerceAtMost(10),
            ),
            yAxisProperties = AxisProperties(
                enabled = true,
                lineCount = 5,
            )
        ),

        indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            textStyle = TextStyle.Default.copy(
                fontSize = appTypography.bodySmall.fontSize,
                color = appColors.onSurface
            ),
            count = IndicatorCount.CountBased(count = 5)
        ),

        labelProperties = LabelProperties(
            enabled = true,
            textStyle = TextStyle.Default.copy(
                fontSize = appTypography.bodySmall.fontSize,
                color = appColors.onSurface
            ),
            labels = chartLabels,
            padding = 4.dp
        )
    )
}

@Composable
fun getChartScale(dataWrapper: ChartDataWrapper): Double {
    val chartScaleConfig = remember(dataWrapper) {
        val validValues = dataWrapper.getDataSet().data
            .map { it.verticalValue.toDouble().absoluteValue }
            .filter { it > 0.0 }
        if (validValues.isEmpty()) return@remember 0.0
        val max = validValues.maxOrNull() ?: 0.0
        val min = validValues.minOrNull() ?: 0.0
        val rawDelta = max - min
        val effectiveDelta = rawDelta.coerceAtLeast(max * 0.3)
        max + (effectiveDelta * 2)
    }
    return chartScaleConfig
}

private fun calculateDynamicDimensions(count: Int): Pair<Dp, Dp> {
    val maxItems = 31
    val minItems = 1

    // Thickness
    val maxThickness = 12.dp
    val minThickness = 3.dp
    // Spacing
    val maxSpacing = 20.dp
    val minSpacing = 4.dp
    val safeCount = count.coerceIn(minItems, maxItems)

    val fraction = (safeCount - minItems) / (maxItems - minItems).toFloat()
    val thickness = maxThickness - (maxThickness - minThickness) * fraction
    val spacing = maxSpacing - (maxSpacing - minSpacing) * fraction

    return thickness to spacing
}

private fun calculateDynamicDotSize(count: Int): Dp {
    val maxItems = 31
    val minItems = 1

    val maxThickness = 8.dp
    val minThickness = 2.dp
    val safeCount = count.coerceIn(minItems, maxItems)

    val fraction = (safeCount - minItems) / (maxItems - minItems).toFloat()
    val thickness = maxThickness - (maxThickness - minThickness) * fraction
    return thickness
}

