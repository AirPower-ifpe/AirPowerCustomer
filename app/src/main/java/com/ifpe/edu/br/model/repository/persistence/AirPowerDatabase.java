package com.ifpe.edu.br.model.repository.persistence;
// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ifpe.edu.br.model.repository.persistence.dao.TokenDao;
import com.ifpe.edu.br.model.repository.persistence.dao.UserDao;
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerToken;
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerUser;
import com.ifpe.edu.br.model.util.AirPowerLog;

@Database(entities = {
        AirPowerToken.class, AirPowerUser.class}, version = 3, exportSchema = false)
public abstract class AirPowerDatabase extends RoomDatabase {

    public static final String TAG = AirPowerDatabase.class.getSimpleName();
    public static final String DATABASE_NAME = "AirPowerApp.db";
    private static AirPowerDatabase dbInstance;

    public static synchronized AirPowerDatabase getDataBaseInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = Room.databaseBuilder(context, AirPowerDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration() // TODO remove before release
                    .build();
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "create instance");
        }
        return dbInstance;
    }

    public abstract TokenDao getTokenDaoInstance();

    public abstract UserDao getUserDaoInstance();
}
