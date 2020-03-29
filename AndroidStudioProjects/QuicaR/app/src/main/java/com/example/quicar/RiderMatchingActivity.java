package com.example.quicar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.datahelper.UserStateDataHelper;
import com.example.entity.Request;
import com.example.font.Button_SF_Pro_Display_Medium;
import com.example.listener.OnGetRequestDataListener;

import java.util.ArrayList;

public class RiderMatchingActivity extends BaseActivity implements OnGetRequestDataListener {

    ProgressDialog mProgressDialog;

    Request currentRequest = null;
    Button_SF_Pro_Display_Medium cancelButton;

    /**
     * when user confirm a request then goes to matching activity
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_matching, frameLayout);

        // get activated request from firebase
        RequestDataHelper.getInstance().setOnNotifyListener(this);

        //get current request from databse
        currentRequest = DatabaseHelper.getInstance().getUserState().getCurrentRequest();

//        //get request intent from riderconfirm activity
//        Intent intent = getIntent();
//        currentRequest = (Request) intent.getSerializableExtra("current request");

        cancelButton = findViewById(R.id.cancel_button);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Waiting for matching...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRequest != null) {
                    System.out.println("currentrequest--------:"+ currentRequest);
                    RequestDataHelper
                            .getInstance()
                            .cancelRequest(currentRequest.getRid(), RiderMatchingActivity.this);
                } else {
                    System.out.println("Unable to retrieve current Request!!!!!!!!!!!!!!!!!!!!!!");
                }
            }
        });
    }

    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {

        if (tag == RequestDataHelper.CANCEL_REQ_TAG) {
            /* added for user state */
            DatabaseHelper.getInstance().getUserState().setOnMatching(true);
            UserStateDataHelper.getInstance().recordState();

            // TODO go back to rider request activity here
        }
    }

    /**
     * over ride to fit unique notification process
     * @param request
     */

    @Override
    public void onActiveNotification(Request request) {
        System.out.println("------------- rider request updated to active -----------------");

        // dismiss progress dialog if rider has been successfully matched to a driver
        mProgressDialog.dismiss();
        Toast.makeText(RiderMatchingActivity.this, "rider request updated to active by driver", Toast.LENGTH_SHORT).show();

        // go to the new activity
        Intent intent = new Intent(RiderMatchingActivity.this, RiderWaitingRideActivity.class);
        intent.putExtra("current request", currentRequest);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPickedUpNotification(Request request) {

    }

    @Override
    public void onArrivedNotification(Request request) {
        Intent intent = new Intent(RiderMatchingActivity.this,RiderOnGoingRequestActivity.class);
        startActivity(intent);

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
