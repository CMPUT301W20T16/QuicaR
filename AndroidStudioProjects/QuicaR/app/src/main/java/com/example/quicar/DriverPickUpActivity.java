package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.entity.Request;
import com.example.font.Button_SF_Pro_Display_Medium;
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

public class DriverPickUpActivity extends DrawRouteBaseActivity implements OnGetRequestDataListener {
    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    Button_SF_Pro_Display_Medium confirmButton;
    Request currentRequest = null;
    DirectionsResult directionsResult;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        currentRequest = (Request) intent.getSerializableExtra("current accepted request");

        start_location = currentRequest.getStart();
        end_location = currentRequest.getDestination();

        System.out.println(String.format("--------requestInfo:-------%s %s %s %s", start_location.getLat(),start_location.getLon(),end_location.getLat(),end_location.getLon()));


        navigationView.inflateMenu(R.layout.activity_driver_pick_up);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_pick_up, frameLayout);



        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_driver_pick_up);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        confirmButton = linearLayout.findViewById(R.id.confirm_button);

        RequestDataHelper.getInstance().setOnNotifyListener(this);


//        mRequest = new Request();
        //get data from firebase
        System.out.println("------------------------current user name: " + DatabaseHelper.getInstance().getCurrentUserName());


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

                System.out.println("Request id-------------" + currentRequest.getRid());
                Intent intent = new Intent(DriverPickUpActivity.this, DriverOnGoingActivity.class);
                intent.putExtra("current accepted request", currentRequest);
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
//        if (tag.equals(RequestDataHelper.USER_REQ_TAG)) {
//
//            if (requests.size() > 0) {
//                //  always check if the return value is valid
//                System.out.println("------------ all open request obtained -----------");
//                for (Request request: requests) {
//                    if (request.getAccepted()
//                            /* Edited by Jeremy */
//                            && request.getRid().equals(DatabaseHelper.getInstance().getUserState().getRequestID())
//                            /* End here */
//                    ) {
//                        mRequest = request;
//
////                        start_location = mRequest.getStart();
////                        end_location = mRequest.getDestination();
////
////                        System.out.println("start location" + start_location.getLat() + start_location.getLon());
////                        System.out.println("end location" + end_location.getLat() + end_location.getLon());
////
////                        start = new MarkerOptions().position(new LatLng(start_location.getLat(), start_location.getLon())).title("origin");
////                        destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");
////
////                        new FetchURL(this)
////                                .execute(getUrl(start.getPosition(), destination.getPosition(), "driving"), "driving");
//
//                    }
//                }
//            }
//            else {
//                System.out.println("------------ empty list obtained -----------");
//            }
//        }

    }


//     protected GeoApiContext getGeoContext() {
//        GeoApiContext geoApiContext = new GeoApiContext();
//        geoApiContext.setQueryRateLimit(3)
//                .setApiKey(getString(R.string.map_key))
//                .setConnectTimeout(1, TimeUnit.SECONDS)
//                .setReadTimeout(1, TimeUnit.SECONDS)
//                .setWriteTimeout(1, TimeUnit.SECONDS);
//        return geoApiContext;
//    }
//
//
//     protected void addPolyline(DirectionsResult results, GoogleMap mMap) {
//        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
//        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
//    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(start);
        mMap.addMarker(destination);
        showAllMarkers();
        addPolyline(directionsResult,mMap);


    }

    @Override
    public void onTaskDone(Object... values) {
//        if (currentPolyline != null)
//            currentPolyline.remove();
//
//        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

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
