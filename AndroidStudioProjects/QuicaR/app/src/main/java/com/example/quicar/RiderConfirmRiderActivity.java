package com.example.quicar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.datahelper.UserDataHelper;
import com.example.datahelper.UserStateDataHelper;
import com.example.entity.Request;
import com.example.entity.Location;
import com.example.font.TextViewSFProDisplayMedium;
import com.example.font.TextViewSFProDisplayRegular;
import com.example.listener.OnGetRequestDataListener;
import com.example.datahelper.RequestDataHelper;
import com.example.datahelper.DatabaseHelper;
import com.example.user.User;




import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;




public class RiderConfirmRiderActivity extends BaseActivity implements OnGetRequestDataListener, TaskLoadedCallback {


    private OnGetRequestDataListener listener = this;

    private LinearLayout linearLayout;
    private BottomSheetBehavior bottomSheetBehavior;


    private Button confirmButton, cancelButton;
    private Request currentRequest = null;
    private DirectionsResult directionsResult;



    private TextViewSFProDisplayRegular view_distance, view_time, view_fare, view_start, view_end;
    private String travelTime, travelDistance;
    private Float travelFare;

    private Location start_location, end_location;
    private MarkerOptions start, destination;



    /**
     * after rider chosen start and end, this activity shows up
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_confirm_ride, frameLayout);


        //set bottom sheet
        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_order_detail);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        // set Buttons
        confirmButton = linearLayout.findViewById(R.id.confirm_button);
        cancelButton = linearLayout.findViewById(R.id.cancel_button);
        //set text views
        view_distance = linearLayout.findViewById(R.id.view_distance);
        view_time = linearLayout.findViewById(R.id.view_time);
        view_fare = linearLayout.findViewById(R.id.view_fare);
        view_start = linearLayout.findViewById(R.id.start_address);
        view_end = linearLayout.findViewById(R.id.end_address);


        //get data from intent, i.e., current address
//        Intent intent = getIntent();
//        start_location = (Location) intent.getSerializableExtra("start location");
//        end_location = (Location) intent.getSerializableExtra("end location");

        start_location = DatabaseHelper.getInstance().getUserState().getCurrentRequest().getStart();
        end_location = DatabaseHelper.getInstance().getUserState().getCurrentRequest().getDestination();

        start = new MarkerOptions().position(new LatLng(start_location.getLat(), start_location.getLon())).title("origin");
        destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");

        //add pins to map
        markerOptionsList.add(start);
        markerOptionsList.add(destination);

        new FetchURL(RiderConfirmRiderActivity.this)
                .execute(getUrl(start.getPosition(), destination.getPosition(), "driving"), "driving");

        //update start and end address on bottom sheet
        view_start.setText(start_location.getName());
        view_end.setText(end_location.getName());


        DateTime now = new DateTime();
        String start_address = start_location.getAddressName();
        String end_address = end_location.getAddressName();
        try {

            directionsResult = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(start_address)
                    .destination(end_address).departureTime(now)
                    .await();


        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /**
         * estimate cost function
         */
        // if user selected confirm button
        // add new request to database
        /**
         * when click confirm button, following will be executed
         */
        confirmButton.setOnClickListener(v -> {

//            /**
//             *   instantiate a new User class for current user
//              */
//            User newUser = new User();
//            newUser.setName(DatabaseHelper.getInstance().getCurrentUserName());

            //parse travel time, now is "14 min"
            String[] estimateTime = travelTime.split("\\s+");


            //add new request to the data base
            Request request = new Request(start_location, start_location.getAddressName(),
                    end_location, end_location.getAddressName(),
                    DatabaseHelper.getInstance().getCurrentUser(), new User(), travelFare, Integer.parseInt(estimateTime[0]));

            currentRequest = request;


            RequestDataHelper.getInstance().addNewRequest(request, listener);
            /* added for user state */
            DatabaseHelper.getInstance().getUserState().setOnMatching(true);
            UserStateDataHelper.getInstance().recordState();


//            Intent intent1 = new Intent(RiderConfirmRiderActivity.this, RiderMatchingActivity.class);
////            Intent intent1 = new Intent(RiderConfirmRiderActivity.this, RiderWaitingRideActivity.class);
//
////            intent1.putExtra("current request", currentRequest);
//            startActivity(intent1);
//            finish();

        });



