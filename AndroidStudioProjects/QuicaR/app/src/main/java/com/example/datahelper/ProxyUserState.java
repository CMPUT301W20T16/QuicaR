package com.example.datahelper;

import com.example.entity.Request;
import com.example.user.User;

public class ProxyUserState {
    private String currentMode;
    private Boolean onConfirm = Boolean.FALSE;
    private Boolean onMatching = Boolean.FALSE;
    private Boolean active = Boolean.FALSE;
    private Boolean onGoing = Boolean.FALSE;
    private Boolean onArrived = Boolean.FALSE;
    private Request currentRequest;

    /**
     * This is the empty constructor for UserState
     */
    public ProxyUserState() {}


    /**
     * Proxy User state constructor. An object that delegate user state class. It is used to retain
     * the previous state of user activity when exit the app and re-enter.
     * @param userState
     *  user state to be convert to proxy user state
     */
    public ProxyUserState(UserState userState) {
        this.currentMode = userState.getCurrentMode();
        this.onConfirm = userState.getOnConfirm();
        this.onMatching = userState.getOnMatching();
        this.active = userState.getActive();
        this.onGoing = userState.getOnGoing();
        this.onArrived = userState.getOnArrived();
        this.currentRequest = userState.getCurrentRequest();
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
     * This method return the onConfirm state
     * @return
     *  onConfirm state
     */
    public Boolean getOnConfirm() {
        return onConfirm;
    }

    /**
     * This method set the onConfirm state
     * @param onConfirm
     *  candidate onConfirm state
     */
    public void setOnConfirm(Boolean onConfirm) {
        this.onConfirm = onConfirm;
    }

    /**
     * This method return the onMatching state
     * @return
     *  onMatching state
     */
    public Boolean getOnMatching() {
        return onMatching;
    }

    /**
     * This method set the onMatching state
     * @param onMatching
     *  candidate onMatching state
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
     * This method return current request of the user
     * @return
     *  current request
     */
    public Request getCurrentRequest() {
        return currentRequest;
    }

    /**
     * This method set current reuqest of user
     * @param currentRequest
     *  candidate current request
     */
    public void setCurrentRequest(Request currentRequest) {
        this.currentRequest = currentRequest;
    }
}
