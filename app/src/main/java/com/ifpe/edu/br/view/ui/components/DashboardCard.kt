// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.
package com.ifpe.edu.br.view.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomColumnChart
import com.ifpe.edu.br.common.components.CustomIconButton
import com.ifpe.edu.br.common.components.CustomLineChart
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.common.components.RectButton
import com.ifpe.edu.br.common.ui.theme.AirPowerTheme
import com.ifpe.edu.br.model.repository.model.ChartType
import com.ifpe.edu.br.model.repository.model.DashboardFilters
import com.ifpe.edu.br.model.repository.remote.dto.AlarmInfo
import com.ifpe.edu.br.model.repository.remote.dto.DashboardInfo
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggDataWrapperResponse
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggStrategy
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggregationRequest
import com.ifpe.edu.br.model.repository.remote.dto.agg.ChartDataWrapper
import com.ifpe.edu.br.model.repository.remote.dto.agg.TelemetryKey
import com.ifpe.edu.br.model.repository.remote.dto.agg.TimeInterval
import com.ifpe.edu.br.model.util.ResultWrapper
import com.ifpe.edu.br.view.ui.screens.SimpleRow
import com.ifpe.edu.br.view.ui.screens.formatDecimalBr
import com.ifpe.edu.br.view.ui.screens.getTimeWrapper
import com.ifpe.edu.br.view.ui.screens.toTitleCase
import com.ifpe.edu.br.viewmodel.AirPowerViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCard(
    dashboard: DashboardInfo,
    mainViewModel: AirPowerViewModel,
    allAlarms: List<AlarmInfo>
) {
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    var activeFilters by remember { mutableStateOf(DashboardFilters()) }
    var request by remember(activeFilters) {
        mutableStateOf(
            AggregationRequest(
                devicesIds = dashboard.devicesIds,
                aggStrategy = AggStrategy.AVG,
                aggKey = activeFilters.telemetryKey,
                timeIntervalWrapper = getTimeWrapper(
                    System.currentTimeMillis(),
                    activeFilters.interval
                )
            )
        )
    }

    val aggregatedDataState by mainViewModel.getAggregatedDataState(request).collectAsState()

    LaunchedEffect(request) {
        mainViewModel.fetchAggregatedData(request)
    }

    CustomCard(
        modifier = Modifier
            .clip(RoundedCornerShape(AirPowerTheme.dimens.cardCornerRadius))
            .background(AirPowerTheme.color.primaryContainer)
            .wrapContentSize(),
        layouts = listOf {
            CustomColumn(
                modifier = Modifier.fillMaxSize(),
                layouts = listOf {
                    HeaderWithSettings(
                        title = dashboard.title,
                        onSettingsClick = { showSheet = true }
                    )
                    ChartQueryDetails(activeFilters)
                    MainChart(
                        aggregationState = aggregatedDataState,
                        chartType = activeFilters.chartType
                    )
                    if (aggregatedDataState is ResultWrapper.Success) {
                        val wrapper =
                            (aggregatedDataState as ResultWrapper.Success).value.chartDataWrapper
                        StatisticsRow(
                            dataWrapper = ChartDataWrapper(wrapper.label, wrapper.entries),
                            telemetryKey = activeFilters.telemetryKey
                        )
                    }
                }
            )
        }
    )

    if (showSheet) {
        FilterBottomSheet(
            sheetTitle = dashboard.title,
            initialFilters = activeFilters,
            sheetState = sheetState,
            onDismiss = { showSheet = false },
            onApply = { newFilters ->
                activeFilters = newFilters
                showSheet = false
            }
        )
    }
}

val telemetryDisplayNames = mapOf(
    TelemetryKey.POWER to "Potência",
    TelemetryKey.CURRENT to "Corrente",
    TelemetryKey.VOLTAGE to "Tensão",
)
 val intervalLabels = mapOf(
    TimeInterval.DAY to "Hoje",
    TimeInterval.WEEK to "Esta Semana",
    TimeInterval.MONTH to "Este Mês",
    TimeInterval.YEAR to "Este Ano"
)
val chartTypesLabels = mapOf(
    ChartType.BAR to "Gráfico de coluna",
    ChartType.LINE to "Gráfico de linha"
)

