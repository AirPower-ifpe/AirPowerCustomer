package com.ifpe.edu.br.view.screens

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.RoundedImageIcon
import com.ifpe.edu.br.common.components.TextTitle
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

@Composable
fun DeviceScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val scrollState = rememberScrollState()
    CustomColumn(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize(),
        alignmentStrategy = CommonConstants.ALIGNMENT_CENTER,
        layouts = listOf {
            Spacer(modifier = Modifier.padding(vertical = 100.dp))
            TextTitle(textAlign = TextAlign.Center, message = "DeviceScreen")
            RoundedImageIcon(
                description = "",
                iconResId = R.drawable.airpower_icon,
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 100.dp))
        }
    )
}