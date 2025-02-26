package com.ifpe.edu.br.model.repository.remote.dto

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

data class ThingsBoardUser(
    val id: Id,
    val createdTime: Long,
    val tenantId: Id,
    val customerId: Id,
    val email: String,
    val name: String,
    val authority: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val additionalInfo: Map<String, Any>
) {
    override fun toString(): String {
        return "ThingsBoardUser(id=$id, " +
                "createdTime=$createdTime, " +
                "tenantId=$tenantId, " +
                "customerId=$customerId, " +
                "email='$email', " +
                "name='$name', " +
                "authority='$authority', " +
                "firstName='$firstName', " +
                "lastName='$lastName', " +
                "phone='$phone', " +
                "additionalInfo=$additionalInfo)"
    }
}