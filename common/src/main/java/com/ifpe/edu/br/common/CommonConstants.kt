package com.ifpe.edu.br.common

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

class CommonConstants {

    object Ui {
        val ALIGNMENT_TOP = 1
        val ALIGNMENT_CENTER = 2
    }

    object State {
        const val STATE_DEFAULT_MESSAGE = "STATE_DEFAULT_MESSAGE"
        const val STATE_DEFAULT_CODE = -1

        const val STATE_LOADING = 5
        const val STATE_AUTH_LOADING = 1
        const val STATE_AUTH_FAILURE = 2
        const val STATE_TOKEN_EXPIRED = 3
        const val STATE_NETWORK_ISSUE = 4
    }
}