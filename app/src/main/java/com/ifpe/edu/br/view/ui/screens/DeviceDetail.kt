package com.ifpe.edu.br.view.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.view.ui.components.getStatusColor
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
            DeviceInfoCard(device)
            DeviceConsumptionCard()
            AlarmsCard()
        }
    )
}

@Composable
private fun AlarmsCard() {
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

@Composable
private fun DeviceConsumptionCard() {
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