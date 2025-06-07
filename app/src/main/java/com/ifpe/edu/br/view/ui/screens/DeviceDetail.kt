package com.ifpe.edu.br.view.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.view.ui.components.DeviceCard
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import java.util.UUID


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


@Composable
fun DeviceDetailScreen(
    deviceId: String,
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val device = mainViewModel.getDeviceById(deviceId)
    CustomColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
        layouts = listOf {
            CustomCard(
                paddingStart = 15.dp,
                paddingEnd = 15.dp,
                paddingTop = 10.dp,
                paddingBottom = 5.dp,
                layouts = listOf {
                    CustomText(
                        color = tb_primary_light,
                        text = "Informaçõs do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = device.label
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = device.type
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = device.isActive.toString()
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = device.name
                    )
                }
            )

            CustomCard(
                paddingStart = 15.dp,
                paddingEnd = 15.dp,
                paddingTop = 5.dp,
                paddingBottom = 5.dp,
                layouts = listOf {
                    CustomText(
                        color = tb_primary_light,
                        text = "Consumo do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Consumo do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Consumo do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Consumo do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Consumo do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Consumo do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Consumo do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Consumo do dispositivo"
                    )
                }
            )

            CustomCard(
                paddingStart = 15.dp,
                paddingEnd = 15.dp,
                paddingTop = 5.dp,
                paddingBottom = 10.dp,
                layouts = listOf {
                    CustomText(
                        color = tb_primary_light,
                        text = "Alarmes do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Alarmes do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Alarmes do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Alarmes do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Alarmes do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Alarmes do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Alarmes do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Alarmes do dispositivo"
                    )
                    CustomText(
                        color = tb_primary_light,
                        text = "Alarmes do dispositivo"
                    )
                }
            )
        }
    )
}

@Composable
private fun DeviceInfo(
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
        items(deviceCards, key = { it }) { deviceItem ->
            DeviceCard(device = deviceItem, onClick = onClick)
        }
    }
}