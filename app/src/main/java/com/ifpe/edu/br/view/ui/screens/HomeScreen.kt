/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

package com.ifpe.edu.br.view.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.ui.theme.AirPowerTheme
import com.ifpe.edu.br.model.repository.model.ChartType
import com.ifpe.edu.br.model.repository.remote.dto.AlarmInfo
import com.ifpe.edu.br.model.repository.remote.dto.DevicesStatusSummary
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggDataWrapperResponse
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggStrategy
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggregationRequest
import com.ifpe.edu.br.model.repository.remote.dto.agg.ChartDataWrapper
import com.ifpe.edu.br.model.repository.remote.dto.agg.TelemetryKey
import com.ifpe.edu.br.model.repository.remote.dto.agg.TimeInterval
import com.ifpe.edu.br.model.repository.remote.dto.agg.TimeIntervalWrapper
import com.ifpe.edu.br.model.util.ResultWrapper
import com.ifpe.edu.br.view.ui.components.MainChart
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Date
import java.util.Locale

private val telemetryKey = TelemetryKey.POWER

@Composable
fun HomeScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val scrollState = rememberScrollState()
    val alarmInfo = mainViewModel.getAlarmInfoSet().collectAsState()
    val allDeviceIds =
        mainViewModel.getDevicesSummary().collectAsState().value.map { it.id.toString() }
    val isRefreshing by mainViewModel.isRefreshing.collectAsState()

    val homeScreenRequest = AggregationRequest(
        devicesIds = allDeviceIds,
        aggStrategy = AggStrategy.AVG,
        aggKey = telemetryKey,
        timeIntervalWrapper = getTimeWrapper(
            System.currentTimeMillis(),
            TimeInterval.MONTH
        )
    )

    val aggregationState = mainViewModel.getAggregatedDataState(homeScreenRequest).collectAsState()

    LaunchedEffect(allDeviceIds) {
        if (allDeviceIds.isNotEmpty()) {
            mainViewModel.fetchAggregatedData(homeScreenRequest)
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { mainViewModel.forceRefresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Resumo Superior de Energia e Dispositivos
            when (val agg = aggregationState.value) {
                is ResultWrapper.Success -> {
                    OverviewSection(
                        aggData = agg.value,
                        statusSummaries = agg.value.statusSummaries
                    )
                }
                else -> {}
            }

            when (val agg = aggregationState.value) {
                is ResultWrapper.Success -> {
                    ChartSection(agg)
                }
                else -> {}
            }

            if (alarmInfo.value.isNotEmpty()) {
                AlarmSection(
                    alarmsInfo = alarmInfo.value,
                    onViewAllClick = { navController.navigate("alarms") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Seção de Resumo Superior (Métricas Principais)
// ---------------------------------------------------------------------------

@Composable
private fun OverviewSection(
    aggData: AggDataWrapperResponse,
    statusSummaries: List<DevicesStatusSummary>
) {
    val unit = aggData.chartDataWrapper.label.ifBlank { "kWh" }
    val totalDevices = aggData.size
    val activeCount = statusSummaries.firstOrNull { it.label.contains("ativo", ignoreCase = true) }?.occurrence
        ?: totalDevices

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Card Principal: Consumo do Período
        Card(
            modifier = Modifier.weight(1.3f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = "Consumo no Mês",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = aggData.aggregation.value.formatDecimalBr(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = "Medidores",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$totalDevices instalados",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color(0xFF2E7D32), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$activeCount online",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Seção do Gráfico com Régua Refinada de Min/Méd/Max
// ---------------------------------------------------------------------------

@Composable
fun ChartSection(value: ResultWrapper.Success<AggDataWrapperResponse>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Curva de Carga Agregada",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Watts (W)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // MainChart mantido inalterado
            MainChart(
                chartHeight = 220.dp,
                aggregationState = value,
                chartType = ChartType.LINE
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Régua de Estatísticas Refinada
            RefinedStatisticsBar(
                dataWrapper = value.value.chartDataWrapper,
                telemetryKey = telemetryKey
            )
        }
    }
}

@Composable
fun RefinedStatisticsBar(
    dataWrapper: ChartDataWrapper,
    telemetryKey: TelemetryKey
) {
    val stats = remember(dataWrapper) {
        val values = dataWrapper.entries.map { it.value }
        if (values.isEmpty()) return@remember null
        val max = values.maxOrNull() ?: 0.0
        val min = values.minOrNull() ?: 0.0
        val avg = values.average()
        Triple(min, avg, max)
    } ?: return

    val (min, avg, max) = stats
    val unit = when (telemetryKey) {
        TelemetryKey.POWER -> "W"
        TelemetryKey.VOLTAGE -> "V"
        TelemetryKey.CURRENT -> "A"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatMetricColumn(
                label = "MÍNIMO",
                value = min.toDouble().formatDecimalBr(),
                unit = unit,
                indicatorColor = Color(0xFF43A047) // Verde
            )

            VerticalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 0.7.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            StatMetricColumn(
                label = "MÉDIA",
                value = avg.formatDecimalBr(),
                unit = unit,
                indicatorColor = MaterialTheme.colorScheme.primary // Azul
            )

            VerticalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 0.7.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            StatMetricColumn(
                label = "MÁXIMO",
                value = max.toDouble().formatDecimalBr(),
                unit = unit,
                indicatorColor = Color(0xFFFB8C00) // Âmbar / Laranja
            )
        }
    }
}

@Composable
private fun StatMetricColumn(
    label: String,
    value: String,
    unit: String,
    indicatorColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(indicatorColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Seção de Alarmes Refatorada
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmSection(
    alarmsInfo: List<AlarmInfo>,
    onViewAllClick: () -> Unit
) {
    val alarmDashboardData = remember(alarmsInfo) { processAlarms(alarmsInfo) }
    val activeCount = remember(alarmsInfo) { alarmsInfo.count { !it.cleared } }
    val criticalCount = remember(alarmsInfo) {
        alarmsInfo.count { it.severity.equals("CRITICAL", ignoreCase = true) }
    }
    val recentAlarms = remember(alarmsInfo) {
        alarmsInfo.sortedByDescending { it.createdTime }.take(2)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onViewAllClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ocorrências & Alarmes",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$activeCount ocorrência(s) ativa(s)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (criticalCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Ver todos →",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val severities = getExistingSeverities(alarmsInfo)
                severities.forEach { sev ->
                    val count = alarmDashboardData.bySeverity[sev]?.size ?: 0
                    SeverityChip(severity = sev, count = count)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Preview dos Alarmes Mais Recentes
            Text(
                text = "Últimos Eventos Registrados",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            recentAlarms.forEach { alarm ->
                AlarmMiniRow(alarm = alarm)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun AlarmMiniRow(alarm: AlarmInfo) {
    val isCleared = alarm.cleared
    val deviceName = alarm.originatorName?.takeIf { it.isNotBlank() } ?: "Dispositivo"

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alarm.type.toTitleCase(),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$deviceName • ${formatAlarmDate(alarm.createdTime)}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isCleared) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
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

private fun formatAlarmDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

@Composable
private fun SeverityChip(severity: String, count: Int) {
    val (bgColor, textColor, borderColor) = when (severity.uppercase()) {
        "CRITICAL" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Color(0xFFFFCDD2))
        "MAJOR", "HIGH" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), Color(0xFFFFE0B2))
        "MINOR", "WARNING" -> Triple(Color(0xFFFFFDE7), Color(0xFFF57F17), Color(0xFFFFF9C4))
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            Color.Transparent
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(0.6.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = severity.toTitleCase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = textColor
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(textColor.copy(alpha = 0.15f))
                .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                color = textColor
            )
        }
    }
}

@Composable
private fun AlarmTypeTag(type: String, count: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = type.toTitleCase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "($count)",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------
// Helpers e Funções de Tempo (Mantidos Originais)
// ---------------------------------------------------------------------------

fun String.toTitleCase(): String {
    if (this.isBlank()) return this
    return this.lowercase()
        .split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }
}

fun String.formatDecimalBr(): String {
    val number = this.toDoubleOrNull() ?: 0.0
    return number.formatDecimalBr()
}

fun Double.formatDecimalBr(): String {
    val ptBr = Locale("pt", "BR")
    val formatter = NumberFormat.getNumberInstance(ptBr).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return formatter.format(this)
}

private fun getExistingSeverities(alarms: List<AlarmInfo>): List<String> {
    return alarms.map { it.severity }.distinct().sorted()
}

private fun getExistingTypes(alarms: List<AlarmInfo>): List<String> {
    return alarms.map { it.type }.distinct().sorted()
}

data class AlarmDashboardData(
    val bySeverity: Map<String, List<AlarmInfo>> = emptyMap(),
    val byType: Map<String, List<AlarmInfo>> = emptyMap(),
)

fun processAlarms(alarmsInfo: List<AlarmInfo>): AlarmDashboardData {
    return AlarmDashboardData(
        bySeverity = alarmsInfo.groupBy { it.severity },
        byType = alarmsInfo.groupBy { it.type },
    )
}

fun getTimeWrapper(
    refEpochMillis: Long,
    timeInterval: TimeInterval
): TimeIntervalWrapper {
    val rawStart = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(refEpochMillis),
        ZoneId.systemDefault()
    ).withNano(0)

    val startTs = when (timeInterval) {
        TimeInterval.DAY -> rawStart.truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli()
        TimeInterval.WEEK -> rawStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli()
        TimeInterval.MONTH -> rawStart.with(TemporalAdjusters.firstDayOfMonth())
            .truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli()
        TimeInterval.YEAR -> rawStart.with(TemporalAdjusters.firstDayOfYear())
            .truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli()
    }

    return TimeIntervalWrapper(startTs, timeInterval)
}

@Composable
fun SimpleRow(
    layouts: List<@Composable () -> Unit>,
    isCentered: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCentered) Arrangement.Center else Arrangement.Start
    ) {
        layouts.forEach { it() }
    }
}

@Composable
fun Container(
    layouts: List<@Composable () -> Unit>
) {
    val dimens = AirPowerTheme.dimens
    CustomCard(
        paddingStart = dimens.paddingSmall,
        paddingEnd = dimens.paddingSmall,
        paddingTop = dimens.paddingSmall,
        paddingBottom = dimens.paddingMedium,
        modifier = Modifier
            .clip(RoundedCornerShape(dimens.cardCornerRadius))
            .background(AirPowerTheme.color.primaryContainer)
            .fillMaxWidth(),
        layouts = layouts
    )
}