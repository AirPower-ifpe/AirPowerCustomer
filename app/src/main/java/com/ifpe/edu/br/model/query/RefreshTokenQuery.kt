package com.ifpe.edu.br.model.query

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

class RefreshTokenQuery(
    private var refreshToken: String
) {
    override fun toString(): String {
        return "RefreshTokenQuery(refreshToken='$refreshToken')"
    }
}