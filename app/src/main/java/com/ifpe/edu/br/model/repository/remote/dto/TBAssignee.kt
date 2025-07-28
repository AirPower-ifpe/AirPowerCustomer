package com.ifpe.edu.br.model.repository.remote.dto

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
data class TBAssignee(
    val id: Id,
    val firstName: String?,
    val lastName: String?,
    val email: String?
)
{
    override fun toString(): String {
        return "Assignee(id=$id, firstName=$firstName, lastName=$lastName, email=$email)"
    }
}