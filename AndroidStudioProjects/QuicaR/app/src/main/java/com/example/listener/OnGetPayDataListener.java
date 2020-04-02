package com.example.listener;

import com.example.entity.PayRecord;

import java.util.ArrayList;

/**
 * This is the interface that act as a listener when interacting with firebase
 * so that it can be notified when pay record data transfer is successful or failed.
 */
public interface OnGetPayDataListener {
    void onSuccess(ArrayList<PayRecord> records);

    void onFailure(String e);
}
