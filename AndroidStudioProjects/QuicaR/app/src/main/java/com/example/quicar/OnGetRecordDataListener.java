package com.example.quicar;

import java.util.ArrayList;

public interface OnGetRecordDataListener {

    void onSuccess(ArrayList<Location> history);

    void onFailure(String errorMessage);
}