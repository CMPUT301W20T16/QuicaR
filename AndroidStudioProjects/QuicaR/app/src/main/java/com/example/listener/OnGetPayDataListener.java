package com.example.listener;

import com.example.entity.PayRecord;

import java.util.ArrayList;

public interface OnGetPayDataListener {
    void onSuccess(ArrayList<PayRecord> records);

    void onFailure(String e);
}
