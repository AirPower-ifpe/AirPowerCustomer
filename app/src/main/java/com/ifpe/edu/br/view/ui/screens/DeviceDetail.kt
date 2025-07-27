package com.ifpe.edu.br.view.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomBarChart
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomText
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
import com.ifpe.edu.br.view.ui.components.EmptyStateCard
import com.ifpe.edu.br.view.ui.components.getStatusColor
import com.ifpe.edu.br.view.ui.theme.app_default_solid_background_light
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import java.util.UUID


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


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
            TimeInterval.MONTH
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
                aggregationState = aggregationState.value
            )

            val deviceAlarms = remember(deviceId, alarmInfoSet.value) {
                AirPowerUtil.getAlarmInfoForDeviceId(deviceId, alarmInfoSet.value)
            }
            AlarmsCard(deviceAlarms)
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
                    color = tb_primary_light,
                    text = "Alarmes do dispositivo",
                    fontSize = 20.sp
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
                    color = tb_primary_light,
                    text = "Detalhes",
                    fontSize = 12.sp
                )
            }
        }
    )
}

@Composable
private fun DeviceConsumptionCard(
    aggregationState: ResultWrapper<AggDataWrapperResponse>
) {
    CustomCard(
        paddingStart = 15.dp,
        paddingEnd = 15.dp,
        paddingTop = 5.dp,
        paddingBottom = 5.dp,
        layouts = listOf {
            CustomColumn(
                layouts = listOf {
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        CustomText(
                            color = tb_primary_light,
                            text = "Consumo do dispositivo",
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.padding(vertical = 12.dp))

                    if (aggregationState is ResultWrapper.Success) {
                        CustomBarChart(
                            height = 300.dp,
                            dataWrapper = ChartDataWrapper(
                                aggregationState.value.chartDataWrapper.label,
                                aggregationState.value.chartDataWrapper.entries
                            )
                        )
                    } else {
                        EmptyStateCard()
                    }
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                }
            )
        }
    )
}

@Composable
private fun DeviceInfoCard(device: DeviceSummary) {
    CustomCard(
        paddingStart = 15.dp,
        paddingEnd = 15.dp,
        paddingTop = 10.dp,
        paddingBottom = 5.dp,
        layouts = listOf {
            CustomColumn(
                layouts = listOf {

                    Spacer(modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        CustomText(
                            color = tb_primary_light,
                            text = "Informaçõs do dispositivo",
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.padding(vertical = 12.dp))

                    CardRow(
                        label = "Nome:",
                        content = device.label.orEmpty()
                    )

                    Spacer(modifier = Modifier.padding(vertical = 4.dp))

                    CardRow(
                        label = "Perfil do dispositivo:",
                        content = ""
                    )

                    Spacer(modifier = Modifier.padding(vertical = 4.dp))

                    CardRow(
                        label = "ID do dispositivo:",
                        content = device.name
                    )

                    Spacer(modifier = Modifier.padding(vertical = 4.dp))

                    CardRow(
                        label = "Status dispositivo:",
                        content = if (device.isActive) "Online" else "Offline"
                    )

                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
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
            color = tb_primary_light,
            text = label
        )
        CustomText(
            color = getStatusColor(content),
            text = content,
            fontWeight = FontWeight.Thin,
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
                backgroundColor = app_default_solid_background_light
            )
        }
    }
}