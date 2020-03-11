package com.example.quicar;

/**
 * This is an object that store the user's acitivity state for convenient
 */
public class UserState {
    private User currentUser = new User();
    private String currentMode;
    private String token;
    private Boolean active = Boolean.FALSE;
    private Boolean onGoing = Boolean.FALSE;
    private Location firstSelectedLocation;
    private Location secondSelectedLocation;
    private String activityState;

    UserState() {
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getCurrentUserName() {
        return currentUser.getName();
    }

    public void setCurrentUserName(String currentUserName) {
        currentUser.setName(currentUserName);
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getOnGoing() {
        return onGoing;
    }

    public void setOnGoing(Boolean onGoing) {
        this.onGoing = onGoing;
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
