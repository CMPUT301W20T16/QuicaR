package com.example.quicar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

import static com.example.quicar.DatabaseHelper.TAG;


public class MainActivity extends AppCompatActivity implements OnGetRequestDataListener {

    private OnGetRequestDataListener listener = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper.setCurrentUserName("testing");
        DatabaseHelper.setCurrentMode("rider");

        //  database test cases
        new DatabaseHelper();
        new RequestDataHelper();
        new RecordDataHelper();
        new UserDataHelper();

        RequestDataHelper.setOnActiveListener(this);

//        User newUser = new User();
//        newUser.setName("testing1");
//        Request request = new Request(new Location(), new Location(), newUser, new User(), 27.0f);
//        Record record = new Record(request, 10.0f, 5.0f);
//        RequestDataHelper.addNewRequest(request, this);
//        RequestDataHelper.queryRiderOpenRequest("testing", this);
//        RequestDataHelper.queryAllOpenRequests(new Location(),this);
//        User newDriver = new User();
//        newDriver.setName("new Driver");
//        RequestDataHelper.setRequestActive("testing1", newDriver, this);
//        RequestDataHelper.cancelRequest("testing", this);
//        RequestDataHelper.queryDriverActiveRequest(newDriver.getName(), this);

        //  test adding new user in register page
//        startActivity(new Intent(getApplicationContext(), Login.class));
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
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                RequestDataHelper.setRequestActive(DatabaseHelper.getCurrentUserName(), newDriver, listener);
                DatabaseHelper.sendNotification("hello");
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
                RequestDataHelper.completeRequest("new Driver", 30.0f, 5.0f, listener);
            }
        });

    }

    @Override
    public void onSuccessRiderOpenRequest(Request request) {
        if (request != null) {
            //  always check if the return value is valid
            System.out.println("---------------" + request.getRider().getName() + "---------------");
        }
    }

    @Override
    public void onSuccessAllOpenRequests(ArrayList<Request> requests) {
        if (requests.size() > 0) {
            //  always check if the return value is valid
            System.out.println("------------ active request obtained -----------");
        }
        else {
            System.out.println("------------ empty list obtained -----------");
        }
    }

    @Override
    public void onSuccessDriverActiveRequest(Request request) {
        if (request != null) {
            //  always check if the return value is valid
            System.out.println("---------------" + request.getDriver().getName() + "---------------");
//            RequestDataHelper.completeRequest("new Driver", 30.0f, 5.0f, this);
        }
    }

    @Override
    public void onSuccessSetActive() {
        System.out.println("------------ request is set to active -----------");
        RequestDataHelper.queryAllOpenRequests(new Location(), this);
        RequestDataHelper.queryDriverActiveRequest("new Driver", this);

    }

    @Override
    public void onSuccessCancel() {
        System.out.println("------------ request is deleted -----------");
    }

    @Override
    public void onSuccessComplete() {
        System.out.println("------------ request is completed & deleted -----------");
        System.out.println("------------ new record is created -----------");
    }

    @Override
    public void onSuccessAddRequest() {
//        Button notityButton = findViewById(R.id.setActive);
//        notityButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActiveNotification(Request request) {
        System.out.println("------------- rider request updated to active -----------------");
    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("-----------" + errorMessage + "-----------");
    }
}


