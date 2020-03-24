package com.example.listener;

import com.example.user.User;

/**
 * This is the interface that act as a listener when interacting with firebase
 * so that it can be notified when user accounts data transfer is successful or failed.
 */
public interface OnGetUserDataListener {

    //  notify listener that new user account is added successfully
    //  notify listener that user profile is updated successfully
    //  notify listener that user is obtained successfully
    void onSuccess(User user, String tag);

    void onUpdateNotification(User user);

    // whenever the query return null object or reading database failed
    void onFailure(String errorMessage);
}