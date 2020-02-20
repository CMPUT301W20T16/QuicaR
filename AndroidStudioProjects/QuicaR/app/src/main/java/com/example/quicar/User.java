package com.example.quicar;

public class User {
    private String name;
    private Boolean isDriver;

    public User() {
        name = "testing";
        isDriver = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDriver() {
        return isDriver;
    }

    public void setDriver(Boolean driver) {
        isDriver = driver;
    }
}
