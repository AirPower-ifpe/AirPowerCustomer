package com.ifpe.edu.br.view.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.view.ui.theme.tb_primary_light

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

@Composable
fun EmptyStateCard() {
    Box(
        modifier = Modifier
            .background(Color.Transparent)
    ) {
        CustomCard(
            layouts = listOf {
                Spacer(modifier = Modifier.padding(vertical = 65.dp))
                CustomText(
                    color = tb_primary_light,
                    text = "Não há dados a exibir",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.padding(vertical = 65.dp))
            }
        )
    }
}