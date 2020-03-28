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
import com.example.font.Button_SF_Pro_Display_Medium;
import com.example.font.TextViewSFProDisplayLight;
import com.example.font.TextViewSFProDisplayRegular;
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
    private DirectionsResult directionsResult;

    TextViewSFProDisplayRegular riderEmail, riderPhone,riderName, startAddress, endAddress;
    Button_SF_Pro_Display_Medium confirmButton;
    TextViewSFProDisplayRegular callButton, emailButton;
    Request currentRequest = null;

    /**
     * when going to this activity, following is executed automatically
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set up view
        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_on_going, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_driver_on_going);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        //set up firebase connection
        RequestDataHelper.getInstance().setOnNotifyListener(this);
        /**
         * current request cannot be updated at driver end
         */
        currentRequest = DatabaseHelper.getInstance().getUserState().getCurrentRequest();



        //set up textView and buttons
        confirmButton = linearLayout.findViewById(R.id.confirm_button);
        riderEmail = linearLayout.findViewById(R.id.userEmail_textView);
        riderPhone = linearLayout.findViewById(R.id.userPhone_textView);
        riderName = linearLayout.findViewById(R.id.userName_textView);
        callButton = linearLayout.findViewById(R.id.call_rider_button);
        emailButton = linearLayout.findViewById(R.id.email_rider_button);
        startAddress = linearLayout.findViewById(R.id.start_address);
        endAddress = linearLayout.findViewById(R.id.end_address);

        startAddress.setText(currentRequest.getStartAddrName());
        endAddress.setText(currentRequest.getDestAddrName());
        riderEmail.setText(currentRequest.getRider().getAccountInfo().getEmail());
        riderPhone.setText(currentRequest.getRider().getAccountInfo().getPhone());
        riderName.setText(currentRequest.getRider().getName());


        start_location = mRequest.getStart();
        end_location = mRequest.getDestination();


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




        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestDataHelper
                        .getInstance()
                        .setRequestArrived(currentRequest.getRid(),
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.addMarker(start);
//        mMap.addMarker(destination);
//        showAllMarkers();
//        addPolyline(directionsResult,mMap);

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
