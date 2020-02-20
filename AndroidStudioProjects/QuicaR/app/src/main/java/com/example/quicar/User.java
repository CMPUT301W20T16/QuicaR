package com.example.quicar;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

public class User {

    private String name;

    private Boolean isDriver;

    public User() {
        name = "testing";
        isDriver = false;
    }

    @PropertyName("username")
    public String getName() {
        return name;
    }

    @PropertyName("username")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("isDriver")
    public Boolean getDriver() {
        return isDriver;
    }

    @PropertyName("isDriver")
    public void setDriver(Boolean driver) {
        isDriver = driver;
    }
}
