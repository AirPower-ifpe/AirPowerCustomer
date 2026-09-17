package com.ifpe.edu.br.view.ui.screens

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.model.repository.remote.dto.AlarmInfo
import com.ifpe.edu.br.model.util.AirPowerUtil
import com.ifpe.edu.br.view.AuthActivity
import com.ifpe.edu.br.view.ui.components.DashboardCard
import com.ifpe.edu.br.view.ui.components.EmptyStateCard
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardsScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val context = LocalContext.current
    val userDashboards by mainViewModel.getDashboardsForCurrentUser().collectAsState(initial = emptyList())
    val allAlarms by mainViewModel.getAlarmInfoSet().collectAsState()
    val isRefreshing by mainViewModel.isRefreshing.collectAsState()

    val totalMonitoredDevices = remember(userDashboards) {
        userDashboards.flatMap { it.devicesIds }.distinct().size
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { mainViewModel.forceRefresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        if (userDashboards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Cabeçalho com visão consolidada
                item {
                    DashboardsHeader(
                        totalDashboards = userDashboards.size,
                        totalDevices = totalMonitoredDevices
                    )
                }

                // Lista de Dashboards
                items(userDashboards, key = { it.id.id }) { dashboard ->
                    DashboardCard(
                        dashboard = dashboard,
                        mainViewModel = mainViewModel,
                        allAlarms = allAlarms
                    )
                }

                // Botão de Logout padronizado
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = {
                            mainViewModel.logout()
                            AirPowerUtil.launchActivity(
                                navController.context,
                                AuthActivity::class.java,
                            )
                            navController.popBackStack()
                            (context as? ComponentActivity)?.finish()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                            )
                        )
                    ) {
                        Text(
                            text = "Encerrar Sessão",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DashboardsHeader(totalDashboards: Int, totalDevices: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Painéis de Monitoramento",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "$totalDashboards visões • $totalDevices medidores",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

fun filterAlarmsByDeviceIds(alarms: List<AlarmInfo>, deviceIds: List<String>): List<AlarmInfo> {
    val deviceIdsSet = deviceIds.toSet()
    return alarms.filter { alarm ->
        alarm.originator.id?.toString()?.let { deviceId ->
            deviceIdsSet.contains(deviceId)
        } ?: false
    }
}