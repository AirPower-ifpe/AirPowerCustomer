package com.ifpe.edu.br.model.repository.remote.dto

import androidx.compose.ui.platform.InspectorValueInfo

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class DashboardInfo(
    val id: Id,
    val name: String,
    val title: String,
    val devicesIds: List<String>
) {

    override fun toString(): String {
        return "DashboardInfo(id=$id, name='$name', title='$title', devicesId=$devicesIds)"
    }
}
