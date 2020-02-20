package com.example.quicar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements OnGetDataListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DatabaseHelper();
        Request request = new Request(new Location(), new Location(), new User(), new User());
        Record record = new Record(request, 10.0f, 5.0f);
        //System.out.println(record.getDateTime());
        DatabaseHelper.addRequest(request);
        //DatabaseHelper.addRecord(record);
        DatabaseHelper.queryRiderOpenRequest("testing", this);
        DatabaseHelper.queryAllOpenRequests(new Location(),this);
        User newDriver = new User();
        newDriver.setName("new Driver");
        DatabaseHelper.setRequestActive("testing", newDriver, this);
        //DatabaseHelper.cancelRequest("testing", this);
        DatabaseHelper.queryDriverActiveRequest(newDriver.getName(), this);
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
        }
    }

    @Override
    public void onSuccessSetActive() {
        System.out.println("------------ request is set to active -----------");
        DatabaseHelper.queryAllOpenRequests(new Location(), this);
        DatabaseHelper.queryDriverActiveRequest("new Driver", this);
    }

    @Override
    public void onSuccessDelete() {
        System.out.println("------------ request is deleted -----------");
    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("-----------" + errorMessage + "-----------");

    }
}
