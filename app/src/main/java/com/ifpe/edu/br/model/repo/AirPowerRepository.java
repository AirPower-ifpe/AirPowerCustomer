package com.ifpe.edu.br.model.repo;

// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


import android.content.Context;

import androidx.annotation.NonNull;

import com.ifpe.edu.br.model.model.auth.AirPowerToken;
import com.ifpe.edu.br.model.repo.persistence.AirPowerDatabase;
import com.ifpe.edu.br.model.repo.persistence.TokenDao;
import com.ifpe.edu.br.viewmodel.manager.SharedPrefManager;
import com.ifpe.edu.br.viewmodel.util.AirPowerLog;
import com.ifpe.edu.br.viewmodel.util.AirPowerUtil;

public class AirPowerRepository {
    private static final String TAG = AirPowerRepository.class.getSimpleName();

    private static AirPowerRepository instance;
    private final TokenDao mTokenDao;
    private final SharedPrefManager mSPManager;

    private AirPowerRepository(Context context) throws Exception {
        AirPowerDatabase db = AirPowerDatabase.getDataBaseInstance(context);
        mTokenDao = db.getTokenDaoInstance();
        mSPManager = SharedPrefManager.getInstance();
    }

    public static void build(Context context) throws Exception {
        if (instance == null) {
            instance = new AirPowerRepository(context);
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.d(TAG, "AirPowerRepository build");
        }
    }

    public static AirPowerRepository getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AirPowerRepository error: getInstance() called before build() method");
        }
        return instance;
    }

    public AirPowerToken getTokenByConnectionId(@NonNull Integer connection) {
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "get token for connection: " + connection);
        return mTokenDao.getTokenByClient(connection);
    }

    public void save(AirPowerToken token) {
        if (token == null) {
            AirPowerLog.e(TAG, "save(): Given token is null. Cancelling");
            return;
        }
        final String jwt = token.getJwt();
        final String refreshToken = token.getRefreshToken();
        final Integer client = token.getClient();
        if (AirPowerUtil.Text.isNullOrEmpty(jwt) || AirPowerUtil.Text.isNullOrEmpty(refreshToken)
                || client == null) {
            AirPowerLog.e(TAG, "save(): ERROR: some token info is null!");
            return;
        }
        AirPowerToken tokenByClient = mTokenDao.getTokenByClient(token.getClient());
        if (tokenByClient != null) {
            AirPowerLog.e(TAG, "save(): ERROR: Token exists for client!" + tokenByClient);
            return;
        }
        if (AirPowerLog.ISLOGABLE) {
            AirPowerLog.d(TAG, "save(): token");
            AirPowerLog.d(TAG, "jwt:" + jwt);
            AirPowerLog.d(TAG, "refreshToken:" + refreshToken);
            AirPowerLog.d(TAG, "client:" + client);
        }
        mTokenDao.insert(token);
    }

    public void update(AirPowerToken token) {
        if (token == null) {
            AirPowerLog.e(TAG, "update(): Given token is null. Cancelling");
            return;
        }
        final String jwt = token.getJwt();
        final String refreshToken = token.getRefreshToken();
        final Integer client = token.getClient();
        if (AirPowerUtil.Text.isNullOrEmpty(jwt) || AirPowerUtil.Text.isNullOrEmpty(refreshToken)
                || client == null) {
            AirPowerLog.e(TAG, "update(): ERROR: some token info is null or empty!");
            return;
        }
        AirPowerToken tokenByClient = mTokenDao.getTokenByClient(client);
        if (tokenByClient == null) {
            AirPowerLog.e(TAG, "update(): Can't update token. Token does not exist");
            return;
        }
        tokenByClient.setJwt(jwt);
        tokenByClient.setRefreshToken(refreshToken);
        tokenByClient.setScope(token.getScope());
        if (AirPowerLog.ISLOGABLE) {
            AirPowerLog.d(TAG, "update(): token");
            AirPowerLog.d(TAG, "jwt:" + jwt);
            AirPowerLog.d(TAG, "refreshToken:" + refreshToken);
            AirPowerLog.d(TAG, "client:" + client);
        }
        mTokenDao.update(tokenByClient);
    }


    public void writeString(@NonNull String key,
                            @NonNull String value) {
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "writeString key:" + key + " value:" + value);
        mSPManager.writeString(key, value);
    }

    public String readString(@NonNull String key) {
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "readString key:" + key);
        return mSPManager.readString(key);
    }

}