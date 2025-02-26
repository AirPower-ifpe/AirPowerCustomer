package com.ifpe.edu.br.model.repository.persistence.manager;
// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


import android.content.Context;
import android.content.SharedPreferences;

import com.ifpe.edu.br.model.util.AirPowerLog;

public class SharedPrefManager {
    private final String TAG = SharedPrefManager.class.getSimpleName();
    private static final String PREF_FILE_NAME = "AirPowerApp-Preference";
    private final SharedPreferences mSP;
    private final SharedPreferences.Editor mEditor;
    private static SharedPrefManager instance;

    public static SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public static SharedPrefManager getInstance() throws Exception {
        if (instance == null) {
            throw new IllegalStateException("SharedPrefManager error: getInstance called before construction");
        }
        return instance;
    }

    private SharedPrefManager(Context context) {
        mSP = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        mEditor = mSP.edit();
    }

    public void writeString(String key, String value) {
        if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
            AirPowerLog.e(TAG, "incorrect data: key:" + key + " value:" + value);
            return;
        }
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public String readString(String key) {
        if (key == null || key.isEmpty()) {
            AirPowerLog.e(TAG, "incorrect data: key:" + key);
            return null;
        }
        return mSP.getString(key, null);
    }
}