package com.ifpe.edu.br.model.repository.remote.dto.agg

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class Agg(
    val label: String,
    val value: String
)
{
    override fun toString(): String {
        return "Agg(label='$label', values='$value')"
    }
}