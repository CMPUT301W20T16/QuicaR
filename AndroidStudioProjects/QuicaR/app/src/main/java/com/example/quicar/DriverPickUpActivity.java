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
import com.example.entity.Location;
import com.example.entity.Request;
import com.example.font.Button_SF_Pro_Display_Medium;
import com.example.font.TextViewSFProDisplayLight;
import com.example.font.TextViewSFProDisplayMedium;
import com.example.font.TextViewSFProDisplayRegular;
import com.example.listener.OnGetRequestDataListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
import java.util.concurrent.TimeUnit;

public class DriverPickUpActivity extends BaseActivity implements OnGetRequestDataListener {
    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    TextViewSFProDisplayRegular riderEmail, riderPhone, startAddress, endAddress;
    TextViewSFProDisplayMedium riderName;
    Button_SF_Pro_Display_Medium confirmButton;
    TextViewSFProDisplayRegular callButton, emailButton;
    Request currentRequest = null;
    DirectionsResult directionsResult;

    Location start_location, end_location;
    MarkerOptions start, destination;
    List<MarkerOptions> markerOptionsList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_pick_up, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_driver_pick_up);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

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



        start_location = new Location(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        end_location = currentRequest.getStart();


        start = new MarkerOptions().position(new LatLng(start_location.getLat(), start_location.getLon())).title("origin");
        destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");

        markerOptionsList.add(start);
        markerOptionsList.add(destination);


        DateTime now = new DateTime();
        String start_address = start_location.getAddressName();
        String end_address = end_location.getAddressName();
        System.out.println("-----start address name-----"+start_address);
        System.out.println("-----end address name-------"+end_address);

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



        RequestDataHelper
                .getInstance()
                .queryUserRequest(DatabaseHelper.getInstance().getCurrentUserName(),
                        "driver", this);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Edited by Jeremy */
                if (currentRequest == null)
                    return;
                /* End here */

                RequestDataHelper
                        .getInstance()
                        .setRequestPickedUp(currentRequest.getRid(),
                                DriverPickUpActivity.this);

            }
        });

    }

    /**
     * Draw route methods
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        boolean success = true;
        mMap = googleMap;
        mMap.addMarker(start);
        mMap.addMarker(destination);
        showAllMarkers();

    }

    public void showAllMarkers() {
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


    protected GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.map_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
        return geoApiContext;
    }


    protected void addPolyline(DirectionsResult results, GoogleMap mMap) {
        if (results != null) {
//            if (results.routes.length == 0)


            List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
            mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(0x802e8b57));
            System.out.println("----------Time---------- :"+ results.routes[0].legs[0].duration.humanReadable);
            System.out.println("----------Distance---------- :" + results.routes[0].legs[0].distance.humanReadable);

        }
        else{
            System.out.println("------- null request queried.--------------");

        }
    }


    /**
     * RequestDataHelper methods
     * @param requests
     * @param tag
     */
    // if successfully successfully set currentRequest status to PickUp
    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag.equals(RequestDataHelper.SET_PICKEDUP_TAG)) {
            System.out.println("------------ request is set to pick up -----------");

            Intent intent = new Intent(DriverPickUpActivity.this, DriverOnGoingActivity.class);
            intent.putExtra("current accepted request", currentRequest);
            startActivity(intent);
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
