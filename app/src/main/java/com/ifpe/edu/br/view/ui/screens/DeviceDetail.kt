// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.
package com.ifpe.edu.br.view.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomColumnChart
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.common.ui.theme.AirPowerTheme
import com.ifpe.edu.br.model.repository.model.DashboardFilters
import com.ifpe.edu.br.model.repository.model.HomeScreenAlarmSummaryCard
import com.ifpe.edu.br.model.repository.remote.dto.AlarmInfo
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggDataWrapperResponse
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggStrategy
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggregationRequest
import com.ifpe.edu.br.model.repository.remote.dto.agg.ChartDataWrapper
import com.ifpe.edu.br.model.repository.remote.dto.agg.TelemetryKey
import com.ifpe.edu.br.model.repository.remote.dto.agg.TimeInterval
import com.ifpe.edu.br.model.util.AirPowerUtil
import com.ifpe.edu.br.model.util.ResultWrapper
import com.ifpe.edu.br.view.ui.components.AlarmCardInfo
import com.ifpe.edu.br.view.ui.components.ChartQueryDetails
import com.ifpe.edu.br.view.ui.components.EmptyStateCard
import com.ifpe.edu.br.view.ui.components.FilterBottomSheet
import com.ifpe.edu.br.view.ui.components.HeaderWithSettings
import com.ifpe.edu.br.view.ui.components.MainChart
import com.ifpe.edu.br.view.ui.components.StatisticsRow
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import java.util.UUID


@Composable
fun DeviceDetailScreen(
    deviceId: UUID,
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {

    val deviceDetailAddRequest = AggregationRequest(
        devicesIds = listOf(deviceId.toString()),
        aggStrategy = AggStrategy.AVG,
        aggKey = TelemetryKey.POWER,
        timeIntervalWrapper = getTimeWrapper(
            System.currentTimeMillis(),
            TimeInterval.WEEK
        )
    )

    val aggregationState =
        mainViewModel.getAggregatedDataState(deviceDetailAddRequest).collectAsState()
    val scrollState = rememberScrollState()
    val device = mainViewModel.getDeviceById(deviceId.toString())
    val alarmInfoSet = mainViewModel.getAlarmInfoSet().collectAsState()

    LaunchedEffect(deviceId) {
        mainViewModel.fetchAggregatedData(deviceDetailAddRequest)
    }

    CustomColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
        layouts = listOf {
            DeviceInfoCard(device)
            DeviceConsumptionCard(
                deviceID = deviceId,
                viewModel = mainViewModel
            )

            val deviceAlarms = remember(deviceId, alarmInfoSet.value) {
                AirPowerUtil.getAlarmInfoForDeviceId(deviceId, alarmInfoSet.value)
            }

            Container(
                layouts = listOf {
                    //AlarmSection(deviceAlarms)
                }
            )
        }
    )
}

