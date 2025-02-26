package com.ifpe.edu.br.model.repository.remote.dto;/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import com.ifpe.edu.br.model.util.AirPowerLog;

import org.json.JSONException;
import org.json.JSONObject;

public class Id {
    private final String TAG = Id.class.getSimpleName();
    private String id;
    private String entityType;

    public Id(JSONObject idObj) {
        try {
            id = idObj.getString("id");
            entityType = idObj.getString("entityType");
        } catch (JSONException e) {
            id = "";
            entityType = "";
            AirPowerLog.e(TAG, "Parser error construction");
        }
    }

    public Id(String id, String entityType) {
        this.id = id;
        this.entityType = entityType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Override
    public String toString() {
        return "Id{" +
                "id='" + id + '\'' +
                ", entityType='" + entityType + '\'' +
                '}';
    }
}