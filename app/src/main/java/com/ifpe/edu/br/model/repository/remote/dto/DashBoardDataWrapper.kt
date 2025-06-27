package com.ifpe.edu.br.model.repository.remote.dto

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class DashBoardDataWrapper(
    val label: String,
    val alarmInfo: List<AlarmInfo>,
    val allMetricsWrapper: AllMetricsWrapper
) {
    override fun toString(): String {
        return "DashBoardDataWrapper(label='$label', alarmInfo=$alarmInfo, allMetricsWrapper=$allMetricsWrapper)"
    }
}