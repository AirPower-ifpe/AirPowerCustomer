package com.ifpe.edu.br.model.repo.persistence;
// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ifpe.edu.br.model.model.auth.AirPowerToken;
import com.ifpe.edu.br.model.model.auth.AirPowerUser;
import com.ifpe.edu.br.viewmodel.util.AirPowerLog;

@Database(entities = {
        AirPowerToken.class, AirPowerUser.class}, version = 1, exportSchema = false)
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
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "Database instantiation");
        }
        return dbInstance;
    }

    public abstract TokenDao getTokenDaoInstance();

    public abstract UserDao getUserDaoInstance();
}
