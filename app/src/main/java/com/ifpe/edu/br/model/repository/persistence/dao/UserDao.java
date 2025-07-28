package com.ifpe.edu.br.model.repository.persistence.dao;/*
 * Trabalho de conclusão de curso - IFPE 2025
 * Author: Willian Santos
 * Project: AirPower Costumer
 */

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ifpe.edu.br.model.repository.persistence.model.AirPowerUser;

import java.util.List;

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

    @Query("DELETE FROM AIR_POWER_USER")
    void deleteAll();
}
