package com.example.quicar;

/**
 * This is the interface that act as a listener when interacting with database helper
 * so that it can be notified when user accounts data transfer is successful or failed.
 */
public interface OnGetUserDataListener {

    //  notify listener that new user account is added successfully
    void onSuccessAddUser();

    //  notify listener that user profile is updated successfully
    void onSuccessUpdateUser();

    // whenever the query return null object or reading database failed
    void onFailure(String errorMessage);
}
