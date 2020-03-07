package com.example.quicar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class RiderConfirmRiderActivity extends BaseActivity implements OnGetRequestDataListener, TaskLoadedCallback {

    private OnGetRequestDataListener listener = this;

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;


    Button confirmButton;
    Button cancelButton;

    Location start_location, end_location;


    private MarkerOptions start, destination;
    private Polyline currentPolyline;
    List<MarkerOptions> markerOptionsList = new ArrayList<>();


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



        //get data from intent, i.e., current address
        Intent intent = getIntent();
        start_location = (Location) intent.getSerializableExtra("start location");
        end_location = (Location) intent.getSerializableExtra("end location");

        start = new MarkerOptions().position(new LatLng(start_location.getLat(), start_location.getLon())).title("origin");
        destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");

//        start = new MarkerOptions().position(new LatLng(27.658143,85.3199503)).title("origin");
//        destination = new MarkerOptions().position(new LatLng(53.5187379,-113.50291)).title("destination");
        markerOptionsList.add(start);
        markerOptionsList.add(destination);

//        System.out.println("end location:" + end_location.getLat());
//        System.out.println(end_location.getLon());
//        System.out.println("start location:" + start_location.getLat());
//        System.out.println(start_location.getLon());


        new FetchURL(RiderConfirmRiderActivity.this)
                .execute(getUrl(start.getPosition(), destination.getPosition(), "driving"), "driving");

        /**
         * estimate cost function
         */
        // if user selected confirm button
        // add new request to database
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 *   instantiate a new User class for current user
                  */
                User newUser = new User();
                newUser.setName(DatabaseHelper.getCurrentUserName());

                /**
                 *    new request's cost is hard coded for now
                  */
                Request request = new Request(start_location, end_location, newUser, new User(), 20.0f);

                RequestDataHelper.addNewRequest(request, listener);

//                RequestDataHelper.setRequestActive(DatabaseHelper.getCurrentUserName(), new User(), 666.f, listener);

                Intent intent = new Intent(RiderConfirmRiderActivity.this, RiderWaitingRideActivity.class);
                startActivity(intent);

            }
        });


        // if user selected cancel button
        // return to previous activity RiderSelectLocation
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(start);
        mMap.addMarker(destination);
        showAllMarkers();
    }

    private void showAllMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (MarkerOptions m : markerOptionsList) {
            builder.include(m.getPosition());

        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameter = str_origin + "&" + str_dest + "&" + mode;
        String format = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + format + "?"
                + parameter + "&key=AIzaSyC2x1BCzgthK4_jfvqjmn6_uyscCiKSc34";


        return url;

    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();

        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);


    }

    @Override
    public void onSuccess(Request request, ArrayList<Request> requests, String tag) {
        if (tag == RequestDataHelper.ADD_REQ_TAG) {
            Toast.makeText(RiderConfirmRiderActivity.this, "rider request added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActiveNotification(Request request) {
        System.out.println("------------- rider request updated to active -----------------");
        Toast.makeText(RiderConfirmRiderActivity.this, "rider request updated to active by driver", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPickedUpNotification(Request request) {

    }

    @Override
    public void onCancelNotification() {

    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("-----------" + errorMessage + "-----------");
        Toast.makeText(RiderConfirmRiderActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

    }
}
