package com.ifpe.edu.br.model.repo.persistence;/*
 * Trabalho de conclusão de curso - IFPE 2025
 * Author: Willian Santos
 * Project: AirPower Costumer
 */

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ifpe.edu.br.model.model.auth.AirPowerToken;
import com.ifpe.edu.br.model.model.auth.AirPowerUser;

import java.util.List;
import java.util.Map;

@Dao
public interface UserDao {

    @Insert
    void insert(AirPowerUser user);

    @Query("SELECT * FROM AIR_POWER_USER WHERE USER_ID = :id")
    AirPowerUser getUserById(String id);

    @Update
    void update(AirPowerUser user);

    @Delete
    void delete(AirPowerUser user);

    @Query("SELECT * FROM AIR_POWER_USER")
    List<AirPowerUser> findAll();
}
