package com.ifpe.edu.br.model.model.auth;
// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AIR_POWER_TOKEN")
public class AirPowerToken {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "TOKEN_ID")
    private int id;
    @ColumnInfo(name = "TOKEN_CLIENT")
    private Integer client;
    @ColumnInfo(name = "TOKEN_JWT")
    private String jwt;
    @ColumnInfo(name = "TOKEN_REFRESH_TOKEN")
    private String refreshToken;
    @ColumnInfo(name = "TOKEN_SCOPE")
    private String scope;

    /**
     * Entity class, used to describe persistence
     * objects and manage them, should NOT be used
     * as dto class
     *
     * @param client is the identifier id
     * @param jwt auth token
     * @param refreshToken used to get new valid jwt
     * @param scope of privileges allowed for current user
     *
     */
    public AirPowerToken(Integer client, String jwt, String refreshToken, String scope) {
        this.client = client;
        if (client == null) this.client = 0;
        this.jwt = jwt;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getClient() {
        return client;
    }

    public void setClient(Integer client) {
        this.client = client;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @NonNull
    @Override
    public String toString() {
        return "AirPowerToken{" +
                "id=" + id +
                ", client='" + client + '\'' +
                ", jwt='" + jwt + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}