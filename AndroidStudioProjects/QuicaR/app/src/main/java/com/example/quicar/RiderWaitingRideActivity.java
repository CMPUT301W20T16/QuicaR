package com.example.quicar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.LocationDataHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.entity.Location;
import com.example.datahelper.UserState;
import com.example.datahelper.UserStateDataHelper;
import com.example.entity.Request;
import com.example.font.Button_SF_Pro_Display_Medium;
import com.example.font.TextViewSFProDisplayMedium;
import com.example.font.TextViewSFProDisplayRegular;
import com.example.listener.OnGetLocationDataListener;
import com.example.listener.OnGetRequestDataListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * rider has successfully sent out a request
 * rider is waiting the system to pair them with a driver
 * rider will get a notification if the match is successful
 * rider is able to cancel ride before driver arrived at pick up location
 * (rider can only cancel ride in a reasonable amount of time)
 */

public class RiderWaitingRideActivity extends BaseActivity implements OnGetRequestDataListener, OnGetLocationDataListener, TaskLoadedCallback {

    private LinearLayout linearLayout;
    private BottomSheetBehavior bottomSheetBehavior;

    private TextView driverDistance;private TextView driverName, driverRating, driverEmail, driverPhone, startAddress, endAddress, driverPlateNumber;private Button DetailButton;
    private TextViewSFProDisplayRegular CallButton;
    private TextViewSFProDisplayRegular EmailButton, estimateFare;
    private Button_SF_Pro_Display_Medium CancelButton;
    private CircleImageView iconImage;

    private Location rider_start_location, rider_end_location;
    private MarkerOptions start, destination, driver_loc;
    private DirectionsResult directionsResult;
    Marker driverLoc;

    Request mRequest;


    final private String PROVİDER = LocationManager.GPS_PROVIDER;





    /**
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        Intent intent = getIntent();
//        currentRequest = (Request) intent.getSerializableExtra("current request");
        /** Added by Jeremy */
        //mRequest = currentRequest;
        /** End here */

        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_waiting_ride, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_ride_status);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        // get activated request from firebase
        RequestDataHelper.getInstance().setOnNotifyListener(this);
        mRequest = (Request) DatabaseHelper.getInstance().getUserState().getCurrentRequest();


        // set Buttons
