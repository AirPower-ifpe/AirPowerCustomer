package com.ifpe.edu.br.view.ui.screens

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomBarChart
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.model.HomeScreenAlarmSummaryCard
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
import com.ifpe.edu.br.view.ui.components.AlarmCardInfo
import com.ifpe.edu.br.view.ui.components.CardInfo
import com.ifpe.edu.br.view.ui.components.EmptyStateCard
import com.ifpe.edu.br.view.ui.components.LoadingCard
import com.ifpe.edu.br.view.ui.theme.app_default_solid_background_light
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.view.ui.theme.tb_secondary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@Composable
fun HomeScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val scrollState = rememberScrollState()
    val alarmInfo = mainViewModel.getAlarmInfoSet().collectAsState()
    val allDeviceIds =
        mainViewModel.getDevicesSummary().collectAsState().value.map { it.id.toString() }

    /*
     * this should me dynamic on future releases
     */
    val homeScreenRequest = AggregationRequest(
        devicesIds = allDeviceIds,
        aggStrategy = AggStrategy.AVG,
        aggKey = TelemetryKey.POWER,
        timeIntervalWrapper = getTimeWrapper(
            System.currentTimeMillis(),
            TimeInterval.YEAR
        )
    )

    val aggregationState = mainViewModel.getAggregatedDataState(homeScreenRequest).collectAsState()

    LaunchedEffect(allDeviceIds) {
        if (allDeviceIds.isNotEmpty()) {
            mainViewModel.fetchAggregatedData(homeScreenRequest)
        }
    }

    CustomColumn(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize(),
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_TOP,
        layouts = listOf {
            DevicesConsumptionSummaryCardBoard(
                aggregationState = aggregationState.value,
                alarmInfo = alarmInfo.value
            )
            AlarmsSummaryCardCardBoard(
                alarmInfo.value
            )
            SummaryCardCardBoard(
                aggregationState.value,
                viewModel = mainViewModel
            )
        }
    )
}

@Composable
fun SummaryCardCardBoard(
    resultWrapper: ResultWrapper<AggDataWrapperResponse>,
    viewModel: AirPowerViewModel

) {
    val aggKey = Constants.UIStateKey.AGG_DATA_KEY
    val aggDataState = viewModel.uiStateManager.observeUIState(aggKey).collectAsState()
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
                    text = "Staus dos dispositivos",
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            if (aggDataState.value.state == Constants.UIState.STATE_LOADING) {
                LoadingCard()
            } else {
                if (resultWrapper is ResultWrapper.Success) {
                    DevicesStatusGrid(resultWrapper.value.statusSummaries) {
                        Toast.makeText(
                            context,
                            "Essa funcionalidade está em desenvolvimento",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    EmptyStateCard()
                }
            }
        }
    )
}

@Composable
private fun AlarmsSummaryCardCardBoard(
    alarmInfo: List<AlarmInfo>
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

            if (alarmInfo != null && alarmInfo.isNotEmpty()) {
                HomeScreenAlarmGrid(alarmInfo) {
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
            } else {
                EmptyStateCard()
            }
        }
    )
}

@Composable
fun DevicesConsumptionSummaryCardBoard(
    aggregationState: ResultWrapper<AggDataWrapperResponse>,
    alarmInfo: List<AlarmInfo>
) {
    val context = LocalContext.current
    val totalAlarmCount = alarmInfo.size ?: 0
    CustomCard(
        paddingStart = 15.dp,
        paddingEnd = 15.dp,
        paddingTop = 5.dp,
        paddingBottom = 5.dp,
        layouts = listOf {
            CustomColumn(
                modifier = Modifier.fillMaxSize(),
                layouts = listOf {
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        CustomText(
                            color = tb_primary_light,
                            text = "Consumo de todos os dispositivos",
                            fontSize = 20.sp
                        )
                    }

                    if (aggregationState is ResultWrapper.Success) {
                        ConsumptionSummaryCard(totalAlarmCount, aggregationState.value)
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
                    } else {
                        EmptyStateCard()
                    }
                }
            )
        }
    )
}

@Composable
private fun ConsumptionSummaryCard(
    totalAlarmCount: Int,
    aggDataWrapperResponse: AggDataWrapperResponse
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        CustomColumn(
            modifier = Modifier.width(110.dp),
            layouts = listOf {
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                SummaryCard("alarmes", "$totalAlarmCount", onClick = {})
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                SummaryCard(
                    aggDataWrapperResponse.label,
                    aggDataWrapperResponse.aggregation.value,
                    onClick = {})
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                SummaryCard(
                    "Dispositivos",
                    aggDataWrapperResponse.size.toString(),
                    onClick = {})
            })

        Spacer(modifier = Modifier.padding(horizontal = 10.dp))
        CustomColumn(
            modifier = Modifier.fillMaxSize(),
            layouts = listOf {
                Spacer(modifier = Modifier.padding(vertical = 12.dp))
                CustomBarChart(
                    height = 300.dp,
                    dataWrapper = ChartDataWrapper(
                        aggDataWrapperResponse.chartDataWrapper.label,
                        aggDataWrapperResponse.chartDataWrapper.entries
                    )
                )
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
            })
    }
}


