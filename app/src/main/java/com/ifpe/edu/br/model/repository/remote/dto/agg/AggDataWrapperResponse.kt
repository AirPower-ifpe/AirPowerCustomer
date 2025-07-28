package com.ifpe.edu.br.model.repository.remote.dto.agg

import com.ifpe.edu.br.model.repository.remote.dto.DevicesStatusSummary


/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class AggDataWrapperResponse(
    val label: String,
    val chartDataWrapper: ChartDataWrapper,
    val statusSummaries: List<DevicesStatusSummary>,
    val aggregation: Agg,
    val size: Int
)
{
    override fun toString(): String {
        return "AggDataWrapperResponse(label='$label'," +
                "chartDataWrapper=$chartDataWrapper," +
                "statusSummaries=$statusSummaries," +
                "aggregation=$aggregation," +
                "size=$size)"
    }
}