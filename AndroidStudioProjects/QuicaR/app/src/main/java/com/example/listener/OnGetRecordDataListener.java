package com.example.listener;

import com.example.entity.Location;

import java.util.ArrayList;

/**
 * This is the interface that act as a listener when interacting with firebase
 * so that it can be notified when records data transfer is successful or failed.
 */
public interface OnGetRecordDataListener {

    void onSuccess(ArrayList<Location> history);

    void onFailure(String errorMessage);
}
