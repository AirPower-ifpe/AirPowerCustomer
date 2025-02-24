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
        if (!isTokenValid(token)) {
            throw new IllegalStateException("Token is not valid");
        }
        String jwt = token.getToken();
        String refreshToken = token.getRefreshToken();
        String scope = token.getScope() == null ? "" : token.getScope();
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
        AirPowerToken storedToken = mRepo.getTokenByConnectionId(connectionId);
        if (!isTokenValid(getTokenFromAirPowerToken(storedToken))) {
            throw new IllegalStateException("Stored Token is not valid");
        }
        if (!isTokenValid(incomingToken)) {
            throw new IllegalStateException("Incoming Token is not valid");
        }
        String incomingJwt = incomingToken.getToken();
        String incomingRefreshToken = incomingToken.getRefreshToken();
        String incomingScope = incomingToken.getScope() == null ? "" : incomingToken.getScope();
        storedToken.setJwt(incomingJwt);
        storedToken.setRefreshToken(incomingRefreshToken);
        storedToken.setScope(incomingScope);
        mRepo.update(storedToken);
        authCallback.onSuccess(storedToken);
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
            AirPowerLog.d(TAG, "getJWTForConnectionId(): " + connectionId);
        Token token = getTokenForConnectionId(connectionId);

        if (token == null) {
            AirPowerLog.w(TAG, "JWT is null");
            return "";
        }
        return token.getToken() == null ? "" : token.getToken();
    }

    public Token getTokenForConnectionId(Integer connectionId) {
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "getTokenForConnectionId(): " + connectionId);
        AirPowerToken token = mRepo.getTokenByConnectionId(connectionId);
        if (token == null) {
            AirPowerLog.w(TAG, "Token is null for connection:" + connectionId);
            return null;
        }
        return getTokenFromAirPowerToken(token);
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
    }

    private Token getTokenFromAirPowerToken(AirPowerToken airPowerToken) {
        try {
            if (AirPowerLog.ISLOGABLE) {
                AirPowerLog.d(TAG, "getTokenFromAirPowerToken():" + airPowerToken.toString());
            }
            return new Token(
                    airPowerToken.getJwt(),
                    airPowerToken.getRefreshToken(),
                    airPowerToken.getScope()
            );
        } catch (Exception e) {
            AirPowerLog.e(TAG, "Error while building Token object: " + e.getMessage());
            return null;
        }
    }

    public boolean isTokenValid(Token token) {
        String reason = "check success";
        boolean isValid = true;
        if (token == null) {
            reason = "token is null";
            isValid = false;
        } else if (AirPowerUtil.Text.isNullOrEmpty(token.getToken())
                || token.getToken().length() < 50) {
            reason = "JWT is NOT valid";
            isValid = false;
        } else if (AirPowerUtil.Text.isNullOrEmpty(token.getRefreshToken())
                || token.getRefreshToken().length() < 50) {
            reason = "Refresh token is NOT valid";
            isValid = false;
        }
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "isTokenValid(): " + isValid + " reason: " + reason);
        return isValid;
    }
}