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
//    private MarkerOptions start, destination;
//    private Polyline currentPolyline;
//    List<MarkerOptions> markerOptionsList = new ArrayList<>();


    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    Button confirmButton;
//    Request mRequest;
//    Location start_location, end_location;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_pick_up, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_driver_pick_up);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        confirmButton = linearLayout.findViewById(R.id.pick_up);

        RequestDataHelper.setOnNotifyListener(this);

//        mRequest = new Request();
        //get data from firebase
        RequestDataHelper.queryUserRequest(DatabaseHelper.getCurrentUserName(), "driver", this);

        if ( mRequest != null) {
//            System.out.println(mRequest);
            start_location = mRequest.getStart();
            end_location = mRequest.getDestination();

            System.out.println("start location" + start_location.getLat() + start_location.getLon());
            System.out.println("end location" + end_location.getLat() + end_location.getLon());

            start = new MarkerOptions().position(new LatLng(start_location.getLat(), start_location.getLon())).title("origin");
            destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");

            new FetchURL(this)
                    .execute(getUrl(start.getPosition(), destination.getPosition(), "driving"), "driving");

        } else {
            System.out.println("--------request is null--------");
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestDataHelper.setRequestPickedUp(mRequest.getRid(), mRequest.getDriver(), DriverPickUpActivity.this);
                Intent intent = new Intent(DriverPickUpActivity.this, DriverOnGoingActivity.class);
                startActivity(intent);
            }
        });

    }

//    /**
//     * Draw route methods
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        if (start != null && destination != null) {
//            mMap.addMarker(start);
//            mMap.addMarker(destination);
//            showAllMarkers();
//
//        }
//    }
//
//    public void showAllMarkers() {
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//        for (MarkerOptions m : markerOptionsList) {
//            builder.include(m.getPosition());
//
//        }
//        LatLngBounds bounds = builder.build();
//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = getResources().getDisplayMetrics().heightPixels;
//        int padding = (int) (width * 0.30);
//
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
//        mMap.animateCamera(cu);
//
//    }
//
//
//
//    public String getUrl(LatLng origin, LatLng dest, String directionMode) {
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//        String mode = "mode=" + directionMode;
//        String parameter = str_origin + "&" + str_dest + "&" + mode;
//        String format = "json";
//        String url = "https://maps.googleapis.com/maps/api/directions/" + format + "?"
//                + parameter + "&key=AIzaSyC2x1BCzgthK4_jfvqjmn6_uyscCiKSc34";
//
//
//        return url;
//
//    }
//
//
//    @Override
//    public void onTaskDone(Object... values) {
//        if (currentPolyline != null)
//            currentPolyline.remove();
//
//        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
//    }


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
//        System.out.println("------------- rider request updated to active -----------------");
//        mRequest = request;
//        Toast.makeText(this, "rider request updated to active by driver", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPickedUpNotification(Request request) {

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
