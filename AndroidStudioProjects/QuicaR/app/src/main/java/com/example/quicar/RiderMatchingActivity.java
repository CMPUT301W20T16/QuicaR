package com.example.quicar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.arsy.maps_library.MapRadar;
import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.datahelper.UserStateDataHelper;
import com.example.entity.Request;
import com.example.font.Button_SF_Pro_Display_Medium;
import com.example.listener.OnGetRequestDataListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.ArrayList;


public class RiderMatchingActivity extends BaseActivity implements OnGetRequestDataListener {

    ProgressDialog mProgressDialog;

    Request currentRequest = null;
    Button_SF_Pro_Display_Medium cancelButton;

    private double radius = 5000;

    /**
     * when user confirm a request then goes to matching activity
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_matching, frameLayout);

        // get data from firebase
        RequestDataHelper.getInstance().setOnNotifyListener(this);
        currentRequest = DatabaseHelper.getInstance().getUserState().getCurrentRequest();

        cancelButton = findViewById(R.id.cancel_button);
//        dotsLoaderView = findViewById(R.id.dots_loader_view);

        // to show loading
//        dotsLoaderView.show();

//        mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setMessage("Waiting for matching...");
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRequest != null) {
                    System.out.println("currentrequest--------:"+ currentRequest);
                    RequestDataHelper
                            .getInstance()
                            .cancelRequest(currentRequest.getRid(), RiderMatchingActivity.this);
                } else {
                    System.out.println("Unable to retrieve current Request!!!!!!!!!!!!!!!!!!!!!!");
                }
            }
        });
    }

    /**
     * set map with larger radar
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //set map style
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

            if (!success) {
                System.out.println("-------------Style parsing failed");
            } else {
                System.out.println("------------Style success");
            }
        } catch(Resources.NotFoundException e) {
            System.out.println("------------Can;t find style");
        }


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }




        //set up radar pin
        mapRadar = new MapRadar(mMap, new LatLng(0, 0 ), this);
        mapRadar.withDistance(5000);
        mapRadar.withClockwiseAnticlockwiseDuration(2);
        mapRadar.withOuterCircleFillColor(Color.parseColor("#12000000"));
        mapRadar.withOuterCircleStrokeColor(Color.parseColor("#2e8b57"));
        mapRadar.withRadarColors(Color.parseColor("#00000000"), Color.parseColor("#ff000000"));  //starts from transparent to fuly black
        mapRadar.withRadarColors(Color.parseColor("#00fccd29"), Color.parseColor("#fffccd29"));  //starts from transparent to fuly black
        mapRadar.withOuterCircleStrokewidth(7);
        mapRadar.withRadarSpeed(5);
        mapRadar.withOuterCircleTransparency(0.5f);
        mapRadar.withRadarTransparency(0.5f);
        mapRadar.startRadarAnimation();      //in onMapReadyCallBack

        mapRadar.withClockWiseAnticlockwise(true);

        circleOptions = new CircleOptions()
                .center(new LatLng(0,0))
                .radius(radius)
                .strokeColor(Color.GREEN)
                .strokeWidth(0f)
                .fillColor(Color.parseColor("#802e8b57"));

        mapCircle = mMap.addCircle(circleOptions);

        //move map camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));





    }

    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag.equals(RequestDataHelper.CANCEL_REQ_TAG)) {

//            RequestDataHelper
//                    .getInstance()
//                    .queryUserRequest(DatabaseHelper.getInstance().getCurrentUserName(),
//                            "rider", this);
            /* added for user state */
            DatabaseHelper.getInstance().getUserState().setOnMatching(true);
            UserStateDataHelper.getInstance().recordState();

            Intent intent = new Intent(RiderMatchingActivity.this, RiderRequestActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * over ride to fit unique notification process
     * @param request
     */

    @Override
    public void onActiveNotification(Request request) {
        System.out.println("------------- rider request updated to active -----------------");

        // dismiss progress dialog if rider has been successfully matched to a driver
//        mProgressDialog.dismiss();
        Toast.makeText(RiderMatchingActivity.this, "rider request updated to active by driver", Toast.LENGTH_SHORT).show();


        // to hide loading
//        dotsLoaderView.hide();

        // go to the new activity
        Intent intent = new Intent(RiderMatchingActivity.this, RiderWaitingRideActivity.class);
        intent.putExtra("current request", currentRequest);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPickedUpNotification(Request request) {

    }

    @Override
    public void onArrivedNotification(Request request) {
//        Intent intent = new Intent(RiderMatchingActivity.this,RiderOnGoingRequestActivity.class);
//        startActivity(intent);

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
