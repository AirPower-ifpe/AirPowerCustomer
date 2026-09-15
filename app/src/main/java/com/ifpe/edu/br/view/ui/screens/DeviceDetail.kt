// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.
package com.ifpe.edu.br.view.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.model.repository.model.DashboardFilters
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
import com.ifpe.edu.br.view.ui.components.ChartQueryDetails
import com.ifpe.edu.br.view.ui.components.FilterBottomSheet
import com.ifpe.edu.br.view.ui.components.HeaderWithSettings
import com.ifpe.edu.br.view.ui.components.MainChart
import com.ifpe.edu.br.view.ui.components.StatisticsRow
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun DeviceDetailScreen(
    deviceId: UUID,
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val scrollState = rememberScrollState()
    val device = mainViewModel.getDeviceById(deviceId.toString())
    val alarmInfoSet by mainViewModel.getAlarmInfoSet().collectAsState()

    val deviceAlarms = remember(deviceId, alarmInfoSet) {
        AirPowerUtil.getAlarmInfoForDeviceId(deviceId, alarmInfoSet)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DeviceInfoCard(device = device)

        DeviceTelemetryCard(
            deviceId = deviceId,
            viewModel = mainViewModel
        )

        DeviceAlarmsSection(alarms = deviceAlarms)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ---------------------------------------------------------------------------
// 1. Card de Identificação
// ---------------------------------------------------------------------------

@Composable
private fun DeviceInfoCard(device: DeviceSummary) {
    val isOnline = device.isActive
    val cardRadius = 18.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cardRadius))
            .border(
                width = 1.dp,
                color = if (isOnline) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.error.copy(alpha = 0.4f),
                shape = RoundedCornerShape(cardRadius)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        shape = RoundedCornerShape(cardRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(isOnline = isOnline)

                device.type?.takeIf { it.isNotBlank() }?.let { type ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = type.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = device.name.toTitleCase(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            val labelText = device.label?.takeIf { it.isNotBlank() } ?: "Localização não informada"
            Text(
                text = labelText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UUID: ${device.id.toString().take(8)}...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isOnline) "Transmissão contínua" else "Sem telemetria recente",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = if (isOnline) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(isOnline: Boolean) {
    val (dotColor, textColor, bgColor) = if (isOnline) {
        Triple(Color(0xFF2E7D32), Color(0xFF1B5E20), Color(0xFFE8F5E9))
    } else {
        Triple(Color(0xFFC62828), Color(0xFFB71C1C), Color(0xFFFFEBEE))
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isOnline) "ONLINE" else "OFFLINE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )
    }
}

// ---------------------------------------------------------------------------
// 2. Painel de Telemetria e Consumo
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceTelemetryCard(
    deviceId: UUID,
    viewModel: AirPowerViewModel
) {
    var activeFilters by remember { mutableStateOf(DashboardFilters(interval = TimeInterval.WEEK)) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var request by remember(activeFilters, deviceId) {
        mutableStateOf(
            AggregationRequest(
                devicesIds = listOf(deviceId.toString()),
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            HeaderWithSettings(
                title = "Consumo & Telemetria",
                deviceCount = 1,
                onSettingsClick = { showSheet = true }
            )

            Spacer(modifier = Modifier.height(4.dp))
            ChartQueryDetails(filters = activeFilters)

            Spacer(modifier = Modifier.height(10.dp))

            MainChart(
                aggregationState = aggregatedDataState,
                chartType = activeFilters.chartType,
                chartHeight = 220.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (aggregatedDataState is ResultWrapper.Success) {
                val wrapper = (aggregatedDataState as ResultWrapper.Success<AggDataWrapperResponse>).value.chartDataWrapper
                RefinedStatisticsBar(
                    dataWrapper = ChartDataWrapper(wrapper.label, wrapper.entries),
                    telemetryKey = activeFilters.telemetryKey
                )
            }
        }
    }

    if (showSheet) {
        FilterBottomSheet(
            sheetTitle = "Parâmetros do Gráfico",
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

// ---------------------------------------------------------------------------
// 3. Central de Alarmes do Dispositivo
// ---------------------------------------------------------------------------

@Composable
private fun DeviceAlarmsSection(alarms: List<AlarmInfo>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ocorrências do Medidor",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${alarms.size} evento(s)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (alarms.any { !it.cleared }) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (alarms.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma anomalia registrada para este medidor.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    alarms.sortedByDescending { it.createdTime }.take(5).forEach { alarm ->
                        DeviceAlarmItem(alarm = alarm)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceAlarmItem(alarm: AlarmInfo) {
    val isCleared = alarm.cleared
    val dateFormatted = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(alarm.createdTime))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alarm.type.toTitleCase(),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$dateFormatted • ${alarm.severity.uppercase()}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isCleared) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isCleared) "RESOLVIDO" else "ATIVO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    color = if (isCleared) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }
    }
}