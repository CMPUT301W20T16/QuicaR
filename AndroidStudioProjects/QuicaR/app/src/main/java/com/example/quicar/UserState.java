package com.example.quicar;

/**
 * This is an object that store the user's acitivity state for convenient
 */
public class UserState {
    private String currentUserName;
    private String currentMode;
    private String token;
    private Boolean notified = Boolean.FALSE;
    private Location firstSelectedLocation;
    private Location secondSelectedLocation;
    private String activityState;

    UserState() {
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public String getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(String currentMode) {
        this.currentMode = currentMode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public Location getFirstSelectedLocation() {
        return firstSelectedLocation;
    }

    public void setFirstSelectedLocation(Location firstSelectedLocation) {
        this.firstSelectedLocation = firstSelectedLocation;
    }

    public Location getSecondSelectedLocation() {
        return secondSelectedLocation;
    }

    public void setSecondSelectedLocation(Location secondSelectedLocation) {
        this.secondSelectedLocation = secondSelectedLocation;
    }

    public String getActivityState() {
        return activityState;
    }

    public void setActivityState(String activityState) {
        this.activityState = activityState;
    }
}
