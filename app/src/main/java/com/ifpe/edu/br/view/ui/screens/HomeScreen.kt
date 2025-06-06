package com.ifpe.edu.br.view.ui.screens

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomBarChart
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.RoundedButton
import com.ifpe.edu.br.common.components.TextTitle
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val scrollState = rememberScrollState()
    CustomColumn(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize(),
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
        layouts = listOf {
            Spacer(modifier = Modifier.padding(vertical = 100.dp))

            TextTitle(textAlign = TextAlign.Center, message = "HomeScreen")

            Spacer(modifier = Modifier.padding(vertical = 100.dp))
        }
    )
}