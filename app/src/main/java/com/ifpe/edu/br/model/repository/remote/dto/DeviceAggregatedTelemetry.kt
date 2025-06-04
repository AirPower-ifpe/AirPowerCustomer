package com.ifpe.edu.br.model.repository.remote.dto

import java.util.UUID


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class DeviceAggregatedTelemetry(
    val deviceId: String? = null,
    val deviceLabel: String? = null,
    val telemetryKeys: List<String>? = null,
    val aggregationFunction: String? = null,
    val timeWindowHours: Int? = null,
    val aggregatedValues: List<Telemetry>? = null,
) {
    override fun toString(): String {
        return "DeviceAggregatedTelemetry(deviceId=$deviceId," +
                "deviceLabel=$deviceLabel, " +
                "telemetryKeys=$telemetryKeys, " +
                "aggregationFunction=$aggregationFunction, " +
                "timeWindowHours=$timeWindowHours, " +
                "aggregatedValues=$aggregatedValues)"
    }
}
