package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.entity.Request;
import com.example.listener.OnGetRequestDataListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.DirectionsApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.io.IOException;
import java.util.ArrayList;

public class DriverOnGoingActivity extends DrawRouteBaseActivity implements OnGetRequestDataListener {

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;
    private Request currentRequest = null;
    private DirectionsResult directionsResult;

    Button confirmButton;

    /**
     * when going to this activity, following is executed automatically
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        currentRequest = (Request) intent.getSerializableExtra("current accepted request");


        start_location = currentRequest.getStart();
        end_location = currentRequest.getDestination();


        start = new MarkerOptions().position(new LatLng(start_location.getLat(), start_location.getLon())).title("origin");
        destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");

        markerOptionsList.add(start);
        markerOptionsList.add(destination);


        DateTime now = new DateTime();
        String start_address = start_location.getAddressName();
        String end_address = end_location.getAddressName();
        try {
            //GeoApiContext geoApiContext = getGeoContext();
            directionsResult = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(start_address)
                    .destination(end_address).departureTime(Instant.now())
                    .await();





        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_on_going, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_driver_on_going);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        confirmButton = linearLayout.findViewById(R.id.confirm_button);

        RequestDataHelper.getInstance().setOnNotifyListener(this);

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
                                DriverOnGoingActivity.this);
                Intent intent = new Intent(DriverOnGoingActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * when add new request, following will be executed automatically
     * @param requests
     * @param tag
     */
    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
//        if (tag == RequestDataHelper.USER_REQ_TAG) {
//            if (requests.size() > 0) {
//                //  always check if the return value is valid
//                System.out.println("------------ all open request obtained -----------");
//                for (Request request: requests) {
//                    if (request.getAccepted()) {
//                        mRequest = request;
//
//                        start_location = mRequest.getStart();
//                        end_location = mRequest.getDestination();
//
//                        System.out.println("start location" + start_location.getLat() + start_location.getLon());
//                        System.out.println("end location" + end_location.getLat() + end_location.getLon());
//
//                        start = new MarkerOptions().position(new LatLng(start_location.getLat(), start_location.getLon())).title("origin");
//                        destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");
//
//                        new FetchURL(this)
//                                .execute(getUrl(start.getPosition(), destination.getPosition(), "driving"), "driving");
//
//
//                    }
//                }
//            }
//            else {
//                System.out.println("------------ empty list obtained -----------");
//            }
//        } else if (tag == RequestDataHelper.SET_ARRIVED_TAG) {
//            System.out.println("-------------- arrived at destination ------------");
//            Toast.makeText(DriverOnGoingActivity.this, "you have arrived at the destination", Toast.LENGTH_SHORT).show();
//
//        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(start);
        mMap.addMarker(destination);
        showAllMarkers();
        addPolyline(directionsResult,mMap);

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
