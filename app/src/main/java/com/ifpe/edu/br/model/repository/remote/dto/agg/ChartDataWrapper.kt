package com.ifpe.edu.br.model.repository.remote.dto.agg

import com.ifpe.edu.br.common.contracts.ChartDataWrapper
import com.ifpe.edu.br.common.contracts.DataEntry
import com.ifpe.edu.br.common.contracts.DataSet

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class ChartDataWrapper(
    val label: String,
    val entries: List<ChartEntry>
): ChartDataWrapper {

    override fun getName(): String {
        return label
    }

    override fun getDataSet(): DataSet {
        val dataEntries = entries.map { item ->
            DataEntry(
                label = item.label,
                verticalValue = item.value.toDouble()
            )
        }
        return DataSet(label, dataEntries)
    }


    override fun toString(): String {
        return "ChartDataWrapper(label='$label', entries=$entries)"
    }
}