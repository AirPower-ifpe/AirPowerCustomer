package com.ifpe.edu.br.model.repository.model

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class HomeScreenAlarmSummaryCard(
    val severity: String,
    val occurrence: Int
)
{
    override fun toString(): String {
        return "HomeScreenAlarmSummaryCard(severity='$severity', occurrence=$occurrence)"
    }
}