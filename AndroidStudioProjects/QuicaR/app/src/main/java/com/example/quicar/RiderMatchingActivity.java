package com.example.quicar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class RiderMatchingActivity extends BaseActivity implements OnGetRequestDataListener {

    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get activated request from firebase
        RequestDataHelper.getInstance().setOnNotifyListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Waiting for macthing...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {

    }

    @Override
    public void onActiveNotification(Request request) {
        System.out.println("------------- rider request updated to active -----------------");
        DatabaseHelper.getInstance().sendPopUpNotification("Notification test", "Ride is being accepted");

        // dismiss progress dialog if rider has been successfully matched to a driver
        mProgressDialog.dismiss();
        Toast.makeText(RiderMatchingActivity.this, "rider request updated to active by driver", Toast.LENGTH_SHORT).show();

        // go to the new activity
        Intent intent = new Intent(RiderMatchingActivity.this, RiderWaitingRideActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPickedUpNotification(Request request) {

    }

    @Override
    public void onArrivedNotification(Request request) {

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
