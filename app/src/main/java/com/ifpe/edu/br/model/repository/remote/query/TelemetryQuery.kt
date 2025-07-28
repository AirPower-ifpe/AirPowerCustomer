package com.ifpe.edu.br.model.repository.remote.query

import com.ifpe.edu.br.model.repository.remote.dto.EntityType


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


class TelemetryQuery(
    var entityType: EntityType,
    var deviceId: String,
    var keys: String,
    var startTs: String,
    var endTs: String,
    var intervalType: String,
    var interval: String,
    val timeZone: String,
    var aggregationStrategy: String,
    var orderStrategy: String
) {
    override fun toString(): String {
        return "TelemetryQuery(entityType=$entityType," +
                "deviceId='$deviceId'," +
                "keys='$keys'," +
                "startTs='$startTs'," +
                "endTs='$endTs'," +
                "intervalType='$intervalType'," +
                "interval='$interval'," +
                "aggregationStrategy='$aggregationStrategy'," +
                "timeZone='$timeZone'," +
                "orderStrategy='$orderStrategy')"
    }
}