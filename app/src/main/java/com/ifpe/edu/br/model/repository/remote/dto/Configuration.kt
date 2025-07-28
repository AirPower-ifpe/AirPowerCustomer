package com.ifpe.edu.br.model.repository.remote.dto

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class Configuration(
    private var type: String
) {
    override fun toString(): String {
        return "Configuration(type='$type')"
    }
}