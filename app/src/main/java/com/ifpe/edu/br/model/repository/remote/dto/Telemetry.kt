package com.ifpe.edu.br.model.repository.remote.dto


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class Telemetry(
    val key: String,
    val value: Double?,
    val dataPointsConsidered: Int
) {
    override fun toString(): String {
        return "Telemetry(key='$key', value=$value, dataPointsConsidered=$dataPointsConsidered)"
    }
}