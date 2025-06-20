package com.ifpe.edu.br.model

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
class Constants {

    object ResponseErrorCodes {
        // --- Autenticação AirPower (1xxx) ---
        const val INVALID_AIRPOWER_TOKEN = 1

        const val INVALID_JWT_TOKEN = 11
        const val INVALID_REFRESH_TOKEN = 12

        const val ACCESS_DENIED = 13

        // --- Erros de Requisição (2xxx) ---
        const val INVALID_ARGUMENTS = 2
        const val INVALID_RESPONSE = 21

        // --- Erros Mapeados do ThingsBoard (3xxx) ---
        const val TB_AUTHENTICATION_FAILED = 31
        const val TB_JWT_EXPIRED = 32
        const val TB_PERMISSION_DENIED = 33
        const val TB_ITEM_NOT_FOUND = 34
        const val TB_TOO_MANY_REQUESTS = 35
        const val TB_GENERIC_ERROR = 36

        // --- Erros Genéricos (9xxx) ---
        const val UNKNOWN_INTERNAL_ERROR = 9
    }

    object UIState {
        const val STATE_ERROR = "STATE_ERROR"
        const val AUTH_STATE = "AUTH_STATE"
        const val STATE_AUTH_REQUIRED = "STATE_AUTH_REQUIRED"
        const val STATE_CONNECTION_FAILURE = "STATE_CONNECTION_FAILURE"
        const val STATE_AUTH_FAILURE = "STATE_AUTH_FAILURE"
        const val STATE_AUTH_LOADING = "STATE_AUTH_LOADING"
    }

    @Deprecated("Replace with navigation approach")
    object Navigation{
        const val NAVIGATION_INITIAL = "START"
        const val NAVIGATION_MAIN = "MAIN"
        const val NAVIGATION_AUTH = "AUTH"
    }

    object ServerConnectionIds {
        const val CONNECTION_ID_THINGSBOARD = 1
        const val CONNECTION_ID_AIR_POWER_SERVER = 2
    }

    object ResKeys{
        const val KEY_COD_DRAWABLE: String = "drawable"
    }

    @Deprecated("Replace with new approach")
    object DeprecatedValues{
        const val THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED = 11
        const val THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED = 10
    }
}