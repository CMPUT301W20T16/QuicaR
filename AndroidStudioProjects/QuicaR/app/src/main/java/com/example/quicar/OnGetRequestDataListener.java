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
    //  provide list of active request that is near to the driver location
    //  notify listener that new request is added successfully
    //  notify listener that new request is added successfully
    //  notify listener that request is successfully updated to active
    //  notify listener that request is successfully updated to picked up
    //  notify listener that request is successfully deleted
    //  notify listener that request is completed, request deleted and new record created
    //  notify when a request is set to active (only when the user is in rider mode)
    void onSuccess(ArrayList<Request> requests, String tag);

    void onActiveNotification(Request request);

    void onPickedUpNotification(Request request);

    void onCancelNotification();

    void onCompleteNotification();

    // whenever the query return null object or reading database failed
    void onFailure(String errorMessage, String tag);
}