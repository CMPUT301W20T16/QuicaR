package com.example.quicar;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DriverOnGoingActivity extends DrawRouteBaseActivity implements OnGetRequestDataListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {

    }

    @Override
    public void onActiveNotification(Request request) {

    }

    @Override
    public void onPickedUpNotification(Request request) {

    }

    @Override
    public void onCancelNotification() {

    }

    @Override
    public void onCompleteNotification() {

    }

    @Override
    public void onFailure(String errorMessage, String tag) {

    }
}
