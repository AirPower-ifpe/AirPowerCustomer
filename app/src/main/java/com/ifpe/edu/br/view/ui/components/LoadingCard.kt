package com.ifpe.edu.br.view.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ifpe.edu.br.common.components.CustomProgressDialog
import com.ifpe.edu.br.view.ui.theme.DefaultTransparentGradient
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.view.ui.theme.tb_secondary_light

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
@Composable
fun LoadingCard() {
    Box(
        modifier = Modifier
            .background(Color.Transparent)
    ) {
        Spacer(modifier = Modifier.padding(vertical = 150.dp))
        CustomProgressDialog(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            indicatorColor = tb_secondary_light,
            textColor = tb_primary_light
        ) { modifier ->
            DefaultTransparentGradient(modifier)
        }
    }
}