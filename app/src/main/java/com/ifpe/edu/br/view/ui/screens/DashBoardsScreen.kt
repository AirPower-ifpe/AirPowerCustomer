package com.ifpe.edu.br.view.ui.screens

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
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
import com.ifpe.edu.br.common.components.RectButton
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius
import com.ifpe.edu.br.model.repository.model.TelemetryDataWrapper
import com.ifpe.edu.br.model.repository.remote.dto.AlarmInfo
import com.ifpe.edu.br.model.repository.remote.dto.AllMetricsWrapper
import com.ifpe.edu.br.model.util.AirPowerUtil
import com.ifpe.edu.br.view.AuthActivity
import com.ifpe.edu.br.view.ui.theme.app_default_solid_background_light
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.view.ui.theme.tb_secondary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

@Composable
fun DashBoardsScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val dashBoardsDataWrapper = mainViewModel.getUserDashBoardsDataWrapper().collectAsState()
    CustomColumn(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize(),
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
        layouts = listOf {
            dashBoardsDataWrapper.value.forEach { dashBoardsDataWrapper ->
                DashboardsCardBoard(
                    label = dashBoardsDataWrapper.label,
                    allMetricsWrapper = dashBoardsDataWrapper.allMetricsWrapper,
                    alarmInfo = dashBoardsDataWrapper.alarmInfo
                )
            }

            RectButton(
                text = "Logout",
                onClick = {
                    mainViewModel.logout()
                    AirPowerUtil.launchActivity(
                        navController.context,
                        AuthActivity::class.java,
                    )
                    navController.popBackStack()
                    (context as? ComponentActivity)?.finish()
                },
                fontSize = 20.sp
            )
        }
    )
}

@Composable
private fun DashboardsCardBoard(
    label: String,
    allMetricsWrapper: AllMetricsWrapper,
    alarmInfo: List<AlarmInfo>
) {

    val context = LocalContext.current
    var totalAlarmCount = 0
    alarmInfo.forEach { info ->
        totalAlarmCount += info.occurrence
    }

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
                            text = label,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.padding(vertical = 6.dp))

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
                                    "Consumo Anual",
                                    allMetricsWrapper.totalConsumption,
                                    onClick = {})
                                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                                SummaryCard(
                                    "Dispositivos",
                                    allMetricsWrapper.devicesCount.toString(),
                                    onClick = {})
                            })

                        Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                        CustomColumn(
                            modifier = Modifier.fillMaxSize(),
                            layouts = listOf {
                                Spacer(modifier = Modifier.padding(vertical = 12.dp))
                                CustomBarChart(
                                    height = 300.dp,
                                    dataWrapper = TelemetryDataWrapper(
                                        allMetricsWrapper.label,
                                        allMetricsWrapper.deviceConsumptionSet
                                    )
                                )
                                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                            })
                    }
                }
            )

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