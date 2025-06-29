package com.ifpe.edu.br.view.ui.screens


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


sealed class Screen(val route: String) {

    object DeviceDetail : Screen("device_detail_screen/{deviceId}") {
        fun createRoute(deviceId: String) = "device_detail_screen/$deviceId"
    }

    object NotificationCenter : Screen("notification_center") {

    }

    object Home : Screen("home") {
    }

    object Devices : Screen("device") {
    }

    object Profile : Screen("profile") {
    }
}