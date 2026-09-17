// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.
package com.ifpe.edu.br.view.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomColumnChart
import com.ifpe.edu.br.common.components.CustomIconButton
import com.ifpe.edu.br.common.components.CustomLineChart
import com.ifpe.edu.br.common.components.CustomText
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
import com.ifpe.edu.br.view.ui.screens.RefinedStatisticsBar
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
                        deviceCount = dashboard.devicesIds.size,
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
                        RefinedStatisticsBar(
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChartQueryDetails(
    filters: DashboardFilters,
    modifier: Modifier = Modifier
) {
    val intervalText = intervalLabels[filters.interval] ?: filters.interval.name
    val metricText = telemetryDisplayNames[filters.telemetryKey] ?: filters.telemetryKey.name
    val chartTypeText = when (filters.chartType) {
        ChartType.BAR -> "Colunas"
        ChartType.LINE -> "Linhas"
    }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FilterQueryBadge(
            label = "Período",
            value = intervalText,
            indicatorColor = MaterialTheme.colorScheme.primary
        )

        FilterQueryBadge(
            label = "Dado",
            value = metricText,
            indicatorColor = Color(0xFFFB8C00) // Laranja / Âmbar
        )

        FilterQueryBadge(
            label = "Estilo",
            value = chartTypeText,
            indicatorColor = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun FilterQueryBadge(
    label: String,
    value: String,
    indicatorColor: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(indicatorColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = sheetTitle.ifBlank { "Personalizar Visualização" },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Selecione as dimensões para a agregação temporal",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            FilterGroupTitle(text = "Grandeza Elétrica")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TelemetryKey.entries.forEach { key ->
                    val isSelected = draftFilters.telemetryKey == key
                    val label = telemetryDisplayNames[key] ?: key.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { draftFilters = draftFilters.copy(telemetryKey = key) },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FilterGroupTitle(text = "Intervalo Temporal")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TimeInterval.entries.forEach { interval ->
                    val isSelected = draftFilters.interval == interval
                    val label = intervalLabels[interval] ?: interval.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { draftFilters = draftFilters.copy(interval = interval) },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FilterGroupTitle(text = "Tipo de Gráfico")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChartType.entries.forEach { chartType ->
                    val isSelected = draftFilters.chartType == chartType
                    val label = when (chartType) {
                        ChartType.BAR -> "Colunas"
                        ChartType.LINE -> "Linhas"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { draftFilters = draftFilters.copy(chartType = chartType) },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Cancelar", style = MaterialTheme.typography.labelLarge)
                }

                Button(
                    onClick = { onApply(draftFilters) },
                    modifier = Modifier.weight(1.4f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Aplicar Filtros",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FilterGroupTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun HeaderWithSettings(
    title: String,
    deviceCount: Int,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title.ifBlank { "Dashboard" },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (deviceCount == 1) "1 medidor vinculado" else "$deviceCount medidores vinculados",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        CustomIconButton(
            iconResId = com.ifpe.edu.br.R.drawable.filter,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            backgroundColor = Color.Transparent,
            onClick = onSettingsClick,
            contentDescription = "Filtros do dashboard",
            modifier = Modifier.size(40.dp)
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