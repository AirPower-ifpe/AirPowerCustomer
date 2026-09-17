package com.ifpe.edu.br.model.repository.persistence.manager;
// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


import android.content.Context;
import android.content.SharedPreferences;

import com.ifpe.edu.br.model.util.AirPowerLog;

public class SharedPrefManager {
    private static final String TAG = SharedPrefManager.class.getSimpleName();
    private static final String PREF_FILE_NAME = "AirPowerApp-Preference";
    private final SharedPreferences mSP;
    private final SharedPreferences.Editor mEditor;
    private static SharedPrefManager instance;

    private static final String KEY_LOCAL_IP = "local_ip";
    private static final String KEY_VPN_IP = "vpn_ip";
    private static final String KEY_IS_FIRST_RUN = "is_first_run";
    private static final String KEY_FORCE_VPN = "force_vpn";

    private static final String DEFAULT_VPN_IP = "https://192.168.15.12:8443";
    private static final String DEFAULT_LOCAL_IP = "https://192.168.15.12:8443";
    private Context context = null;

    public static SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.d(TAG, "create instance");
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
        this.context = context;
    }

    public void writeString(String key, String value) {
        if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
            AirPowerLog.e(TAG, "incorrect data: key:" + key + " value:" + value);
            return;
        }
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public Context getContext() {
        return context;
    }

    public void writeBoolean(String key, Boolean value) {
        if (key == null || key.isEmpty() || value == null) {
            AirPowerLog.e(TAG, "incorrect data: key:" + key + " value:" + value);
            return;
        }
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    public Boolean getBoolean(String key) {
        if (key == null || key.isEmpty()) {
            AirPowerLog.e(TAG, "incorrect data: key:" + key);
            return null;
        }
        return mSP.getBoolean(key, false);
    }

    public String readString(String key) {
        if (key == null || key.isEmpty()) {
            AirPowerLog.e(TAG, "incorrect data: key:" + key);
            return null;
        }
        return mSP.getString(key, null);
    }

    public String getLocalIp() {
        return mSP.getString(KEY_LOCAL_IP, DEFAULT_LOCAL_IP);
    }

    public String getVpnIp() {
        return mSP.getString(KEY_VPN_IP, DEFAULT_VPN_IP);
    }

    public boolean isFirstRun() {
        return mSP.getBoolean(KEY_IS_FIRST_RUN, true);
    }

    public boolean isForceVpn() {
        return mSP.getBoolean(KEY_FORCE_VPN, false);
    }

    public void setServerIps(String localIp, String vpnIp) {
        writeString(KEY_LOCAL_IP, localIp);
        writeString(KEY_VPN_IP, vpnIp);
        writeBoolean(KEY_IS_FIRST_RUN, false);
    }

    public void setForceVpn(boolean forceVpn) {
        writeBoolean(KEY_FORCE_VPN, forceVpn);
    }

}