@Composable
fun ChartQueryDetails(filters: DashboardFilters) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomText(
                    color = AirPowerTheme.color.onPrimaryContainer,
                    text = "Intervalo:",
                    fontStyle = AirPowerTheme.typography.bodyLarge
                )
                CustomText(
                    color = AirPowerTheme.color.onPrimaryContainer,
                    text = "${intervalLabels[filters.interval]}",
                    fontStyle = AirPowerTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomText(
                    color = AirPowerTheme.color.onPrimaryContainer,
                    text = "Dado:",
                    fontStyle = AirPowerTheme.typography.bodyLarge
                )
                CustomText(
                    color = AirPowerTheme.color.onPrimaryContainer,
                    text = "${telemetryDisplayNames[filters.telemetryKey]}",
                    fontStyle = AirPowerTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun MainChart(
    aggregationState: ResultWrapper<AggDataWrapperResponse>,
    paddingStart: Dp = 0.dp,
    paddingEnd: Dp = 0.dp,
    paddingTop: Dp = 0.dp,
    paddingBottom: Dp = 0.dp,
    chartHeight: Dp = 300.dp,
    chartType: ChartType = ChartType.BAR
) {
    CustomCard(
        paddingStart = paddingStart,
        paddingEnd = paddingEnd,
        paddingTop = paddingTop,
        paddingBottom = paddingBottom,
        layouts = listOf {
            CustomColumn(
                modifier = Modifier.fillMaxSize(),
                layouts = listOf {
                    when (aggregationState) {
                        is ResultWrapper.Success -> {
                            val chartDataWrapper = aggregationState.value.chartDataWrapper
                            key(chartDataWrapper) {
                                when (chartType) {
                                    ChartType.BAR -> {
                                        CustomColumnChart(
                                            height = chartHeight,
                                            dataWrapper = chartDataWrapper
                                        )
                                    }

                                    ChartType.LINE -> {
                                        CustomLineChart(
                                            height = chartHeight,
                                            dataWrapper = chartDataWrapper
                                        )
                                    }
                                }

                            }
                        }

                        is ResultWrapper.Empty -> {
                            LoadingCard()
                        }

                        else -> {
                            EmptyStateChart()
                        }
                    }
                }
            )
        }
    )
}

@Composable
private fun EmptyStateChart() {
    Box(
        modifier = Modifier
            .background(Color.Transparent)
    ) {
        CustomCard(
            layouts = listOf {
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                CustomText(
                    fontStyle = AirPowerTheme.typography.bodyLarge,
                    color = AirPowerTheme.color.onPrimaryContainer,
                    text = "Não há dados a exibir".toTitleCase(),
                )
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    initialFilters: DashboardFilters,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onApply: (DashboardFilters) -> Unit,
    sheetTitle: String
) {
    var draftFilters by remember { mutableStateOf(initialFilters) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AirPowerTheme.color.surface
    ) {
        Column(
            modifier = Modifier
                .padding(AirPowerTheme.dimens.paddingSmall)
                .fillMaxWidth()
        ) {
            SimpleRow(
                isCentered = true,
                layouts = listOf {
                    Spacer(modifier = Modifier.padding(horizontal = AirPowerTheme.dimens.paddingSmall))
                    CustomText(
                        text = sheetTitle,
                        fontStyle = AirPowerTheme.typography.displayMedium,
                        color = AirPowerTheme.color.onSurface
                    )
                }
            )

            Spacer(modifier = Modifier.padding(vertical = AirPowerTheme.dimens.paddingSmall))

            GenericDropdownSelector(
                items = ChartType.entries,
                selectedItem = draftFilters.chartType,
                onItemSelected = { newType ->
                    draftFilters = draftFilters.copy(chartType = newType)
                },
                label = "Estilo do gráfico",
                itemLabelMapper = { type ->
                    chartTypesLabels[type] ?: draftFilters.chartType.name
                }
            )

            GenericDropdownSelector(
                items = TimeInterval.entries,
                selectedItem = draftFilters.interval,
                onItemSelected = { newType ->
                    draftFilters = draftFilters.copy(interval = newType)
                },
                label = "Intervalo de Tempo",
                itemLabelMapper = { type ->
                    intervalLabels[type] ?: draftFilters.chartType.name
                }
            )

            GenericDropdownSelector(
                items = TelemetryKey.entries,
                selectedItem = draftFilters.telemetryKey,
                onItemSelected = { newType ->
                    draftFilters = draftFilters.copy(telemetryKey = newType)
                },
                label = "Tipo de dado",
                itemLabelMapper = { type ->
                    telemetryDisplayNames[type] ?: draftFilters.chartType.name
                }
            )

            Spacer(modifier = Modifier.padding(vertical = 16.dp))

            RectButton(
                text = "Aplicar Filtros",
                fontStyle = AirPowerTheme.typography.button,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary
                ),
                onClick = { onApply(draftFilters) }
            )

            Spacer(modifier = Modifier.padding(vertical = 24.dp))
        }
    }
}

@Composable
fun HeaderWithSettings(
    title: String,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomText(
            color = AirPowerTheme.color.onPrimaryContainer,
            text = title,
            fontStyle = AirPowerTheme.typography.displayMedium
        )

        CustomIconButton(
            iconResId = com.ifpe.edu.br.R.drawable.filter,
            iconTint = AirPowerTheme.color.onSecondaryContainer,
            backgroundColor = Color.Transparent,
            onClick = onSettingsClick,
            contentDescription = "filtering button",
            modifier = Modifier.size(45.dp)
        )
    }
}

@Composable
fun StatItem(
    label: String,
    value: Double,
    unit: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomText(
            text = label.toTitleCase(),
            fontStyle = AirPowerTheme.typography.bodyLarge,
            color = color,
        )
        CustomText(
            text = value.toString().formatDecimalBr() + unit,
            color = color,
            fontStyle = AirPowerTheme.typography.bodySmall
        )
    }
}

@Composable
fun StatisticsRow(
    dataWrapper: ChartDataWrapper,
    telemetryKey: TelemetryKey
) {
    val stats = remember(dataWrapper) {
        // Converte cada valor Long para Double diretamente via .toDouble()
        val values = dataWrapper.entries.map { it.value.toDouble() }
        if (values.isEmpty()) return@remember null
        val max = values.maxOrNull() ?: 0.0
        val min = values.minOrNull() ?: 0.0
        val avg = values.average()
        Triple(min, avg, max)
    } ?: return

    val (min, avg, max) = stats
    val unit = when (telemetryKey) {
        TelemetryKey.POWER -> " W"
        TelemetryKey.VOLTAGE -> " V"
        TelemetryKey.CURRENT -> " A"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AirPowerTheme.dimens.cardCornerRadius))
            .background(AirPowerTheme.color.secondaryContainer)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(
            label = "Mínimo",
            value = min,
            unit = unit,
            color = AirPowerTheme.color.onSecondaryContainer
        )
        StatItem(
            label = "Média",
            value = avg,
            unit = unit,
            color = AirPowerTheme.color.onSecondaryContainer
        )
        StatItem(
            label = "Máximo",
            value = max,
            unit = unit,
            color = AirPowerTheme.color.onSecondaryContainer
        )
    }
}