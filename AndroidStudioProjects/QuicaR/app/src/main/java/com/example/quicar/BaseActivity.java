package com.example.quicar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.arsy.maps_library.MapRadar;


import com.example.datahelper.DatabaseHelper;
import com.example.entity.Request;
import com.example.user.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class BaseActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener {
    protected GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation = null;
    protected LocationRequest mLocationRequest;
    protected Marker mCurrLocationMarker;
    protected Geocoder geocoder;
    protected List<Address> addresses;
    MapRadar mapRadar;
    CircleOptions circleOptions;
    Circle mapCircle;

//    Request currentRequest = null;

    protected FrameLayout frameLayout;
    protected DrawerLayout drawer;
    protected NavigationView navigationView;
    protected List<MarkerOptions> markerOptionsList = new ArrayList<>();

    private double radius = 1000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //initialize view

        // set up google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // set framelayout so the children of base activity can inflate its own content
        frameLayout = (FrameLayout) findViewById(R.id.container);


        // set up the action tool bar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // set up the left navigation  drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        View headerView = navigationView.getHeaderView(0);

        TextView userName_textView = headerView.findViewById(R.id.userName_textView);
        TextView userEmail_textView = headerView.findViewById(R.id.userEmail_textView);

        User currentUser = DatabaseHelper.getInstance().getCurrentUser();
        String userEmailStr =currentUser.getAccountInfo().getEmail();
        String userNameStr = currentUser.getAccountInfo().getUserName();


        userName_textView.setText(userNameStr);
        userEmail_textView.setText(userEmailStr);


        // connect navigation drawer to tool bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close);
        toggle.syncState();


    }




    /**
     * google map methods
     */
    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        }
    }



    protected Location getmLastLocation(){

        return mLastLocation;
    }

    /**
     * This is executed when the map is drawing on the UI
     *
     *
     *
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
        mapRadar.withDistance(2000);
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





    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

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


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    protected void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(BaseActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    /*** added by Yuxin March 28 th **************/

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
//            System.out.println("----------Time---------- :"+ results.routes[0].legs[0].duration.humanReadable);
//            System.out.println("----------Distance---------- :" + results.routes[0].legs[0].distance.humanReadable);

        }
        else{
            System.out.println("------- null request queried.--------------");

        }
    }

    protected double estimateFare (long distance){
        double fare;

        if(distance<1000){
            fare = 7.0;

        }
        else if (distance <= 5000 && distance >= 1000){
            fare = 5 + (distance / 1000)*2.3;
        }
        else{
            fare = (distance/1000)*2.0;
        }


        return fare;
    }

    /******* ended here *********************************************/





    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }

                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mapRadar.isAnimationRunning()) {
//            mapRadar.stopRadarAnimation();
//        }
//    }





    /**
     * drawer method
     */
    // enable user to close the navigation drawer
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // enable user to select item from navigation drawer

    /**
     * This is executed when the one item is selected
     *
     *
     *
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                // change here
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
//                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivityForResult(intent, 2);
                break;


            case R.id.nav_driver_mode:
                if(DatabaseHelper.getInstance().getCurrentMode() == "rider") {

                    User currentUser = DatabaseHelper.getInstance().getCurrentUser();
                    if(currentUser.isDriver()){
                        Intent intent2 = new Intent(getApplicationContext(), DriverBrowsingActivity.class);
                        startActivity(intent2);

                    }

                    else{
                        Intent intent2 = new Intent(getApplicationContext(), RegisterDriverActivity.class);
                        startActivity(intent2);
                    }

                }
                break;

            case R.id.rider_mode:
                if(DatabaseHelper.getInstance().getCurrentMode() == "driver") {
                    //Toast.makeText(this, "Enter if statement!", Toast.LENGTH_LONG).show();


                    Intent intent3 = new Intent(getApplicationContext(), RiderRequestActivity.class);
                    startActivity(intent3);
                }
                break;
            case R.id.nav_wallet:
                User user = DatabaseHelper.getInstance().getCurrentUser();
                if (user.getAccountInfo().getWallet().isOpen()){
                    Intent intent4 = new Intent(getApplicationContext(), WalletOverviewActivity.class);
                    //Intent intent4 = new Intent(getApplicationContext(), PayPasswordEnterActivity.class);
                    startActivity(intent4);
                }else{
                    //Intent intent4 = new Intent(getApplicationContext(), WalletOverviewActivity.class);
                    Intent intent4 = new Intent(getApplicationContext(), PayPasswordSetActivity.class);
                    startActivity(intent4);
                }
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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


}