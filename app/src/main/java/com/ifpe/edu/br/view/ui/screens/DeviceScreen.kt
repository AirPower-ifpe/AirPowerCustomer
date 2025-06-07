package com.ifpe.edu.br.view.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.TextTitle
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.view.ui.components.DeviceCard
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import java.util.UUID

@Composable
fun DeviceScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val devicesSummary by mainViewModel.getDevicesSummary().observeAsState(initial = emptyList())

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        //mainViewModel.startDataFetchers()
    }

    when {
        devicesSummary.isEmpty() -> {
            CustomColumn(
                modifier = Modifier.fillMaxSize(),
                alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
                layouts = listOf {
                    TextTitle(
                        textColor = tb_primary_light,
                        textAlign = TextAlign.Center,
                        message = "Nenhum dispositivo encontrado.\nVerifique sua conexão ou adicione novos dispositivos."
                    )
                }
            )
        }

        else -> {
            DeviceGrid(deviceCards = devicesSummary) { deviceId ->
                navController.navigate(Screen.DeviceDetail.createRoute(deviceId.toString()))
            }
        }
    }
}

@Composable
private fun DeviceGrid(
    deviceCards: List<DeviceSummary>,
    onClick: (UUID) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(deviceCards, key = { it.id }) { deviceItem ->
            DeviceCard(device = deviceItem, onClick = onClick)
        }
    }
}