package com.ifpe.edu.br.model.repository.remote.dto

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class DeviceData(
    var configuration: Configuration,
    var transportConfiguration: Configuration
) {
    override fun toString(): String {
        return "DeviceData(" +
                "configuration=$configuration," +
                "transportConfiguration=$transportConfiguration)"
    }
}