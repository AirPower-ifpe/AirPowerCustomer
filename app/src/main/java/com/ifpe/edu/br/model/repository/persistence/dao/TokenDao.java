package com.ifpe.edu.br.model.repository.persistence.dao;
// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ifpe.edu.br.model.repository.persistence.model.AirPowerToken;

import java.util.List;

@Dao
public interface TokenDao {
    @Insert
    void insert(AirPowerToken token);

    @Update
    void update(AirPowerToken token);

    @Query("SELECT * from AIR_POWER_TOKEN")
    List<AirPowerToken> getTokens();

    @Query("SELECT * from AIR_POWER_TOKEN WHERE TOKEN_CLIENT = :client")
    AirPowerToken getTokenByClient(Integer client);
}