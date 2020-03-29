package com.example.quicar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.entity.Request;

import com.example.font.Button_SF_Pro_Display_Medium;
import com.example.font.TextViewSFProDisplayLight;
import com.example.font.TextViewSFProDisplayMedium;
import com.example.font.TextViewSFProDisplayRegular;
import com.example.listener.OnGetRequestDataListener;
import com.example.user.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DriverPickUpActivity extends DrawRouteBaseActivity implements OnGetRequestDataListener {

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    TextViewSFProDisplayRegular riderEmail, riderPhone, startAddress, endAddress;
    TextViewSFProDisplayMedium riderName;
    Button_SF_Pro_Display_Medium confirmButton;
    TextViewSFProDisplayRegular callButton, emailButton;
    Request currentRequest = null;
    DirectionsResult directionsResult;

    LocationManager locationManager;
    LocationListener locationListener;
    Context context;
    boolean gps_enabled,network_enabled;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_pick_up, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_driver_pick_up);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        RequestDataHelper.getInstance().setOnNotifyListener(this);

//
//        requestPermission();
//
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(DriverPickUpActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            System.out.println("------PERMISSION DENIED!!!!!!!");
//            finish();
//        }
//
//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(DriverPickUpActivity.this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if(location != null){
//                    currentLocation = location;
//                }
//                else{
//                    System.out.println("--------current location is indeed null!!!!!!!!!!");
//
//                }
//
//
//            }
//        });

        //currentLocation = geocoder.getLastLocation()

//        fetchLastLocation();

        //System.out.println("----------current location:-"+currentLocation.getLatitude()+currentLocation.getLongitude());

//        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);



        start = new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("origin");
        destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");

        markerOptionsList.add(start);
        markerOptionsList.add(destination);

        DateTime now = new DateTime();
        String start_address = findAddress(currentLocation.getLatitude(),currentLocation.getLongitude());
        String end_address = end_location.getAddressName();
        System.out.println("-----start address name-----"+start_address);
        System.out.println("-----end address name-------"+end_address);

        try {
            //GeoApiContext geoApiContext = getGeoContext();
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


        start_location = currentRequest.getStart();
        end_location = currentRequest.getDestination();



        destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");

        //markerOptionsList.add(start);
        //markerOptionsList.add(destination);


//        DateTime now = new DateTime();
//        String start_address = findAddress(currentLocation.getLatitude(),currentLocation.getLongitude());
//        String end_address = end_location.getAddressName();
//        System.out.println("-----start address name-----"+start_address);
//        System.out.println("-----end address name-------"+end_address);
//
//        try {
//            //GeoApiContext geoApiContext = getGeoContext();
//            directionsResult = DirectionsApi.newRequest(getGeoContext())
//                    .mode(TravelMode.DRIVING).origin(start_address)
//                    .destination(end_address).departureTime(now)
//                    .await();
//
//        } catch (ApiException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



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


    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);

    }

//    private void fetchLastLocation() {
//
//        Task<Location> task = fusedLocationProviderClient.getLastLocation();
//        task.addOnSuccessListener( new View.O {
//            if(location != null) {
//                System.out.println("--------current location is not null!!!!!!!!!!");
//                currentLocation = location;
//            }
//            else{
//                System.out.println("--------current location is indeed null!!!!!!!!!!");
//
//            }
//        });
//
//
//    }
//

    @Override
    public void onLocationChanged(Location location) {

        this.mLastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));


        //update map radar
        mapRadar.withLatLng(new LatLng(location.getLatitude(), location.getLongitude()));


        //place a new circle behind map radar
        if (mapCircle != null) {
            mapCircle.remove();
        }

        circleOptions.center(latLng);
        mapCircle = mMap.addCircle(circleOptions);

    }


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
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("----------current location:-"+currentLocation.getLatitude()+currentLocation.getLongitude());

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