@Composable
private fun SummaryCard(
    label: String,
    data: String,
    onClick: () -> Unit,
    backgroundColor: Color = app_default_solid_background_light,
    textColor: Color = tb_primary_light,
    fontWeight: FontWeight = FontWeight.Light
) {
    CustomCard(
        modifier = Modifier
            .clip(RoundedCornerShape(cardCornerRadius))
            .fillMaxWidth()
            .wrapContentHeight()
            .background(backgroundColor)
            .clickable { onClick() },
        layouts = listOf {
            CustomColumn(
                modifier = Modifier.fillMaxSize(),
                layouts = listOf {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CustomColumn(
                            modifier = Modifier.wrapContentSize(),
                            layouts = listOf {
                                CustomText(
                                    text = label,
                                    alignment = TextAlign.Center,
                                    fontWeight = fontWeight,
                                    fontSize = 12.sp,
                                    color = textColor,
                                    modifier = Modifier.wrapContentWidth()
                                )
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CustomColumn(
                            modifier = Modifier.fillMaxSize(),
                            layouts = listOf {
                                CustomText(
                                    text = data,
                                    alignment = TextAlign.Center,
                                    fontWeight = fontWeight,
                                    fontSize = 12.sp,
                                    color = tb_secondary_light,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(all = 0.dp)
                                )
                            }
                        )
                    }

                }
            )
        }
    )
}

@Composable
private fun HomeScreenAlarmGrid(
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
    var cardHeight = if (gridCount == 2) 160.dp else 140.dp
    if (cards.size > 6) {
        cardHeight = 260.dp
    }

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

@Composable
private fun DevicesStatusGrid(
    statusSummaries: List<DevicesStatusSummary>,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    if (statusSummaries != null && statusSummaries.isNotEmpty()) {
        val gridCount = if (statusSummaries.size > 3) 2 else 3
        var cardHeight = if (gridCount == 2) 160.dp else 140.dp
        if (statusSummaries.size > 6) {
            cardHeight = 260.dp
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(gridCount),
            modifier = Modifier
                .height(cardHeight)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(statusSummaries) { deviceItem ->
                CardInfo(
                    label = deviceItem.label,
                    value = deviceItem.occurrence.toString(),
                    onClick = onClick,
                    backgroundColor = app_default_solid_background_light
                )
            }
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
    } else {
        EmptyStateCard()
    }
}

/**
 * Calcula a data de início de um intervalo de tempo com base em um timestamp de referência,
 * retornando um [TimeIntervalWrapper] que representa o início do período e o tipo de agregação desejado.
 *
 * Essa função é utilizada para padronizar o alinhamento temporal dos dados de telemetria
 * agregados que serão consultados via API do ThingsBoard, como médias por hora, por dia etc.
 *
 * Exemplo de uso: ao selecionar o intervalo "WEEK", o timestamp de início será ajustado
 * para a segunda-feira mais próxima (ou o próprio dia, se já for segunda).
 *
 * @param refEpochMillis O timestamp de referência em milissegundos (Epoch).
 * Normalmente, representa o instante atual no cliente (ex: `System.currentTimeMillis()`).
 *
 * @param timeInterval Enum que define o intervalo desejado: [TimeInterval.DAY],
 * [TimeInterval.WEEK], [TimeInterval.MONTH] ou [TimeInterval.YEAR].
 *
 * @return [TimeIntervalWrapper] contendo o timestamp de início ajustado (`startTs`)
 * e o tipo de intervalo (`timeInterval`). Esse wrapper é usado para construir as consultas
 * com agregações temporais à API do ThingsBoard.
 *
 * Exemplo de retorno para `refEpochMillis` em 13/07/2025 às 10h30:
 * - DAY  → startTs = 13/07/2025 00:00
 * - WEEK → startTs = 07/07/2025 00:00 (segunda-feira da semana)
 * - MONTH → startTs = 01/07/2025 00:00
 * - YEAR → startTs = 01/01/2025 00:00
 */
fun getTimeWrapper(
    refEpochMillis: Long,
    timeInterval: TimeInterval
): TimeIntervalWrapper {

    val rawStart = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(refEpochMillis),
        ZoneId.systemDefault()
    ).withNano(0)

    val startTs = when (timeInterval) {
        TimeInterval.DAY -> {
            val start = rawStart.truncatedTo(ChronoUnit.DAYS)
            start.toInstant().toEpochMilli()

        }

        TimeInterval.WEEK -> {
            val start =
                rawStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .truncatedTo(ChronoUnit.DAYS)
            start.toInstant().toEpochMilli()
        }

        TimeInterval.MONTH -> {
            val start = rawStart.with(TemporalAdjusters.firstDayOfMonth())
                .truncatedTo(ChronoUnit.DAYS)
            start.toInstant().toEpochMilli()
        }

        TimeInterval.YEAR -> {
            val start = rawStart.with(TemporalAdjusters.firstDayOfYear())
                .truncatedTo(ChronoUnit.DAYS)
            start.toInstant().toEpochMilli()
        }
    }

    return TimeIntervalWrapper(
        startTs,
        timeInterval
    )
}