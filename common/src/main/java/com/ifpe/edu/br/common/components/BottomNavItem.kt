package com.ifpe.edu.br.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/*
* Dispositivos Móveis - IFPE 2023 
* Author: Willian Santos
* Project: AirPower Costumer
*/

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
) {
    data object Home :
        BottomNavItem(
            "home",
            "Home",
            { Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.Black)})

    data object Devices :
        BottomNavItem(
            "device",
            "Devices",
            { Icon(Icons.Filled.Devices, contentDescription = "Device", tint = Color.Black) })

    data object DashBoards :
        BottomNavItem(
            "dashboards",
            "DashBoards",
            { Icon(Icons.Filled.Assessment, contentDescription = "DashBoards", tint = Color.Black) })
}