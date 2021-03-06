package com.example.datahelper;

import com.example.entity.Request;
import com.example.user.User;

/**
 * This is an object that store the user's acitivity state for convenient
 */
public class UserState {
    private User currentUser = new User();
    private String currentMode = "rider";
    private String token;
    private Boolean onConfirm = Boolean.FALSE;
    private Boolean onMatching = Boolean.FALSE;
    private Boolean active = Boolean.FALSE;
    private Boolean onGoing = Boolean.FALSE;
    private Boolean onArrived = Boolean.FALSE;
    private Request currentRequest = new Request();

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
     * This method return the onConfirm state of the user
     * @return
     *  onConfirm state of the user
     */
    public Boolean getOnConfirm() {
        return onConfirm;
    }

    /**
     * This method set the onConfirm state of user
     * @param onConfirm
     *  onConfirm state of the user
     */
    public void setOnConfirm(Boolean onConfirm) {
        this.onConfirm = onConfirm;
    }

    /**
     * This method set the onMatching state of user
     * @return
     *  onMatching state of user
     */
    public Boolean getOnMatching() {
        return onMatching;
    }

    /**
     * This method set the onMatching state of user
     * @param onMatching
     *  onMatching state of user
     */
    public void setOnMatching(Boolean onMatching) {
        this.onMatching = onMatching;
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
     * This method return thecID of current request
     * @return
     * current request's ID
     */
    public String getRequestID() {
        return currentRequest.getRid();
    }

    /**
     * This method return current request of the user
     * @return
     *  current request of the user
     */
    public Request getCurrentRequest() {
        return currentRequest;
    }

    /**
     * This method set current request of the user
     * @param currentRequest
     *  candidate current request
     */
    public void setCurrentRequest(Request currentRequest) {
        this.currentRequest = currentRequest;
    }

    /**
     * This method convert proxy user state to user state
     * @param proxyUserState
     *  proxy user state to be converted
     */
    public void setState(ProxyUserState proxyUserState) {
        this.currentMode = proxyUserState.getCurrentMode();
        this.onConfirm = proxyUserState.getOnConfirm();
        this.onMatching = proxyUserState.getOnMatching();
        this.active = proxyUserState.getActive();
        this.onGoing = proxyUserState.getOnGoing();
        this.onArrived = proxyUserState.getOnArrived();
        this.currentRequest = proxyUserState.getCurrentRequest();
    }
}
