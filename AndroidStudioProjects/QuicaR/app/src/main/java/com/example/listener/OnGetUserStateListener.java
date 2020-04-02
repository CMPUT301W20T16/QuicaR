package com.example.listener;

/**
 * This is the interface that act as a listener when interacting with firebase
 * so that it can be notified when user state data transfer is successful or failed.
 */
public interface OnGetUserStateListener {
    void onStateUpdated();
}
