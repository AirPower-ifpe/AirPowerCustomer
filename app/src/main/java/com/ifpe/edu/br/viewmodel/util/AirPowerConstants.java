package com.ifpe.edu.br.viewmodel.util;
// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


public interface AirPowerConstants {

    String KEY_COD_DRAWABLE = "drawable";
    String KEY_DEVICE_ID = "key_device_id";

    int DEVICE_CONNECTION_SUCCESS = 1;
    int DEVICE_CONNECTION_FAIL = 2;
    int NETWORK_CONNECTION_SUCCESS = 3;
    int EDIT_NETWORK_CONNECTION_SUCCESS = 4;
    int NETWORK_CONNECTION_FAILURE = 5;

    int ACTION_NONE = -1;
    int ACTION_REGISTER_DEVICE = 1;
    int ACTION_EDIT_DEVICE = 2;
    String ACTION_EDIT_DEVICE_ = "action_edit_device";
    String ACTION_LAUNCH_MY_DEVICES = "action_launch_my_devices";
    String ACTION_LAUNCH_DETAIL = "action_launch_detail";
    int INVALID_DEVICE_ID = -999999999;
    String ACTION_NEW_DEVICE = "action_new_device";
    // HTTP constants
    int HTTP_OK = 200;

    // Group constants
    String ACTION_NEW_GROUP = "action_new_group";
    String ACTION_EDIT_GROUP = "action_edit_group";
    int AIR_POWER_DEVICE_SUCCESSFULLY_PAIRED = 12309;
    int THINGSBOARD_PROVISION_OK = 76777;

    interface TransportType {
        String MQTT = "MQTT";
        String HTTP = "HTTP";
        String STUB = "STUB";
        String DEFAULT = "DEFAULT";
    }

    interface CredentialType{
        String MQTT_BASIC = "MQTT_BASIC";
    }
}