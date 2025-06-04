package com.ifpe.edu.br.model.repository.remote.query


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class AggregatedTelemetryQuery(
    var deviceIds: List<String>,
    var telemetryKeys: List<String>,
    var aggregationFunction: String,
    var timeWindowHours: Int
) {
    override fun toString(): String {
        return "AggregatedTelemetryQuery(" +
                "devicesID=$deviceIds, " +
                "telemetryKeys=$telemetryKeys, " +
                "aggregationFunction='$aggregationFunction', " +
                "timeWindowHours=$timeWindowHours)"
    }
}