package com.ifpe.edu.br.common.components

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomColumn(
    layouts: List<@Composable () -> Unit>
) {
    Column {
        layouts.forEach { layout ->
            layout()
        }
    }
}

@Composable
fun Card(
    layouts: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier
) {
    Box(
    ) {
        Column {
            Surface {
                Column(
                    modifier = modifier.padding(8.dp)
                ) {
                    layouts.forEach { layout ->
                        layout()
                    }
                }
            }
        }
    }
}