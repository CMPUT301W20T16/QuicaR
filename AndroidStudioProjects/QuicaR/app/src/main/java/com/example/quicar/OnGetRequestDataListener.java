package com.example.quicar;


// Joseph Varghese
// https://stackoverflow.com/questions/30659569/wait-until-firebase-retrieves-data

import java.util.ArrayList;

/**
 * This is the interface that act as a listener when interacting with database helper
 * so that it can be notified when requests data transfer is successful or failed.
 */
public interface OnGetRequestDataListener {
    //  provide request that is open by the user
    void onSuccessRiderOpenRequest(Request request);

    //  provide list of active request that is near to the driver location
    void onSuccessAllOpenRequests(ArrayList<Request> requests);

    //  provide request that the driver accepted
    void onSuccessDriverActiveRequest(Request request);

    // notify listener that new request is added successfully
    void onSuccessAddRequest();

    //  notify listener that request is successfully updated to active
    void onSuccessSetActive();

    //  notify listener that request is successfully deleted
    void onSuccessCancel();

    //  notify listener that request is completed, request deleted and new record created
    void onSuccessComplete();

    //  notify when a request is set to active (only when the user is in rider mode)
    void onActiveNotification(Request request);

    // whenever the query return null object or reading database failed
    void onFailure(String errorMessage);
}
