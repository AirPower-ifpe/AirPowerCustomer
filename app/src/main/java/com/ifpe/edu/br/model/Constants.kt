package com.ifpe.edu.br.model

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
class Constants {

    object ResponseErrorId {
        // --- Autenticação AirPower (1xxx) ---
        const val AP_JWT_EXPIRED = 10
        const val AP_REFRESH_TOKEN_EXPIRED = 11
        const val AP_GENERIC_ERROR = 12

        // --- Erros Mapeados do ThingsBoard (3xxx) ---
        const val TB_INVALID_CREDENTIALS = 30
        const val TB_REFRESH_TOKEN_EXPIRED = 31
        const val TB_GENERIC_ERROR = 32

        // --- Erros Genéricos (9xxx) ---
        const val UNKNOWN_INTERNAL_ERROR = 90
    }

    object UIState {
        const val STATE_SUCCESS = -1
        const val STATE_TB_INVALID_CREDENTIALS = 1
        const val STATE_TB_REFRESH_TOKEN_EXPIRED = 2
        const val STATE_AP_GENERIC_ERROR = 3
        const val STATE_AP_REFRESH_TOKEN_EXPIRED = 4
        const val STATE_AP_JWT_EXPIRED = 5
        const val STATE_UNKNOWN_INTERNAL_ERROR = 6
        const val STATE_NETWORK_ISSUE = 7
        const val STATE_ERROR = "STATE_ERROR"
        const val AUTH_STATE = "AUTH_STATE"
    }

    object UIStateId{
        const val SESSION = "SESSION"
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