//        DetailButton = linearLayout.findViewById(R.id.driver_detail_button);
        CallButton = linearLayout.findViewById(R.id.call_driver_button);
        EmailButton = linearLayout.findViewById(R.id.email_driver_button);
        CancelButton = linearLayout.findViewById(R.id.cancel_button);

        // set TextView
        driverName = linearLayout.findViewById(R.id.driver_name_tv);
        driverEmail = linearLayout.findViewById(R.id.driver_email_tv);
        driverPhone = linearLayout.findViewById(R.id.driver_phone_tv);
        driverRating = linearLayout.findViewById(R.id.driver_rating_tv);
        estimateFare = linearLayout.findViewById(R.id.estimate_fare);
        startAddress = linearLayout.findViewById(R.id.start_address);
        endAddress = linearLayout.findViewById(R.id.end_address);
        driverPlateNumber = linearLayout.findViewById(R.id.driver_plate_number);


        driverName.setText(mRequest.getDriver().getAccountInfo().getFirstName());
        driverPlateNumber.setText(mRequest.getDriver().getAccountInfo().getDriverInfo().getPlateNumber());

        driverEmail.setText(mRequest.getDriver().getAccountInfo().getEmail());
        driverPhone.setText(mRequest.getDriver().getAccountInfo().getPhone());
        driverRating.setText(mRequest.getDriver().getAccountInfo().getDriverInfo().getRating().toString());
        estimateFare.setText("$" + Float.toString(mRequest.getEstimatedCost()));
        startAddress.setText(mRequest.getStart().getAddressName());
        endAddress.setText(mRequest.getDestination().getAddressName());

        //set Image View
        iconImage = linearLayout.findViewById(R.id.icon);
        Integer sourceID = generate_icon(mRequest.getDriver().getAccountInfo().getDriverInfo().getRating());
        iconImage.setImageResource(sourceID);


        rider_start_location = mRequest.getStart();
        String riderStartAddress = rider_start_location.getAddressName();

        rider_end_location = mRequest.getDestination();
        String riderDestinationAddress = rider_end_location.getAddressName();


        // connect location database
        LocationDataHelper.getInstance().setOnNotifyListener(this);


        start = new MarkerOptions().position(new LatLng(rider_start_location.getLat(), rider_start_location.getLon())).title("rider's start location");
        destination = new MarkerOptions().position(new LatLng(rider_end_location.getLat(), rider_end_location.getLon())).title("rider's destination location");

        driver_loc = new MarkerOptions().position(new LatLng(0,0)).title("driver's current location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_caronmap));

        markerOptionsList.add(driver_loc);
        markerOptionsList.add(start);
        markerOptionsList.add(destination);

        new FetchURL(RiderWaitingRideActivity.this)
                .execute(getUrl(start.getPosition(), destination.getPosition(), "driving"), "driving");


        //draw route
        DateTime now = new DateTime();

        try {
            //GeoApiContext geoApiContext = getGeoContext();
            directionsResult = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(riderStartAddress)
                    .destination(riderDestinationAddress).departureTime(now)
                    .await();

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(directionsResult == null){
            Toast.makeText(RiderWaitingRideActivity.this, "no route to this rider found!", Toast.LENGTH_SHORT).show();



            }




        // set on click listener for buttons
        // transfer to default dial page
        CallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + driverPhone.getText().toString()));
                startActivity(intent);

            }
        });

        // if user tries to cancel the ride while driver is on their way
        // call cancelRequest so ride will be deleted in the database
        // user back to the main screen
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRequest != null) {
                    RequestDataHelper
                            .getInstance()
                            .cancelRequest(mRequest.getRid(), RiderWaitingRideActivity.this);


                } else {
                    System.out.println("Unable to retrieve current Request--------------");
                }
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
        mMap.setMyLocationEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);

        driverLoc = mMap.addMarker(driver_loc);
        showAllMarkers();

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            //mMap.setMyLocationEnabled(true);
        }


    }




    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();


        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }



    @Override
    public void onLocationChanged(android.location.Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        if (driver_loc != null) {
            driverLoc.remove();
//            driver_loc.position(latLng);
        }
        else {
            driver_loc = new MarkerOptions().position(latLng).title("driver's current location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_caronmap));
            markerOptionsList.add(driver_loc);

        }
        driverLoc = mMap.addMarker(driver_loc);
    }





    /**
     * Request Database helper listener methods
     * @param requests
     * @param tag
     */
    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag.equals(RequestDataHelper.CANCEL_REQ_TAG)) {

//            RequestDataHelper
//                    .getInstance()
//                    .queryUserRequest(DatabaseHelper.getInstance().getCurrentUserName(),
//                            "rider", this);
            UserState userState = DatabaseHelper.getInstance().getUserState();
            userState.setOnConfirm(Boolean.FALSE);
            userState.setOnMatching(Boolean.FALSE);
            userState.setActive(Boolean.FALSE);
            userState.setCurrentRequest(new Request());
            DatabaseHelper.getInstance().setUserState(userState);
            UserStateDataHelper.getInstance().recordState();

            Intent intent = new Intent(RiderWaitingRideActivity.this, RiderRequestActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * execute following when an request becomes active (matches a driver successfully)
     * @param request
     */
    @Override
    public void onActiveNotification(Request request) {
    }

    /**
     * execute when the correspond driver cliks picked up rider
     * @param request
     */
    @Override
    public void onPickedUpNotification(Request request) {
        Toast.makeText(RiderWaitingRideActivity.this, "rider is picked up by driver", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(RiderWaitingRideActivity.this, RiderOnGoingRequestActivity.class);
        startActivity(intent);
        finish();
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


    @Override
    public void onUpdate(Location location) {
        if (location != null) {
            System.out.println("Get location--------------" + location);
            System.out.println("Location-------------" + location.getLat() + "  " + location.getLon());
        }

        assert location != null;
        android.location.Location location_temp = new android.location.Location(PROVİDER);
        location_temp.setLatitude(location.getLat());
        location_temp.setLongitude(location.getLon());
        System.out.println("New location -----------" + location_temp.getLatitude() + " " + location_temp.getLongitude());
        onLocationChanged(location_temp);


    }

}
