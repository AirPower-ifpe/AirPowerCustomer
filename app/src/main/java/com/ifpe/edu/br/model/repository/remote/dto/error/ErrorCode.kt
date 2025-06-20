package com.ifpe.edu.br.model.repository.remote.dto.error

import com.ifpe.edu.br.model.Constants

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
enum class ErrorCode(
    val httpStatus: Int,
    val errorCode: Int,
    val defaultMessage: String
) {
    AP_JWT_EXPIRED(401, Constants.ResponseErrorId.AP_JWT_EXPIRED, "O token de acesso fornecido é inválido ou expirou."),
    AP_REFRESH_TOKEN_EXPIRED(401, Constants.ResponseErrorId.AP_REFRESH_TOKEN_EXPIRED, "O token de atualização é inválido ou já foi utilizado."),
    AP_GENERIC_ERROR(401, Constants.ResponseErrorId.AP_GENERIC_ERROR, "Ocorreu um erro inesperado no servidor AirPower"),

    TB_INVALID_CREDENTIALS(401, Constants.ResponseErrorId.TB_INVALID_CREDENTIALS, "Usuário ou senha incorretos."),
    TB_REFRESH_TOKEN_EXPIRED(401, Constants.ResponseErrorId.TB_REFRESH_TOKEN_EXPIRED, "O token de atualização é inválido ou já foi utilizado."),
    TB_GENERIC_ERROR(502, Constants.ResponseErrorId.TB_GENERIC_ERROR,"Ocorreu um erro inesperado no serviço externo."),

    UNKNOWN_INTERNAL_ERROR(500, Constants.ResponseErrorId.UNKNOWN_INTERNAL_ERROR,"Ocorreu um erro interno inesperado no servidor."),
}