package com.ifpe.edu.br.model.dto

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
class Device(
    var name: String,
    var label: String,
    var deviceProfileId: Id?
) {
    var id: Id? = null
    var createdTime: Long? = null
    var readOnly: Boolean = true
    var tenantId: Id? = null
    var customerId: Id? = null
    var type: String? = null
    var deviceData: DeviceData? = null

    constructor(
        name: String,
        label: String,
        deviceProfileId: Id,
        createdTime: Long,
        readOnly: Boolean,
        tenantId: Id,
        customerId: Id,
        type: String,
        deviceData: DeviceData
    ) : this(name, label, deviceProfileId) {
        this.createdTime = createdTime
        this.readOnly = readOnly
        this.tenantId = tenantId
        this.customerId = customerId
        this.type = type
        this.deviceData = deviceData
    }

    override fun toString(): String {
        return "Device(" +
                "name='$name'," +
                "label='$label'," +
                "deviceProfileId=$deviceProfileId," +
                "id=$id, createdTime=$createdTime," +
                "readOnly=$readOnly," +
                "tenantId=$tenantId," +
                "customerId=$customerId," +
                "type=$type," +
                "deviceData=$deviceData)"
    }
}