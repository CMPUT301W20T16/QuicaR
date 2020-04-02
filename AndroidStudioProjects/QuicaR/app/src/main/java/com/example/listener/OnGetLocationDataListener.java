package com.example.listener;

import com.example.entity.Location;

/**
 * This is the interface that act as a listener when interacting with firebase
 * so that it can be notified when user's location data transfer is successful or failed.
 */
public interface OnGetLocationDataListener {
    void onUpdate(Location location);
}
