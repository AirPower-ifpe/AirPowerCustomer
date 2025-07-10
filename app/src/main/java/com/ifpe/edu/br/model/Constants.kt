package com.ifpe.edu.br.model

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
class Constants {

    object ResponseErrorCode {
        // --- Erros Mapeados do ThingsBoard (2xxx) ---
        const val TB_INVALID_CREDENTIALS = 20
        const val TB_REFRESH_TOKEN_EXPIRED = 21
        const val TB_GENERIC_ERROR = 22

        // --- Autenticação AirPower (3xxx) ---
        const val AP_JWT_EXPIRED = 30
        const val AP_REFRESH_TOKEN_EXPIRED = 31
        const val AP_GENERIC_ERROR = 32

        // --- Erros Genéricos (9xxx) ---
        const val UNKNOWN_INTERNAL_ERROR = 90

        const val SUCCESS = 0
        const val EMPTY_STATE_MESSAGE = ""
        const val EMPTY_STATE_CODE = -1
    }

    object UIState {
        const val EMPTY_STATE = "EMPTY_STATE"
        const val STATE_UPDATE_SESSION = "STATE_UPDATE_SESSION"
        const val STATE_REFRESH_TOKEN = "STATE_REFRESH_TOKEN"
        const val STATE_LOADING = "LOADING"
        const val STATE_REQUEST_LOGIN = "STATE_REQUEST_LOGIN"
        const val STATE_NETWORK_ISSUE = "NETWORK_ISSUE"
        const val STATE_SUCCESS = "SUCCESS"
        const val GENERIC_ERROR = "GENERIC_ERROR"
        const val STATE_AUTHENTICATION_FAILURE = "AUTHENTICATION_FAILURE"
    }

    object MetricsGroup {
        const val ALL = "ALL"
        const val DASHBOARDS = "DASHBOARDS"
    }

    object UIStateKey {
        const val STATE_ERROR = "STATE_ERROR"
        const val SESSION = "SESSION"
        const val AUTH_KEY = "AUTH_STATE"
        const val LOGIN_KEY = "STATE_LOGIN"
        const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"
        const val AGG_TELEMETRY_STATE = "AGG_TELEMETRY_STATE"
        const val DEVICE_SUMMARY_KEY = "DEVICE_SUMMARY_KEY"
        const val ALARMS_KEY = "ALARMS_KEY"
        const val METRICS_KEY = "METRICS_KEY"
        const val DEVICE_METRICS_KEY = "DEVICE_METRICS_KEY"
    }

    @Deprecated("Replace with navigation approach")
    object Navigation {
        const val NAVIGATION_INITIAL = "START"
        const val NAVIGATION_MAIN = "MAIN"
        const val NAVIGATION_AUTH = "AUTH"
    }

    object ServerConnectionIds {
        const val CONNECTION_ID_THINGSBOARD = 1
        const val CONNECTION_ID_AIR_POWER_SERVER = 2
    }

    object ResKeys {
        const val KEY_COD_DRAWABLE: String = "drawable"
    }

    @Deprecated("Replace with new approach")
    object DeprecatedValues {
        const val THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED = 11
        const val THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED = 10
    }
}