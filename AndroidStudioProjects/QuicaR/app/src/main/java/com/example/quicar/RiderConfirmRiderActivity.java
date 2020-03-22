package com.example.quicar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.entity.Request;
import com.example.entity.Location;
import com.example.listener.OnGetRequestDataListener;
import com.example.datahelper.RequestDataHelper;
import com.example.datahelper.DatabaseHelper;
import com.example.user.User;




import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
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




public class RiderConfirmRiderActivity extends DrawRouteBaseActivity implements OnGetRequestDataListener {
    //extends DrawRouteBaseActivity

    private OnGetRequestDataListener listener = this;

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;


    Button confirmButton;
    Button cancelButton;
    Request currentRequest = null;
    DirectionsResult directionsResult;


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




        //get data from intent, i.e., current address
        Intent intent = getIntent();
        start_location = (Location) intent.getSerializableExtra("start location");
        end_location = (Location) intent.getSerializableExtra("end location");

        start = new MarkerOptions().position(new LatLng(start_location.getLat(), start_location.getLon())).title("origin");
        destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");
        LatLng start_latlng = new LatLng(start_location.getLat(),start_location.getLon());
        LatLng dest_latlng = new LatLng(end_location.getLat(),end_location.getLon());



        markerOptionsList.add(start);
        markerOptionsList.add(destination);


        //mMap.clear();
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

//        addMarkersToMap(directionsResult,mMap);
//        addPolyline(directionsResult,mMap);




        /*new FetchURL(RiderConfirmRiderActivity.this)
                .execute(getUrl(start.getPosition(), destination.getPosition(), "driving"), "driving");*/

        /**
         * estimate cost function
         */
        // if user selected confirm button
        // add new request to database
        /**
         * when click confirm button, following will be executed
         */
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 *   instantiate a new User class for current user
                  */
                User newUser = new User();
                newUser.setName(DatabaseHelper.getInstance().getCurrentUserName());

                /**
                 *    new request's cost is hard coded for now
                  */
                Request request = new Request(start_location, start_location.getAddressName(),
                        end_location, end_location.getAddressName(),
                        newUser, new User(), 20.0f);

                currentRequest = request;


                /***2020.03.20 new part Yuxin for calculating distance------------------------------------------------------------------
                 */

//                DateTime now = new DateTime();
//                try {
//                    directionsResult = DirectionsApi.newRequest(getGeoContext())
//                            .mode(TravelMode.DRIVING).origin(start_location.getAddressName())
//                            .destination(end_location.getAddressName()).departureTime(now)
//                            .await();
//                } catch (ApiException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                addMarkersToMap(directionsResult,mMap);

                /** end new part
                 -----------------------------------------------------------------------------
                 */


                RequestDataHelper.getInstance().addNewRequest(request, listener);



                Intent intent = new Intent(RiderConfirmRiderActivity.this, RiderWaitingRideActivity.class);
                intent.putExtra("current request", currentRequest);
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

    /**
     * Draw route methods
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(start);
        mMap.addMarker(destination);
        showAllMarkers();
        addPolyline(directionsResult,mMap);
        System.out.println("----------Time---------- :"+ directionsResult.routes[0].legs[0].duration.humanReadable);
        System.out.println("----------Distance---------- :" + directionsResult.routes[0].legs[0].distance.humanReadable);

//        addMarkersToMap(directionsResult,mMap);

//        addMarkersToMap(directionsResult,mMap);
//        addPolyline(directionsResult,mMap);


//        mMap.addMarker(start);
//        mMap.addMarker(destination);
//        showAllMarkers();
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

    public String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameter = str_origin + "&" + str_dest + "&" + mode;
        String format = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + format + "?"
                + parameter + "&key=AIzaSyC2x1BCzgthK4_jfvqjmn6_uyscCiKSc34";


        return url;    }


    /***2020.03.20 new part Yuxin for calculating distance------------------------------------------------------------------
     *
     */

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.map_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
        return geoApiContext;
    }

//   private GeoApiContext getGeoApiContext() {
//       return new GeoApiContext.Builder()
//               .apiKey(getString(R.string.map_key))
//               .build();
//   }


    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng)).title(results.routes[0].legs[0].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0].startAddress).snippet(getEndLocationTitle(results)));
    }


    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable;
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }



    /** end new part
     -----------------------------------------------------------------------------
     */






    /**
     * overide helper function for drawing a route
     * @param values
     */


    @Override
    public void onTaskDone(Object... values) {




        /**if (currentPolyline != null)
            currentPolyline.remove();

        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);**/


    }

    /**
     * automatically executed
     * @param requests
     * @param tag
     */
    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag == RequestDataHelper.ADD_REQ_TAG) {
            Toast.makeText(RiderConfirmRiderActivity.this, "rider request added successfully", Toast.LENGTH_SHORT).show();
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
