package com.ifpe.edu.br.model.repository.remote.dto


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class TelemetryAggregationResponse(
    val results: List<DeviceAggregatedTelemetry>
) {
    override fun toString(): String {
        return "TelemetryAggregationResponse(results=$results)"
    }
}