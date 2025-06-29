package com.ifpe.edu.br.model.repository.remote.dto

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class NotificationItem(
    val message: String,
    val timestamp: Long,
    var wasOped: Boolean = false
)
