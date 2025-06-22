package com.ifpe.edu.br.model.repository.persistence.manager

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
import com.ifpe.edu.br.model.repository.Repository
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerToken
import com.ifpe.edu.br.model.repository.remote.dto.auth.Token
import com.ifpe.edu.br.model.util.AirPowerLog
import org.json.JSONObject
import java.util.Base64

object JWTManager {
    private val TAG = JWTManager::class.java.simpleName
    private val repository: Repository = Repository.getInstance()

    suspend fun handleAuthentication(
        connectionId: Int,
        token: Token,
        authCallback: suspend (AirPowerToken) -> Unit
    ) {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "handleAuthentication()")
        require(isTokenValid(token)) { "Token is not valid" }

        val persistToken = AirPowerToken(
            connectionId,
            token.token,
            token.refreshToken,
            token.scope ?: ""
        )

        val clientToken = repository.getTokenByConnectionId(connectionId)

        if (clientToken == null) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "Token NOT found. Creating!")
            repository.save(persistToken)
        } else {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "Token found. Updating!")
            repository.update(persistToken)
        }

        authCallback(persistToken)
    }

    suspend fun handleRefreshToken(
        connectionId: Int,
        incomingToken: Token,
        authCallback: suspend (AirPowerToken) -> Unit
    ) {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "handleRefreshToken()")
        val storedToken = repository.getTokenByConnectionId(connectionId)
            ?: throw IllegalStateException("Stored Token not found")

        require(isTokenValid(getTokenFromAirPowerToken(storedToken))) { "Stored Token is not valid" }
        require(isTokenValid(incomingToken)) { "Incoming Token is not valid" }

        storedToken.apply {
            jwt = incomingToken.token
            refreshToken = incomingToken.refreshToken
            scope = incomingToken.scope ?: ""
        }

        repository.update(storedToken)
        authCallback(storedToken)
    }

//    suspend fun isTokenExpiredForConnection(connectionId: Int): Boolean {
//        val tokenByClient = repository.getTokenByConnectionId(connectionId) ?: return true.also {
//            AirPowerLog.w(TAG, "Token not found for connectionId: $connectionId")
//        }
//
//        val jwtParts = tokenByClient.jwt.split(".")
//        if (jwtParts.size != 3) return true.also {
//            if (AirPowerLog.ISVERBOSE)
//                AirPowerLog.w(TAG, "Invalid JWT format")
//        }
//
//        return try {
//            val decodedPayload = Base64.getUrlDecoder().decode(jwtParts[1]).decodeToString()
//            val expIndex = decodedPayload.indexOf("\"exp\"")
//
//            if (expIndex != -1) {
//                val expValue = decodedPayload.substring(expIndex)
//                    .substringAfter(":")
//                    .substringBefore(",")
//                    .trim()
//                    .toLong()
//
//                val now = System.currentTimeMillis() / 1000
//                if (AirPowerLog.ISVERBOSE)
//                    AirPowerLog.d(TAG, "exp: $expValue, system time: $now")
//                expValue < now
//            } else {
//                if (AirPowerLog.ISVERBOSE)
//                    AirPowerLog.e(TAG, "Expiration time not found")
//                true
//            }
//        } catch (e: Exception) {
//            AirPowerLog.e(TAG, "Error parsing JWT: ${e.message}")
//            true
//        }
//    }

    suspend fun isTokenExpiredForConnection(connectionId: Int): Boolean {
        val tokenByClient = repository.getTokenByConnectionId(connectionId) ?: return true.also {
            AirPowerLog.w(TAG, "Token not found for connectionId: $connectionId")
        }

        val jwtParts = tokenByClient.jwt.split(".")
        if (jwtParts.size != 3) return true.also {
            if (AirPowerLog.ISVERBOSE)
                AirPowerLog.w(TAG, "Invalid JWT format")
        }

        return try {
            val payloadJson = decodeJwtPayload(jwtParts[1])
            val exp = payloadJson.getLong("exp")
            val now = System.currentTimeMillis() / 1000

            if (AirPowerLog.ISVERBOSE)
                AirPowerLog.d(TAG, "exp: $exp, now: $now")
            exp < now
        } catch (e: Exception) {
            AirPowerLog.e(TAG, "Error parsing JWT: ${e.message}")
            true
        }
    }

    private fun decodeJwtPayload(encodedPayload: String): JSONObject {
        val decoded = Base64.getUrlDecoder().decode(encodedPayload)
        return JSONObject(String(decoded))
    }


    suspend fun getJwtForConnectionId(connectionId: Int): String {
        if (AirPowerLog.ISVERBOSE)
            AirPowerLog.d(TAG, "getJWTForConnectionId(): $connectionId")
        return getTokenForConnectionId(connectionId)?.token ?: ""
    }

    suspend fun getTokenForConnectionId(connectionId: Int): Token? {
        if (AirPowerLog.ISVERBOSE)
            AirPowerLog.d(TAG, "getTokenForConnectionId(): $connectionId")
        val token = repository.getTokenByConnectionId(connectionId)

        return token?.let { getTokenFromAirPowerToken(it) }
            ?: run {
                if (AirPowerLog.ISVERBOSE)
                    AirPowerLog.w(TAG, "Token is null for connection: $connectionId")
                null
            }
    }

    suspend fun resetTokenForConnection(connectionId: Int) {
        repository.getTokenByConnectionId(connectionId)?.apply {
            scope = "empty"
            jwt = "empty"
            refreshToken = "empty"
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.d(TAG, "Resetting token for connectionId: $connectionId")
            repository.update(this)
        } ?: AirPowerLog.w(TAG, "Token not found for connectionId: $connectionId")
    }

    private fun getTokenFromAirPowerToken(airPowerToken: AirPowerToken): Token {
        return try {
//            if (AirPowerLog.ISLOGABLE)
//                AirPowerLog.d(TAG, "getTokenFromAirPowerToken: $airPowerToken")
            Token(
                airPowerToken.jwt,
                airPowerToken.refreshToken,
                airPowerToken.scope
            )
        } catch (e: Exception) {
            AirPowerLog.e(TAG, "Error while building Token object: ${e.message}")
            Token("", "", "")
        }
    }

    fun isTokenValid(token: Token?): Boolean {
        return when {
            token == null -> {
                if (AirPowerLog.ISLOGABLE)
                    AirPowerLog.w(TAG, "Token is null")
                false
            }

            token.token.isBlank() || token.token.length < 50 -> {
                if (AirPowerLog.ISVERBOSE)
                    AirPowerLog.w(TAG, "JWT is NOT valid")
                false
            }

            token.refreshToken.isBlank() || token.refreshToken.length < 50 -> {
                if (AirPowerLog.ISLOGABLE)
                    AirPowerLog.w(TAG, "Refresh token is NOT valid")
                false
            }

            else -> {
                if (AirPowerLog.ISVERBOSE)
                    AirPowerLog.d(TAG, "Token is valid")
                true
            }
        }
    }
}