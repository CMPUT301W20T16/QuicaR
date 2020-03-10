package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class RiderMatchingActivity extends BaseActivity implements OnGetRequestDataListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_matching, frameLayout);

    }


    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
    }

    @Override
    public void onActiveNotification(Request request) {
        System.out.println("------------- rider request updated to active -----------------");
        Toast.makeText(RiderMatchingActivity.this, "rider request updated to active by driver", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RiderWaitingRideActivity.class);
        startActivity(intent);

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
