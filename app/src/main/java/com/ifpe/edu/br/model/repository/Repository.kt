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
import com.ifpe.edu.br.model.util.AuthenticateFailureException
import com.ifpe.edu.br.model.util.InvalidStateException
import com.ifpe.edu.br.model.util.TokenExpiredException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository private constructor(context: Context) {
    private val db = AirPowerDatabase.getDataBaseInstance(context)
    private val tokenDao = db.getTokenDaoInstance()
    private val userDao = db.getUserDaoInstance()
    private val spManager = SharedPrefManager.getInstance(context)
    private val thingsBoardConnection =
        ConnectionManager.getInstance().getConnectionById(ThingsBoardConnectionContractImpl)
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
                        if (AirPowerLog.ISLOGABLE)
                            AirPowerLog.d(TAG, "build()")
                        instance = Repository(context)
                    }
                }
            }
        }

        fun getInstance(): Repository {
            return instance
                ?: throw IllegalStateException("AirPowerRepository not initialized. Call build() first.")
        }

        private val TAG = Repository::class.simpleName
    }

    suspend fun authenticate(
        user: AuthUser,
        onSuccessCallback: () -> Unit,
        onFailureCallback: (e: Exception) -> Unit
    ) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "authenticate()")
        try {
            thingsBoardMgr.auth(user) { onSuccessCallback.invoke() }
        } catch (e: Exception) {
            onFailureCallback.invoke(e)
            throw e
        }
    }

    suspend fun updateSession(
        onSuccessCallback: () -> Unit,
    ) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "updateSession()")
        try {
            thingsBoardMgr.refreshToken { onSuccessCallback.invoke() }
        } catch (e: Exception) {
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.w(TAG, "Exception -> updateSession() -> ${e.message}")
            throw e
        }
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
            ?: throw IllegalStateException("[$TAG]: Exception -> [current user is null]")
        try {
            val devicesList = thingsBoardMgr.getAllDevicesForCustomer(user)
            withContext(Dispatchers.Main) {
                _devices.value = devicesList
            }
        } catch (e: TokenExpiredException) {
            AirPowerLog.d(TAG, "[$TAG]: TokenExpiredException -> ${e.message}")
            throw e
        } catch (e: Exception) {
            AirPowerLog.d(TAG, "[$TAG]: Exception -> ${e.message}")
            throw e
        }
    }

    suspend fun getTokenByConnectionId(connection: Int): AirPowerToken? {
        return withContext(Dispatchers.IO) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getTokenByConnectionId: $connection")
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
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "save() token: $token")
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
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "update() token: $token")
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

    suspend fun retrieveCurrentUser(
        onSuccessCallback: (user: AirPowerUser?) -> Unit
    ) {
        try {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "retrieveCurrentUser()")
            val user = thingsBoardMgr.getCurrentUser()
            _currentUser.value = user.toAirPowerUser()
            onSuccessCallback.invoke(_currentUser.value)
        } catch (e: Exception) {
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.w(TAG, "[$TAG]: Exception: -> ${e.message}")
            throw e
        }
    }

    fun isCurrentUserValid(): Boolean {
        return _currentUser.value != null
    }
}