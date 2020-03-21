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
    private Boolean onArrived = Boolean.FALSE;
    private Request currentRequest;
    private String activityState;

    /**
     * This is the empty constructor for UserState
     */
    UserState() {
    }

    /**
     * This method return current user
     * @return
     *  current user object
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * This method set the value of current user
     * @param currentUser
     *  candidate user object
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * This method return the name of current user
     * @return
     */
    public String getCurrentUserName() {
        return currentUser.getName();
    }

    /**
     * This method set the name of current user
     * @param currentUserName
     *  candidate name of current user
     */
    public void setCurrentUserName(String currentUserName) {
        currentUser.setName(currentUserName);
    }

    /**
     * This method return current mode of the user ("rider" or "driver")
     * @return
     *  current mode of user
     */
    public String getCurrentMode() {
        return currentMode;
    }

    /**
     * This method set the value of current mode
     * @param currentMode
     *  camdidate current mode of user
     */
    public void setCurrentMode(String currentMode) {
        this.currentMode = currentMode;
    }

    /**
     * This method return the token of current user's device
     * @return
     *  token of current device
     */
    public String getToken() {
        return token;
    }

    /**
     * This method set the value of token of current user's device
     * @param token
     *  candidate value of token of current device
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * This method return the activity state of current user's request
     * @return
     *  activity state of current user's request
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * This method set the value of activity state of current user's request
     * @param active
     *  boolean for activity state
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * This method return the on going state of current user's request
     * @return
     *  on going state of current user's request
     */
    public Boolean getOnGoing() {
        return onGoing;
    }

    /**
     * This method set the value of on going state of current user's request
     * @param onGoing
     *  boolean for on going state
     */
    public void setOnGoing(Boolean onGoing) {
        this.onGoing = onGoing;
    }

    /**
     * This method return the arriving state of current user's request
     * @return
     *  arriving state of current user's request
     */
    public Boolean getOnArrived() {
        return onArrived;
    }

    /**
     * This method set the arriving state of current user's request
     * @param onArrived
     *  arriving state of current user's request
     */
    public void setOnArrived(Boolean onArrived) {
        this.onArrived = onArrived;
    }

    /**
     * This method return the activity state of the user
     * @return
     *  activity state of the user
     */
    public String getActivityState() {
        return activityState;
    }

    /**
     * This method set the activity state of the user
     * @param activityState
     *  candidiate activity state
     */
    public void setActivityState(String activityState) {
        this.activityState = activityState;
    }

    public String getRequestID() {
        return currentRequest.getRid();
    }

    public Request getCurrentRequest() {
        return currentRequest;
    }

    public void setCurrentRequest(Request currentRequest) {
        this.currentRequest = currentRequest;
    }
}
