package com.ifpe.edu.br.model.repository.model

import com.ifpe.edu.br.common.contracts.ChartDataWrapper
import com.ifpe.edu.br.common.contracts.DataEntry
import com.ifpe.edu.br.common.contracts.DataSet
import com.ifpe.edu.br.model.repository.remote.dto.DeviceConsumption


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


class TelemetryDataWrapper(
    private val label: String,
    private val consumption: List<DeviceConsumption>
) : ChartDataWrapper {

    override fun getName(): String {
        return label
    }

    override fun getDataSet(): DataSet {
        val dataEntries = consumption.map { item ->
            DataEntry(
                label = item.label,
                verticalValue = item.value
            )
        }
        return DataSet(label, dataEntries)
    }
}