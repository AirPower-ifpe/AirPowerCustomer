package com.ifpe.edu.br.model.repository.remote.dto.agg

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class AggregationRequest(
    val devicesIds: List<String>,
    val aggKey: TelemetryKey,
    val aggStrategy: AggStrategy,
    val timeIntervalWrapper: TimeIntervalWrapper
) {
    override fun toString(): String {
        return "AggregationRequest(" +
                "devices=$devicesIds, " +
                "aggKey='$aggKey', " +
                "aggStrategy='$aggStrategy', " +
                "timeInterval='$timeIntervalWrapper')"
    }
}