        // if user selected cancel button
        // return to previous activity RiderSelectLocation
        cancelButton.setOnClickListener(v -> {
            /* added for user state */
            DatabaseHelper.getInstance().getUserState().setOnConfirm(false);
            DatabaseHelper.getInstance().getUserState().getCurrentRequest().setStart(null);
            DatabaseHelper.getInstance().getUserState().getCurrentRequest().setDestination(null);
            UserStateDataHelper.getInstance().recordState();

            finish();
        });


    }

    /**
     * Draw route methods
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        mMap.setBuildingsEnabled(true);
        mMap.setTrafficEnabled(true);

        mMap.addMarker(start);
        mMap.addMarker(destination);
        showAllMarkers();

        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);

        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);

        try {
            if (directionsResult != null) {
                if (directionsResult.routes != null) {
                    //addPolyline(directionsResult, mMap);

                    travelTime = directionsResult.routes[0].legs[0].duration.humanReadable;
                    travelDistance = directionsResult.routes[0].legs[0].distance.humanReadable;
                    if (directionsResult.routes[0].legs[0].distance.inMeters >= 200000) {
                        Toast.makeText(RiderConfirmRiderActivity.this, "cannot request for more than 200 km!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RiderConfirmRiderActivity.this, RiderSelectLocationActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    travelFare = (float) estimateFare(directionsResult.routes[0].legs[0].distance.inMeters);

                    if (travelFare > DatabaseHelper.getInstance().getCurrentUser().getAccountInfo().getWallet().getBalance()){
                        Toast.makeText(RiderConfirmRiderActivity.this, "current balance not enough for this trip, please recharge", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RiderConfirmRiderActivity.this, RiderSelectLocationActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    view_distance.setText(travelDistance);
                    view_time.setText(travelTime);
                    view_fare.setText("$ " + travelFare);


                }
            }

        }catch (Exception e){

            Toast.makeText(RiderConfirmRiderActivity.this, "no valid route found", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RiderConfirmRiderActivity.this, RiderSelectLocationActivity.class);
            startActivity(intent);

        }

    }





    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();


        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);


    }



    protected double estimateFare (long distance){
        double fare;

        if(distance<1000){
            fare = 5.0;

        }
        else if (distance <= 5000 && distance >= 1000){
            fare = 5 + (distance / 1000)*3;
        }
        else{
            fare = (distance/1000)*2.5;
        }


        return fare;
    }



    /***2020.03.20 new part Yuxin for calculating distance------------------------------------------------------------------
     *
     */



    /** end new part
     -----------------------------------------------------------------------------
     */






    /**
     * overide helper function for drawing a route
     * @param values
     */




    /**
     * automatically executed
     * @param requests
     * @param tag
     */
    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag == RequestDataHelper.ADD_REQ_TAG) {
            Toast.makeText(RiderConfirmRiderActivity.this, "rider request added successfully", Toast.LENGTH_SHORT).show();

            Intent intent1 = new Intent(RiderConfirmRiderActivity.this, RiderMatchingActivity.class);
//            Intent intent1 = new Intent(RiderConfirmRiderActivity.this, RiderWaitingRideActivity.class);

//            intent1.putExtra("current request", currentRequest);
            startActivity(intent1);
            finish();
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
        System.out.println("-----------" + errorMessage + "-----------");
        Toast.makeText(RiderConfirmRiderActivity.this, errorMessage, Toast.LENGTH_SHORT).show();


    }


}
