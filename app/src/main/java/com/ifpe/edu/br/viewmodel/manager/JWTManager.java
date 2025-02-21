package com.ifpe.edu.br.viewmodel.manager;

// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


import com.ifpe.edu.br.model.dto.Token;
import com.ifpe.edu.br.model.model.auth.AirPowerToken;
import com.ifpe.edu.br.model.repo.AirPowerRepository;
import com.ifpe.edu.br.viewmodel.util.AirPowerLog;
import com.ifpe.edu.br.viewmodel.util.AirPowerUtil;

import java.util.Base64;

public class JWTManager {
    private static final String TAG = JWTManager.class.getSimpleName();
    private static JWTManager instance;
    private final AirPowerRepository mRepo;

    private JWTManager() {
        mRepo = AirPowerRepository.getInstance();
    }

    public static JWTManager getInstance() {
        if (instance == null) {
            instance = new JWTManager();
        }
        return instance;
    }

    public void handleAuthentication(Integer connectionId,
                                     Token token,
                                     IHandleAuthCallback authCallback) {
        if (token == null) {
            AirPowerLog.e(TAG, "handleAuthentication() ERROR: auth token is null");
            authCallback.onFailure(-1); // todo add error code here
            return;
        }
        String jwt = token.getToken();
        String refreshToken = token.getRefreshToken();
        String scope = token.getScope();
        if (AirPowerUtil.Text.isNullOrEmpty(jwt) || AirPowerUtil.Text.isNullOrEmpty(refreshToken)) {
            AirPowerLog.e(TAG, "handleAuthentication() ERROR: jwt or token is/are null");
            authCallback.onFailure(-2); // todo add error code here
            return;
        }
        AirPowerToken persistToken = new AirPowerToken(
                connectionId,
                jwt,
                refreshToken,
                scope);
        AirPowerToken clientToken = mRepo.getTokenByConnectionId(connectionId);
        if (clientToken == null) {
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.d(TAG, "No token found for connectionId. Creating!");
            mRepo.save(persistToken);
        } else {
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.d(TAG, "Token found for connectionId. Updating!");
            mRepo.update(persistToken);
        }
        authCallback.onSuccess(persistToken);
    }

    public void handleRefreshToken(Integer connectionId,
                                   Token incomingToken,
                                   IHandleAuthCallback authCallback) {
        AirPowerToken tokenByClient = mRepo.getTokenByConnectionId(connectionId);
        if (tokenByClient == null) {
            AirPowerLog.w(TAG, "handleRefreshToken() token not found for connectionId:" + connectionId);
            authCallback.onFailure(-6); // todo add error code here
            return;
        }
        if (incomingToken == null) {
            authCallback.onFailure(-16); // todo add error code here
            return;
        }
        String incomingJwt = incomingToken.getToken();
        String incomingRefreshToken = incomingToken.getRefreshToken();
        incomingToken.getScope();
        String incomingScope = incomingToken.getScope();
        if (AirPowerUtil.Text.isNullOrEmpty(incomingJwt) ||
                AirPowerUtil.Text.isNullOrEmpty(incomingRefreshToken)) {
            authCallback.onFailure(-15);// todo add error code here
            return;
        }
        tokenByClient.setJwt(incomingJwt);
        tokenByClient.setRefreshToken(incomingRefreshToken);
        tokenByClient.setScope(incomingScope);
        mRepo.update(tokenByClient);
        authCallback.onSuccess(tokenByClient);
    }

    public boolean isTokenExpiredForConnection(Integer connectionId) {
        AirPowerToken tokenByClient = mRepo.getTokenByConnectionId(connectionId);
        if (tokenByClient == null) {
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.w(TAG, "isTokenExpiredForClient() token not found for connectionId:" + connectionId);
            return true;
        }
        String[] jwtParts = tokenByClient.getJwt().split("\\.");
        if (jwtParts.length != 3) {
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.w(TAG, "isTokenExpiredForClient() token removed by user");
            return true;
        }
        byte[] decodedPayload = Base64.getUrlDecoder().decode(jwtParts[1]);
        try {
            String payloadString = new String(decodedPayload);
            if (payloadString.contains("\"exp\"")) {
                int expIndex = payloadString.indexOf("\"exp\"");
                String expSubstring = payloadString.substring(expIndex);
                int colonIndex = expSubstring.indexOf(":");
                int commaIndex = expSubstring.indexOf(",");
                String expValueString = expSubstring.substring(colonIndex + 1, commaIndex).trim();
                long expValue = Long.parseLong(expValueString);
                long now = System.currentTimeMillis() / 1000;
                if (AirPowerLog.ISLOGABLE) {
                    AirPowerLog.d(TAG, "exp: " + expValue);
                    AirPowerLog.d(TAG, "system time: " + now);
                }
                boolean isTokenExpired = expValue < now;
                if (AirPowerLog.ISLOGABLE)
                    AirPowerLog.d(TAG, "isSessionExpiredForClient(): " + isTokenExpired);
                return isTokenExpired;
            } else {
                AirPowerLog.e(TAG, "isTokenExpiredForClient() exp. time not found");
                return true;
            }
        } catch (Exception e) {
            AirPowerLog.e(TAG, "isTokenExpiredForClient() parsing error: " + e.getMessage());
            return true;
        }
    }

    public String getJwtForConnectionId(Integer connectionId) {
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "getJwtForConnectionId(): " + connectionId);
        AirPowerToken token = mRepo.getTokenByConnectionId(connectionId);
        if (token == null) {
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.w(TAG, "Token is null for connection:" + connectionId);
            return "";
        }
        return token.getJwt();
    }

    public void resetTokenForConnection(Integer connectionId) {
        AirPowerToken tokenByClient = mRepo.getTokenByConnectionId(connectionId);
        if (tokenByClient == null) {
            AirPowerLog.w(TAG, "getTokenByConnectionId(): " +
                    "token not found for connectionId:" + connectionId);
            return;
        }
        tokenByClient.setScope("empty");
        tokenByClient.setJwt("empty");
        tokenByClient.setRefreshToken("empty");
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "getTokenByConnectionId(): resetting" + connectionId);
        mRepo.update(tokenByClient);
    }

    public interface IHandleAuthCallback {
        void onSuccess(AirPowerToken airPowerToken);
        void onFailure(int failure);
    }
}