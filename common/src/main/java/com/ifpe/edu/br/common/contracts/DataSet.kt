package com.ifpe.edu.br.common.contracts

import androidx.compose.ui.graphics.Brush
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientDark
import ir.ehsannarmani.compose_charts.models.Bars


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class DataSet(
    val label: String,
    val data: List<DataEntry>,
    val color: Brush = Brush.linearGradient(defaultBackgroundGradientDark)
) {
    fun toBar(): List<Bars> {
        return data.map { entry ->
            Bars(
                label = entry.label,
                values = listOf(
                    Bars.Data(
                        label = label,
                        value = entry.verticalValue,
                        color = color
                    )
                )
            )
        }
    }
}