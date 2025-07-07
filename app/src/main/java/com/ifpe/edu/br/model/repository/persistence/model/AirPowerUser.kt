package com.ifpe.edu.br.model.repository.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ifpe.edu.br.model.repository.remote.dto.Id
import com.ifpe.edu.br.model.repository.remote.dto.user.ThingsBoardUser
import java.util.UUID

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
@Entity(tableName = "AIR_POWER_USER")
data class AirPowerUser(
    @PrimaryKey
    @ColumnInfo(name = "USER_ID")
    val id: String,
    @ColumnInfo(name = "USER_AUTHORITY")
    val authority: String = "",
    @ColumnInfo(name = "USER_COSTUMER_ID")
    val customerId: String = "",
    @ColumnInfo(name = "TENANT_ID")
    val tenantId: String = "",
    @ColumnInfo(name = "USER_FIRST_NAME")
    val firstName: String? = "",
    @ColumnInfo(name = "USER_LAST_NAME")
    val lastName: String? = "",
    @ColumnInfo(name = "USER_NAME")
    var name: String? = "",
    @ColumnInfo(name = "USER_PHONE")
    val phone: String? = "",
    @ColumnInfo(name = "USER_EMAIL")
    val email: String = ""
) {
    override fun toString(): String {
        return "AirPowerUser(id='$id', authority='$authority', customerId='$customerId', tenantId='$tenantId', firstName=$firstName, lastName=$lastName, name=$name, phone=$phone, email='$email')"
    }
}

fun AirPowerUser.toThingsBoardUser(): ThingsBoardUser {
    return ThingsBoardUser(
        id = Id(UUID.fromString(id), "id"),
        createdTime = System.currentTimeMillis(),
        tenantId = Id(UUID.fromString(tenantId), "tenantId"),
        customerId = Id(UUID.fromString(customerId), "customerId"),
        email = email,
        name = name ?: "",
        authority = authority,
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        phone = phone ?: "",
        additionalInfo = mapOf()
    )
}