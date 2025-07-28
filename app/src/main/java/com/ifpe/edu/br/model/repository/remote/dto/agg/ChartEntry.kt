package com.ifpe.edu.br.model.repository.remote.dto.agg

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class ChartEntry(
    val label: String,
    val value: Long
)
{
    override fun toString(): String {
        return "ChatEntry(label='$label', value=$value)"
    }
}