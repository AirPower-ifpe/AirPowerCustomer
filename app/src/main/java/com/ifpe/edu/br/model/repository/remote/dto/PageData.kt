package com.ifpe.edu.br.model.repository.remote.dto

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class PageData<T>(
    val data: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val hasNext: Boolean
) {
    override fun toString(): String {
        return "PageData(data=$data, " +
                "totalPages=$totalPages, " +
                "totalElements=$totalElements, " +
                "hasNext=$hasNext)"
    }
}