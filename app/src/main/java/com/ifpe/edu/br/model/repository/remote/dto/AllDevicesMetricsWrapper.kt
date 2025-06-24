package com.ifpe.edu.br.model.repository.remote.dto

import com.ifpe.edu.br.model.repository.model.TelemetryDataWrapper


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class AllDevicesMetricsWrapper(
    val deviceConsumptionSet: List<DeviceConsumption>,
    val statusSummaries: List<DevicesStatusSummary>,
    val totalConsumption: String,
    val devicesCount: Int,
    val label: String
) {

    override fun toString(): String {
        return "AllDevicesMetricsWrapper(deviceConsumptionSet=$deviceConsumptionSet, statusSummaries=$statusSummaries, totalConsumption='$totalConsumption', devicesCount=$devicesCount, label='$label')"
    }
}