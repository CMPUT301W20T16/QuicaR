package com.example.quicar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements OnGetRequestDataListener {

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

        User newUser = new User();
        newUser.setName("testing1");
        Request request = new Request(new Location(), new Location(), newUser, new User(), 27.0f);
        Record record = new Record(request, 10.0f, 5.0f);
        RequestDataHelper.addNewRequest(request, this);
//        RequestDataHelper.queryRiderOpenRequest("testing", this);
//        RequestDataHelper.queryAllOpenRequests(new Location(),this);
        User newDriver = new User();
        newDriver.setName("new Driver");
//        RequestDataHelper.setRequestActive("testing", newDriver, this);
//        RequestDataHelper.cancelRequest("testing", this);
        RequestDataHelper.queryDriverActiveRequest(newDriver.getName(), this);

        //  test adding new user in register page
        startActivity(new Intent(getApplicationContext(), Login.class));
        System.out.println("user name" + DatabaseHelper.getCurrentUserName());


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
            RequestDataHelper.completeRequest("new Driver", 30.0f, 5.0f, this);
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