@Composable
private fun AlarmsCard(
    alarmCards: List<AlarmInfo>
) {
    val context = LocalContext.current
    CustomCard(
        paddingStart = 15.dp,
        paddingEnd = 15.dp,
        paddingTop = 5.dp,
        paddingBottom = 10.dp,
        layouts = listOf {
            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                CustomText(
                    color = AirPowerTheme.color.onPrimaryContainer,
                    fontStyle = AirPowerTheme.typography.bodyLarge,
                    text = "Alarmes do dispositivo",
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 4.dp))


            DeviceDetailAlarmGrid(alarmCards) {
                Toast.makeText(
                    context,
                    "Essa funcionalidade está em desenvolvimento",
                    Toast.LENGTH_SHORT
                ).show()
            }

            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                CustomText(
                    modifier = Modifier.clickable {
                        Toast.makeText(
                            context,
                            "Essa funcionalidade está em desenvolvimento",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    color = AirPowerTheme.color.onPrimaryContainer,
                    fontStyle = AirPowerTheme.typography.bodyLarge,
                    text = "Detalhes",
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceConsumptionCard(
    deviceID: UUID,
    viewModel: AirPowerViewModel
) {
    var activeFilters by remember { mutableStateOf(DashboardFilters()) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var request by remember(activeFilters) {
        mutableStateOf(
            AggregationRequest(
                devicesIds = listOf(deviceID.toString()),
                aggStrategy = AggStrategy.AVG,
                aggKey = activeFilters.telemetryKey,
                timeIntervalWrapper = getTimeWrapper(
                    System.currentTimeMillis(),
                    activeFilters.interval
                )
            )
        )
    }

    val aggregatedDataState by viewModel.getAggregatedDataState(request).collectAsState()

    LaunchedEffect(request) {
        viewModel.fetchAggregatedData(request)
    }

    CustomCard(
        modifier = Modifier
            .clip(RoundedCornerShape(AirPowerTheme.dimens.cardCornerRadius))
            .fillMaxWidth()
            .background(AirPowerTheme.color.primaryContainer),
        paddingStart = AirPowerTheme.dimens.paddingSmall,
        paddingEnd = AirPowerTheme.dimens.paddingSmall,
        paddingTop = AirPowerTheme.dimens.paddingSmall,
        paddingBottom = AirPowerTheme.dimens.paddingSmall,
        layouts = listOf {
            CustomColumn(
                layouts = listOf {
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    HeaderWithSettings(
                        title = "Consumo do dispositivo",
                        onSettingsClick = { showSheet = true }
                    )
                    ChartQueryDetails(activeFilters)
                    MainChart(
                        aggregationState = aggregatedDataState,
                        chartType = activeFilters.chartType
                    )
                    if (aggregatedDataState is ResultWrapper.Success) {
                        val wrapper = (aggregatedDataState as ResultWrapper.Success<AggDataWrapperResponse>).value.chartDataWrapper
                        StatisticsRow(
                            dataWrapper = ChartDataWrapper(wrapper.label, wrapper.entries),
                            telemetryKey = activeFilters.telemetryKey
                        )
                    }

                    Spacer(modifier = Modifier.padding(vertical = AirPowerTheme.dimens.paddingSmall))
                }
            )

            if (showSheet) {
                FilterBottomSheet(
                    sheetTitle = "Busca personalizada",
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
    )
}

@Composable
fun SectionTitle(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        CustomText(
            text = text,
            color = AirPowerTheme.color.onPrimaryContainer,
            fontStyle = AirPowerTheme.typography.displayMedium
        )
    }
}

@Composable
private fun DeviceInfoCard(device: DeviceSummary) {
    CustomCard(
        modifier = Modifier
            .clip(RoundedCornerShape(AirPowerTheme.dimens.cardCornerRadius))
            .fillMaxWidth()
            .background(AirPowerTheme.color.primaryContainer),
        paddingStart = AirPowerTheme.dimens.paddingSmall,
        paddingEnd = AirPowerTheme.dimens.paddingSmall,
        paddingTop = AirPowerTheme.dimens.paddingSmall,
        paddingBottom = AirPowerTheme.dimens.paddingSmall,
        layouts = listOf {
            CustomColumn(
                layouts = listOf {
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    SectionTitle("Informaçõs do dispositivo")

                    Spacer(modifier = Modifier.padding(vertical = 12.dp))

                    CardRow(
                        label = "Nome:",
                        content = device.name.toTitleCase()
                    )

                    Spacer(modifier = Modifier.padding(vertical = 4.dp))

                    CardRow(
                        label = "Status dispositivo:",
                        content = if (device.isActive) "Online" else "Offline"
                    )

                    Spacer(modifier = Modifier.padding(AirPowerTheme.dimens.paddingSmall))
                }
            )
        }
    )
}

@Composable
private fun CardRow(
    label: String,
    content: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        CustomText(
            color = AirPowerTheme.color.onPrimaryContainer,
            fontStyle = AirPowerTheme.typography.bodyLarge,
            text = label
        )

        val contentColor =
            if ("offline" == (content.toLowerCase(Locale.current)))
                AirPowerTheme.color.secondary else AirPowerTheme.color.onSecondaryContainer
        CustomText(
            text = content,
            color = contentColor,
            fontStyle = AirPowerTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun DeviceDetailAlarmGrid(
    alarmInfoSet: List<AlarmInfo>,
    onClick: (String) -> Unit
) {
    val severityAggregationMap: MutableMap<String, Int> = mutableMapOf()
    alarmInfoSet.forEach { item ->
        val severity = item.severity
        val occurrence = severityAggregationMap[severity] ?: 0
        severityAggregationMap[severity] = occurrence + 1
    }

    val cards: List<HomeScreenAlarmSummaryCard> =
        severityAggregationMap.map { (severity, count) ->
            HomeScreenAlarmSummaryCard(severity, count)
        }
    val gridCount = if (cards.size > 3) 2 else 3
    val cardHeight = if (gridCount == 2) 200.dp else 140.dp

    LazyVerticalGrid(
        columns = GridCells.Fixed(gridCount),
        modifier = Modifier
            .height(cardHeight)
            .fillMaxWidth(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(cards, key = { it.severity }) { deviceItem ->
            AlarmCardInfo(
                alarmCardInfo = deviceItem,
                onClick = onClick,
                backgroundColor = AirPowerTheme.color.primaryContainer
            )
        }
    }
}