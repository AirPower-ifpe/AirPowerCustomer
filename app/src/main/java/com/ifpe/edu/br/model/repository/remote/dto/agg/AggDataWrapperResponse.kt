package com.ifpe.edu.br.model.repository.remote.dto.agg


/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class AggDataWrapperResponse(
    val label: String,
    val chartDataWrapper: ChartDataWrapper,
    val aggregation: Agg,
    val size: Int
)
{
    override fun toString(): String {
        return "AggDataWrapper(" +
                "label='$label', " +
                "chartDataWrapper=$chartDataWrapper, " +
                "aggregation=$aggregation, " +
                "size=$size" +
                ")"
    }
}