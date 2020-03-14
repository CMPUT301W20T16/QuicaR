package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class DriverPickUpActivity extends DrawRouteBaseActivity implements OnGetRequestDataListener{
    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    Button confirmButton;
    Request currentRequest;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        currentRequest = (Request) intent.getSerializableExtra("current accepted request");

        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_pick_up, frameLayout);



        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_driver_pick_up);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        confirmButton = linearLayout.findViewById(R.id.pick_up);

        RequestDataHelper.getInstance().setOnNotifyListener(this);

//        mRequest = new Request();
        //get data from firebase
        System.out.println("------------------------current user name: " + DatabaseHelper.getInstance().getCurrentUserName());

        RequestDataHelper
                .getInstance()
                .queryUserRequest(DatabaseHelper.getInstance().getCurrentUserName(),
                        "driver", this);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestDataHelper
                        .getInstance()
                        .setRequestPickedUp(mRequest.getRid(),
                                DriverPickUpActivity.this);

                System.out.println("Request id-------------" + mRequest.getRid());
                Intent intent = new Intent(DriverPickUpActivity.this, DriverOnGoingActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * RequestDataHelper methods
     * @param requests
     * @param tag
     */
    // if successfully get all request associated with the specific driver, i.e., current user
    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag == RequestDataHelper.USER_REQ_TAG) {
            if (requests.size() > 0) {
                //  always check if the return value is valid
                System.out.println("------------ all open request obtained -----------");
                for (Request request: requests) {
                    if (request.getAccepted()) {
                        mRequest = request;

                        start_location = mRequest.getStart();
                        end_location = mRequest.getDestination();

                        System.out.println("start location" + start_location.getLat() + start_location.getLon());
                        System.out.println("end location" + end_location.getLat() + end_location.getLon());

                        start = new MarkerOptions().position(new LatLng(start_location.getLat(), start_location.getLon())).title("origin");
                        destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");

                        new FetchURL(this)
                                .execute(getUrl(start.getPosition(), destination.getPosition(), "driving"), "driving");

                    }
                }
            }
            else {
                System.out.println("------------ empty list obtained -----------");
            }
        }

    }

    @Override
    public void onActiveNotification(Request request) {

    }

    @Override
    public void onPickedUpNotification(Request request) {

    }

    @Override
    public  void onArrivedNotification(Request request) {

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
