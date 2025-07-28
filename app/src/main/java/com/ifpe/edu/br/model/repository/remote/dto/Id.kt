package com.ifpe.edu.br.model.repository.remote.dto

import java.util.UUID

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class Id(
    val id: UUID,
    val entityType: String? = null
) {
    override fun toString(): String {
        return "ID(id=$id, entityType=$entityType)"
    }
}