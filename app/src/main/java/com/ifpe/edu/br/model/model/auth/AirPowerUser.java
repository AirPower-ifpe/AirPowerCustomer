package com.ifpe.edu.br.model.model.auth;
/*
 * Trabalho de conclusão de curso - IFPE 2025
 * Author: Willian Santos
 * Project: AirPower Costumer
 */

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AIR_POWER_USER")
public class AirPowerUser {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "USER_ID")
    private String id;
    @ColumnInfo(name = "USER_AUTHORITY")
    private String authority;
    @ColumnInfo(name = "USER_COSTUMER_ID")
    private String customerId;
    @ColumnInfo(name = "USER_FIRST_NAME")
    private String firstName;
    @ColumnInfo(name = "USER_LAST_NAME")
    private String lastName;
    @ColumnInfo(name = "USER_NAME")
    private String name;
    @ColumnInfo(name = "USER_PHONE")
    private String phone;
    @ColumnInfo(name = "USER_EMAIL")
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "AirPowerUser{" +
                "id=" + id +
                ", authority='" + authority + '\'' +
                ", customerId='" + customerId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}