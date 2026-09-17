// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.
package com.ifpe.edu.br.model.repository.model

import com.ifpe.edu.br.model.repository.remote.dto.agg.TelemetryKey
import com.ifpe.edu.br.model.repository.remote.dto.agg.TimeInterval

data class DashboardFilters(
    val interval: TimeInterval = TimeInterval.WEEK,
    val telemetryKey: TelemetryKey = TelemetryKey.POWER,
    val chartType: ChartType = ChartType.BAR
)
