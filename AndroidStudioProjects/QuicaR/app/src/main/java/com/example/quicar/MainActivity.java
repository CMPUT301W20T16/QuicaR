package com.example.quicar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import static com.example.quicar.DatabaseHelper.TAG;


public class MainActivity extends AppCompatActivity implements OnGetRequestDataListener, OnGetRecordDataListener {

    private OnGetRequestDataListener listener = this;
    private static int SPLASH_TIME_OUT = 0;

    private String requestID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DatabaseHelper.setCurrentUser(new User());
//        DatabaseHelper.setCurrentUserName("Name");

        //  database setup
        DatabaseHelper.setCurrentMode("rider");
        DatabaseHelper.setOldServerKey(getString(R.string.OLD_SERVER_KEY));
        new DatabaseHelper();
        new RequestDataHelper();
        new RecordDataHelper();
        new UserDataHelper();

        //RequestDataHelper.setOnNotifyListener(this);
        //FirebaseMessaging.getInstance().setAutoInitEnabled(true);



        // Get token
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        DatabaseHelper.setToken(token);
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END retrieve_current_token]

//        //  test adding new user in register page
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
//
          // test map view
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent homeIntent = new Intent(MainActivity.this, RiderRequestActivity.class);
//                startActivity(homeIntent);
//                finish();
//            }
//        }, SPLASH_TIME_OUT);
//        System.out.println("user name" + DatabaseHelper.getCurrentUserName());


        Button logTokenButton = findViewById(R.id.logTokenButton);
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                // [START retrieve_current_token]
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                DatabaseHelper.setToken(token);
                                // Log and toast
                                String msg = getString(R.string.msg_token_fmt, token);
                                Log.d(TAG, msg);
                                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END retrieve_current_token]
            }
        });


        Button notityButton = findViewById(R.id.setActive);
//        notityButton.setVisibility(View.INVISIBLE);
        notityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User newDriver = new User();
                newDriver.setName("new Driver");
                RequestDataHelper.setRequestActive(requestID, newDriver, 666.f, listener);
            }
        });

        Button requestButton = findViewById(R.id.request);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.editText);
                DatabaseHelper.setCurrentUserName(input.getText().toString());
                User newUser = new User();
                newUser.setName(DatabaseHelper.getCurrentUserName());
                Request request = new Request(new Location(), new Location(), newUser, new User(), 27.0f);
                RequestDataHelper.addNewRequest(request, listener);
            }
        });

        Button completeButton = findViewById(R.id.complete);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestDataHelper.completeRequest(requestID, 30.0f, 5.0f, listener);
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
            RequestDataHelper.queryUserRequest("new Driver", "driver", this);
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
        DatabaseHelper.sendPopUpNotification("Notification test", "hello");
        Toast.makeText(MainActivity.this, "rider request updated to active by driver", Toast.LENGTH_SHORT).show();
    }

    @Override
    public  void onPickedUpNotification(Request request) {

    }

    @Override
    public void onCancelNotification() {

    }

    @Override
    public void onCompleteNotification() {
        RecordDataHelper.queryHistoryLocation(DatabaseHelper.getCurrentUserName(), null,this);
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
}
