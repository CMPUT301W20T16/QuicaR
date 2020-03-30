package com.example.quicar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.LocationDataHelper;
import com.example.datahelper.RecordDataHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.datahelper.UserDataHelper;
import com.example.entity.Location;
import com.example.entity.Request;
import com.example.listener.OnGetLocationDataListener;
import com.example.listener.OnGetRecordDataListener;
import com.example.listener.OnGetRequestDataListener;
import com.example.user.User;
import com.example.util.ConnectivityReceiver;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements OnGetRequestDataListener, OnGetRecordDataListener, OnGetLocationDataListener {
    private final String TAG = "MainActivity";
    private OnGetRequestDataListener listener = this;
    private static int SPLASH_TIME_OUT = 0;

    private String requestID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //  database setup
        DatabaseHelper.getInstance().setCurrentMode("rider");
        // initialize data helper
        RequestDataHelper.getInstance();
        RecordDataHelper.getInstance();
        UserDataHelper.getInstance();

//        //  test adding new user in register page
        startActivity(new Intent(getApplicationContext(), LogoActivity.class));
//        finish();

////           test map view
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent homeIntent = new Intent(MainActivity.this, RiderRequestActivity.class);
//                startActivity(homeIntent);
//                finish();
//            }
//        }, SPLASH_TIME_OUT);
//        System.out.println("user name" + DatabaseHelper.getCurrentUserName());

        Button notityButton = findViewById(R.id.setActive);
//        notityButton.setVisibility(View.INVISIBLE);
        notityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User newDriver = new User();
                newDriver.setName("new Driver");
                RequestDataHelper.getInstance().setRequestActive(requestID, newDriver, 666.f, listener);
            }
        });

        Button requestButton = findViewById(R.id.request);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.editText);
                User newUser = new User();
                newUser.setName(input.getText().toString());
                DatabaseHelper.getInstance().setCurrentUser(newUser);
                Request request = new Request(new Location(), "address name for starting point",
                        new Location(), "address name for destination",
                        newUser, new User(),27.0f);
                RequestDataHelper.getInstance().addNewRequest(request, listener);
            }
        });

        Button completeButton = findViewById(R.id.complete);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestDataHelper.getInstance().completeRequest(requestID, 30.0f, 5.0f, listener);
                Request testRequest = new Request();
                User testUser = new User();
                testUser.setName("test");
                testRequest.setDriver(testUser);
                DatabaseHelper.getInstance().getUserState().setCurrentRequest(testRequest);
                LocationDataHelper
                        .getInstance()
                        .updateLocation(
                                "new user testing"
                                , new Location(123.d, 123.d));
            }
        });


    }

    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag == RequestDataHelper.USER_REQ_TAG) {

        } else if (tag == RequestDataHelper.ALL_REQs_TAG) {
            if (requests.size() > 0) {
                //  always check if the return value is valid
                System.out.println("------------ active request obtained -----------");
                for (Request request: requests)
                    System.out.println(request);
            }
            else {
                System.out.println("------------ empty list obtained -----------");
            }
        } else if (tag == RequestDataHelper.SET_ACTIVE_TAG) {
            System.out.println("------------ request is set to active -----------");
            //RequestDataHelper.queryAllOpenRequests( this);
            RequestDataHelper.getInstance().queryUserRequest("new Driver", "driver", this);
            Toast.makeText(MainActivity.this, "rider request updated to active successfully", Toast.LENGTH_SHORT).show();
        } else if (tag == RequestDataHelper.SET_PICKEDUP_TAG) {
            Toast.makeText(MainActivity.this, "rider is picked up successfully", Toast.LENGTH_SHORT).show();
        } else if (tag == RequestDataHelper.CANCEL_REQ_TAG) {
            System.out.println("------------ request is deleted -----------");
            Toast.makeText(MainActivity.this, "rider request deleted successfully", Toast.LENGTH_SHORT).show();
        } else if (tag == RequestDataHelper.COMPLETE_REQ_TAG) {
            System.out.println("------------ request is completed & deleted -----------");
            System.out.println("------------ new record is created -----------");
            Toast.makeText(MainActivity.this, "rider request completed successfully", Toast.LENGTH_SHORT).show();
        } else if (tag == RequestDataHelper.ADD_REQ_TAG) {
            Toast.makeText(MainActivity.this, "rider request added successfully", Toast.LENGTH_SHORT).show();
            requestID = requests.get(0).getRid();
            System.out.println(requestID+"---------requestID");
        }
    }

    /*
    This is part for OnGetRequestDataListener
     */

    @Override
    public void onActiveNotification(Request request) {
        System.out.println("------------- rider request updated to active -----------------");
        Toast.makeText(MainActivity.this, "rider request updated to active by driver",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public  void onPickedUpNotification(Request request) {

    }

    @Override
    public  void onArrivedNotification(Request request) {

    }
    @Override
    public void onCancelNotification() {

    }

    @Override
    public void onCompleteNotification() {
        RecordDataHelper
                .getInstance()
                .queryHistoryLocation(DatabaseHelper.getInstance().getCurrentUserName(), 5,this);
        System.out.println("here ------");
    }



    @Override
    public void onFailure(String errorMessage, String tag) {
        System.out.println("-----------" + errorMessage + "-----------");
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    /*
    This is part for OnGetRecordDataListener
     */

    @Override
    public void onSuccess(ArrayList<Location> history) {
        for (Location loc: history)
            System.out.println(loc.getLat() + " " + loc.getLon() + " --------- history here");
    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("-----------" + errorMessage + "-----------");
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdate(Location location) {
        Toast.makeText(MainActivity.this, location.getLat().toString(), Toast.LENGTH_SHORT).show();
    }

}
