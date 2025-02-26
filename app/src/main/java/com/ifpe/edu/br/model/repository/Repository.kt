package com.ifpe.edu.br.model.repository
/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ifpe.edu.br.core.api.ConnectionManager
import com.ifpe.edu.br.model.repository.persistence.AirPowerDatabase
import com.ifpe.edu.br.model.repository.persistence.manager.JWTManager
import com.ifpe.edu.br.model.repository.persistence.manager.SharedPrefManager
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerToken
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerUser
import com.ifpe.edu.br.model.repository.remote.api.ThingsBoardConnectionContractImpl
import com.ifpe.edu.br.model.repository.remote.api.ThingsBoardManager
import com.ifpe.edu.br.model.repository.remote.dto.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.Device
import com.ifpe.edu.br.model.repository.remote.dto.ThingsBoardUser
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.TokenExpiredException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository private constructor(context: Context) {
    private val db = AirPowerDatabase.getDataBaseInstance(context)
    private val tokenDao = db.getTokenDaoInstance()
    private val userDao = db.getUserDaoInstance()
    private val spManager = SharedPrefManager.getInstance()
    private val thingsBoardConnection =
        ConnectionManager.getInstance().getConnection(ThingsBoardConnectionContractImpl)
    private val thingsBoardMgr = ThingsBoardManager(thingsBoardConnection)

    private val _currentUser = MutableLiveData<AirPowerUser?>()
    val currentUser: LiveData<AirPowerUser?> get() = _currentUser

    private val _devices = MutableLiveData<List<Device>>()
    val devices: MutableLiveData<List<Device>> get() = _devices

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun build(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = Repository(context)
                        if (AirPowerLog.ISLOGABLE)
                            AirPowerLog.d(TAG, "AirPowerRepository built")
                    }
                }
            }
        }

        fun getInstance(): Repository {
            return instance
                ?: throw IllegalStateException("AirPowerRepository not initialized. Call build() first.")
        }

        private const val TAG = "AirPowerRepository"
    }

    suspend fun authenticate(
        user: AuthUser,
        onSuccessCallback: () -> Unit
    ) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "authenticate()")
        thingsBoardMgr.auth(user) { onSuccessCallback.invoke() }
        val tbUser = thingsBoardMgr.getCurrentUser()
        save(tbUser)
        _currentUser.value = tbUser.toAirPowerUser()
    }

    suspend fun updateSession(
        onSuccessCallback: () -> Unit,
    ) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "updateSession()")
        thingsBoardMgr.refreshToken { onSuccessCallback.invoke() }
    }

    suspend fun isSessionExpired(): Boolean {
        val connectionId = ThingsBoardConnectionContractImpl.getConnectionId()
        val isExpired = connectionId.let { JWTManager.isTokenExpiredForConnection(it) }
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "isSessionExpired(): $isExpired")
        return isExpired
    }

    suspend fun logout() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "logout()")
        val connectionId = ThingsBoardConnectionContractImpl.getConnectionId()
        JWTManager.resetTokenForConnection(connectionId)
        currentUser.value?.let { delete(it) }
        _currentUser.value = null
    }

    suspend fun getDevicesForCurrentUser() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getDevicesForCurrentUser()")
        val user = currentUser.value
            ?: throw IllegalStateException("getDevicesForCurrentUser() user is null")
        try {
            val devicesList = thingsBoardMgr.getAllDevicesForCustomer(user)
            withContext(Dispatchers.Main) {
                _devices.value = devicesList
            }
        } catch (e: TokenExpiredException) {
            updateSession { }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    suspend fun getTokenByConnectionId(connection: Int): AirPowerToken? {
        return withContext(Dispatchers.IO) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getToken for connection: $connection")
            tokenDao.getTokenByClient(connection)
        }
    }

    private suspend fun save(user: ThingsBoardUser) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "save: $user")
        require(isUserValid(user)) { "ThingsBoardUser is invalid" }

        withContext(Dispatchers.IO) {
            try {
                val airPowerUser = user.toAirPowerUser()
                userDao.insert(airPowerUser)
            } catch (e: Exception) {
                throw IllegalStateException("Error persisting user in DB: ${e.message}")
            }
        }
    }

    private suspend fun delete(user: AirPowerUser) {
        withContext(Dispatchers.IO) {
            try {
                val persistUser = userDao.getUserById(user.id)
                persistUser?.let { userDao.delete(it) }
            } catch (e: Exception) {
                throw IllegalStateException("Error deleting user in DB: ${e.message}")
            }
        }
    }

    suspend fun getCurrentAirPowerUser(): AirPowerUser? {
        return withContext(Dispatchers.IO) {
            try {
                userDao.findAll().firstOrNull()
            } catch (e: Exception) {
                throw IllegalStateException("Error getting current user from DB: ${e.message}")
            }
        }
    }

    suspend fun save(token: AirPowerToken) {
        require(!token.jwt.isNullOrEmpty() && !token.refreshToken.isNullOrEmpty() && token.client != null) {
            "Token info is null or empty!"
        }

        withContext(Dispatchers.IO) {
            val existingToken = tokenDao.getTokenByClient(token.client)
            if (existingToken != null) {
                AirPowerLog.e(TAG, "save(): ERROR: Token exists for client! $existingToken")
                return@withContext
            }
            tokenDao.insert(token)
        }
    }

    suspend fun update(token: AirPowerToken) {
        require(!token.jwt.isNullOrEmpty() && !token.refreshToken.isNullOrEmpty() && token.client != null) {
            "Token info is null or empty!"
        }

        withContext(Dispatchers.IO) {
            val existingToken = tokenDao.getTokenByClient(token.client)
            existingToken?.let {
                it.jwt = token.jwt
                it.refreshToken = token.refreshToken
                it.scope = token.scope
                tokenDao.update(it)
            } ?: AirPowerLog.e(TAG, "update(): Can't update token. Token does not exist")
        }
    }

    fun writeString(key: String, value: String) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "writeString key:$key value:$value")
        spManager.writeString(key, value)
    }

    fun readString(key: String): String {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "readString key:$key")
        return spManager.readString(key)
    }

    private fun isUserValid(user: ThingsBoardUser): Boolean {
        return when {
            user.authority.isEmpty() -> {
                AirPowerLog.d(TAG, "User authority is null or empty")
                false
            }

            user.customerId.id.isNullOrEmpty() -> {
                AirPowerLog.d(TAG, "Customer ID is null or empty")
                false
            }

            else -> true
        }
    }

    private fun ThingsBoardUser.toAirPowerUser(): AirPowerUser {
        return AirPowerUser(
            id = id.id,
            authority = authority,
            email = email,
            customerId = customerId.id,
            firstName = firstName,
            lastName = lastName,
            name = name,
            phone = phone
        )
    }
}