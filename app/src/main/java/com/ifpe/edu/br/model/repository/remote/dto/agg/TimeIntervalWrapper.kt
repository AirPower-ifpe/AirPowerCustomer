package com.ifpe.edu.br.model.repository.remote.dto.agg

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class TimeIntervalWrapper(
    val periodStartTs: Long,
    val timeInterval: TimeInterval,
)
{
    override fun toString(): String {
        return "TimeIntervalWrapper(periodStartTs=$periodStartTs, timeInterval=$timeInterval)"
    }
}