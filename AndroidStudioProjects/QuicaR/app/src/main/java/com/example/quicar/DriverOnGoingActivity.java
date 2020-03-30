package com.example.quicar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.io.IOException;
import java.sql.Driver;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DriverOnGoingActivity extends BaseActivity implements OnGetRequestDataListener {
    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    TextViewSFProDisplayRegular riderEmail, riderPhone, startAddress, endAddress;
    TextViewSFProDisplayMedium riderName;
    Button_SF_Pro_Display_Medium confirmButton;
    TextViewSFProDisplayRegular callButton, emailButton;
    Request currentRequest = null;
    ImageView qrCode;

    Location start_location, end_location;
    MarkerOptions start, destination;
    List<MarkerOptions> markerOptionsList = new ArrayList<>();
    DirectionsResult directionsResult;

    long tStart;


    final private String PROVİDER = LocationManager.GPS_PROVIDER;

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
        currentRequest = DatabaseHelper.getInstance().getUserState().getCurrentRequest();



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
        startAddress = linearLayout.findViewById(R.id.start_address);
        endAddress = linearLayout.findViewById(R.id.end_address);

        startAddress.setText(currentRequest.getStartAddrName());
        endAddress.setText(currentRequest.getDestAddrName());
        riderEmail.setText(currentRequest.getRider().getAccountInfo().getEmail());
        riderPhone.setText(currentRequest.getRider().getAccountInfo().getPhone());
        riderName.setText(currentRequest.getRider().getName());

        // start timing the activity
        long tStart = System.currentTimeMillis();




        start_location = currentRequest.getStart();
        end_location = currentRequest.getDestination();


        start = new MarkerOptions().position(new LatLng(start_location.getLat(), start_location.getLon())).title("origin").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_caronmap));
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
                    .destination(end_address).departureTime(now)
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

                if (currentRequest == null)
                    return;

                System.out.println("set arrived----------");

                RequestDataHelper
                        .getInstance()
                        .setRequestArrived(currentRequest.getRid(),
                                DriverOnGoingActivity.this);

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

        //draw route
        if (directionsResult != null) {
            addPolyline(directionsResult, mMap);
        }
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

    }


    protected void showQRBottom() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DriverOnGoingActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.driver_scan_qr, (LinearLayout) findViewById(R.id.qr_linear));
        Button scan = (Button)bottomSheetView.findViewById(R.id.scan);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DriverScanActivity.class));
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
        if (tag.equals(RequestDataHelper.SET_ARRIVED_TAG)) {

            System.out.println("susccess------------------");

            long tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            double elapsedSeconds = tDelta / 1000.0;


            //if the ride is longer or shorter than expcetd
            //charge 1 extra dollar per 5 minutes
//            float extraCost = (elapsedSeconds - currentRequest.getEstimateTime()) / 5;
//            currentRequest.setEstimatedCost(currentRequest.getEstimatedCost() + extraCost);
            showQRBottom();

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
