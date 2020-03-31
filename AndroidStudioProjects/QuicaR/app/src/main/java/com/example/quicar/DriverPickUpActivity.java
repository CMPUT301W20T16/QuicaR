package com.example.quicar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.LocationDataHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.entity.Location;
import com.example.entity.Request;
import com.example.font.Button_SF_Pro_Display_Medium;
import com.example.font.TextViewSFProDisplayLight;
import com.example.font.TextViewSFProDisplayMedium;
import com.example.font.TextViewSFProDisplayRegular;
import com.example.listener.OnGetLocationDataListener;
import com.example.listener.OnGetRequestDataListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DriverPickUpActivity extends BaseActivity implements OnGetRequestDataListener, OnGetLocationDataListener, TaskLoadedCallback {
    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    TextViewSFProDisplayRegular riderEmail, riderPhone, startAddress, endAddress,travelTime;
    TextViewSFProDisplayMedium riderName;

    Button_SF_Pro_Display_Medium confirmButton;
    TextViewSFProDisplayRegular callButton, emailButton;
    Request currentRequest = null;
    DirectionsResult directionsResult;

    Location driver_start_location, driver_end_location;
    MarkerOptions start, destination;
    List<MarkerOptions> markerOptionsList = new ArrayList<>();

    protected Polyline currentPolyline;

    final private String PROVİDER = LocationManager.GPS_PROVIDER;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_pick_up, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_driver_pick_up);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        RequestDataHelper.getInstance().setOnNotifyListener(this);
        LocationDataHelper.getInstance().setOnNotifyListener(this);

        currentRequest = DatabaseHelper.getInstance().getUserState().getCurrentRequest();

        String riderNameStr = currentRequest.getRider().getName();


        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mLastLocation = mLocationManager.getLastKnownLocation(PROVİDER);


        //set up textView and buttons

        confirmButton = linearLayout.findViewById(R.id.confirm_button);
        riderEmail = linearLayout.findViewById(R.id.userEmail_textView);
        riderPhone = linearLayout.findViewById(R.id.userPhone_textView);
        riderName = linearLayout.findViewById(R.id.userName_textView);
        travelTime = linearLayout.findViewById(R.id.estimate_fare);
        callButton = linearLayout.findViewById(R.id.call_rider_button);
        emailButton = linearLayout.findViewById(R.id.email_rider_button);
        startAddress = linearLayout.findViewById(R.id.start_address);
        endAddress = linearLayout.findViewById(R.id.end_address);



        startAddress.setText(currentRequest.getStartAddrName());
        endAddress.setText(currentRequest.getDestAddrName());
        riderEmail.setText(currentRequest.getRider().getAccountInfo().getEmail());
        riderPhone.setText(currentRequest.getRider().getAccountInfo().getPhone());
        riderName.setText(currentRequest.getRider().getName());



        driver_start_location = new Location(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        String driverCurrentAddress = findAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        driver_start_location.setAddressName(driverCurrentAddress);
        driver_end_location = currentRequest.getStart();
        String driverPickUpAddress = driver_end_location.getAddressName();




        start = new MarkerOptions().position(new LatLng(driver_start_location.getLat(), driver_start_location.getLon()))
                .title("driver's current location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_caronmap));
        destination = new MarkerOptions().position(new LatLng(driver_end_location.getLat(), driver_end_location.getLon())).title("rider's pick up location");

        markerOptionsList.add(start);
        markerOptionsList.add(destination);

        new FetchURL(DriverPickUpActivity.this)
                .execute(getUrl(start.getPosition(), destination.getPosition(), "driving"), "driving");


        DateTime now = new DateTime();
//        String start_address = start_location.getAddressName();
        System.out.println("-----start address name-----" );
        System.out.println("-----end address name-------" );

        try {
            //GeoApiContext geoApiContext = getGeoContext();
            directionsResult = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(driverCurrentAddress)
                    .destination(driverPickUpAddress).departureTime(now)
                    .await();


            travelTime.setText(directionsResult.routes[0].legs[0].duration.humanReadable);

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if(directionsResult == null){
//            Toast.makeText(DriverPickUpActivity.this, "no route to this rider found!", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(DriverPickUpActivity.this,DriverBrowsingActivity.class);
//            startActivity(intent);
//            finish();
//
//        }
//
//        if(directionsResult.routes == null){
//            Toast.makeText(DriverPickUpActivity.this, "no route to this rider found!", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(DriverPickUpActivity.this,DriverBrowsingActivity.class);
//            startActivity(intent);
//            finish();
//
//        }

        travelTime.setText(directionsResult.routes[0].legs[0].duration.humanReadable);


//        RequestDataHelper
//                .getInstance()
//                .queryUserRequest(DatabaseHelper.getInstance().getCurrentUserName(),
//                        "driver", this);


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


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                //mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

//
//        try {
//            //draw route
//            //addPolyline(directionsResult, mMap);
//        }catch (Exception e){
//
//        }


    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }

    public String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameter = str_origin + "&" + str_dest + "&" + mode;
        String format = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + format + "?"
                + parameter + "&key=AIzaSyC2x1BCzgthK4_jfvqjmn6_uyscCiKSc34";


        return url;

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

    @Override
    public void onLocationChanged(android.location.Location location) {
        mLastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        if (start != null) {
            start.position(latLng);
        }
        else {
            start = new MarkerOptions().position(latLng).title("origin");
        }
        mMap.addMarker(start);

        //upload current location to database
        LocationDataHelper.getInstance().updateLocation(DatabaseHelper.getInstance().getCurrentUserName(), new Location((double)location.getLatitude(), (double)location.getLongitude()));

    }

    /**
     * helper function for address of selected location
     * @param lat
     * @param lng
     * @return
     */

    // get address name in String from lat and long
    public String findAddress(double lat, double lng) {
        // set pick up location automatically as customer's current location
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        if (lat != 0 && lng != 0) {
            try {
                addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                if (address.length() != 0) {
                    return address;
                }
            }

        }
        return null;

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
        System.out.println("cancel notification  --------- ");
        Toast.makeText(DriverPickUpActivity.this, "This request has been canceled!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DriverPickUpActivity.this, DriverBrowsingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCompleteNotification() {

    }

    @Override
    public void onFailure(String errorMessage, String tag) {

    }

    @Override
    public void onUpdate(Location location) {

    }
}
