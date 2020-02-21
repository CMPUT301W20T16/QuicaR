package com.example.quicar;


// Joseph Varghese
// https://stackoverflow.com/questions/30659569/wait-until-firebase-retrieves-data

import java.util.ArrayList;

public interface OnGetDataListener {
    //  provide request that is open by the user
    void onSuccessRiderOpenRequest(Request request);

    //  provide list of active request that is near to the driver location
    void onSuccessAllOpenRequests(ArrayList<Request> requests);

    //  provide request that the driver accepted
    void onSuccessDriverActiveRequest(Request request);

    //  notify listener that request is successfully updated to active
    void onSuccessSetActive();

    //  notify listener that request is successfully deleted
    void onSuccessDelete();

    //  notify listener that request is completed, request deleted and new record created
    void onSuccessComplete();

    // whenever the query return null object or reading database failed
    void onFailure(String errorMessage);
}
