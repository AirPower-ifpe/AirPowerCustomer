package com.ifpe.edu.br.model.repository.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    @ColumnInfo(name = "USER_FIRST_NAME")
    val firstName: String? = "",
    @ColumnInfo(name = "USER_LAST_NAME")
    val lastName: String? = "",
    @ColumnInfo(name = "USER_NAME")
    val name: String? = "",
    @ColumnInfo(name = "USER_PHONE")
    val phone: String? = "",
    @ColumnInfo(name = "USER_EMAIL")
    val email: String = ""
) {
    override fun toString(): String {
        return "User(" +
                "id='$id', " +
                "authority=$authority, " +
                "customerId=$customerId, " +
                "firstName=$firstName, " +
                "lastName=$lastName, " +
                "name=$name, " +
                "phone=$phone, " +
                "email=$email)"
    }
}