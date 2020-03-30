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
import android.widget.ImageView;
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


import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.example.datahelper.DatabaseHelper;
import com.example.user.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

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
    protected FirebaseAuth mAuth;

    private double radius = 1000;

//    ImageView imgHeaderHeadPro;


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


        // design for head
        // navView from base Activity
        navigationView = findViewById(R.id.nav_view);
        // header of navView
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        // head image in the nav header.xml
        ImageView imgHeaderHeadPro = (ImageView) headerView.findViewById(R.id.head_image);

        User currentUser = DatabaseHelper.getInstance().getCurrentUser();
        Double rate = currentUser.getAccountInfo().getDriverInfo().getRating();
//        if (rate != null){
//            if (rate >= 0.0 & rate < 1.0 ) {
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headtuling5);
//            } else if(rate >= 1.0 & rate < 2.0){
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headjojo1);
//            } else if(rate >= 2.0 & rate < 3.0){
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headdog2);
//            } else if(rate >= 3.0 & rate < 4.0){
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headvan3);
//            } else if (rate >= 4.0 & rate < 5.0) {
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headtop4);
//            } else if (rate >= 5.0 ){
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headteemo0);
//            }
//        }
        Integer sourceID = generate_icon(rate);
        imgHeaderHeadPro.setImageResource(sourceID);



        TextView userName_textView = headerView.findViewById(R.id.userName_textView);
        TextView userEmail_textView = headerView.findViewById(R.id.userEmail_textView);
        String userEmailStr =currentUser.getAccountInfo().getEmail();
        String userNameStr = currentUser.getAccountInfo().getUserName();


        userName_textView.setText(userNameStr);
        userEmail_textView.setText(userEmailStr);


        // connect navigation drawer to tool bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close);
        toggle.syncState();


        // alert
        String firstName = currentUser.getAccountInfo().getFirstName();
        String lastName = currentUser.getAccountInfo().getLastName();
        String phone = currentUser.getAccountInfo().getPhone();
//        System.out.println(firstName);
////        System.out.println("haha");
        if (firstName == null | lastName == null | phone == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("update profile")//设置标题
                    .setMessage("Hey,Loos like you forgot to update your personal information, please click user profile in the sidebar to update")//设置内容
                    .setCancelable(false)//设置是否可以点击对话框以外的地方消失
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // profile activity start
                            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                            startActivityForResult(intent, 2);
                        }
                    })
                    .setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });


            AlertDialog alertDialog = builder.create();

            alertDialog.show();

        }





    }

    /**
     * google map methods
     */
    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
            catch(Exception e){


            }

        }
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
            System.out.println("------------Can't find style");
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

        //move map camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));





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
        mLocationRequest.setInterval(10);
        mLocationRequest.setFastestInterval(10);
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
            case R.id.nav_account:
                Intent i = new Intent(getApplicationContext(), UpdateAccountActivity.class);
                startActivity(i);
                break;

            // logout activity start
            case R.id.nav_logout:

//                startActivity(intentLogout);
// log out directly
//                mAuth.getInstance().signOut();
                //log out directly
                mAuth.getInstance().signOut();
                Intent intentLogout = new Intent(getApplicationContext(), Login.class);
                intentLogout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogout);
                return true;
//                break;

            case R.id.nav_profile:
                // profile activity start
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
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

    /**
     * generate an icon based on user's rating
     * @return
     */
    public int generate_icon(Double rate) {
        if (rate != null){
            if (rate >= 0.0 & rate < 1.0 ) {
                return R.drawable.ic_headtuling5;
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headtuling5);
            } else if(rate >= 1.0 & rate < 2.0){
                return R.drawable.ic_headjojo1;
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headjojo1);
            } else if(rate >= 2.0 & rate < 3.0){
                return R.drawable.ic_headdog2;
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headdog2);
            } else if(rate >= 3.0 & rate < 4.0){
                return R.drawable.ic_headvan3;
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headvan3);
            } else if (rate >= 4.0 & rate < 5.0) {
                return R.drawable.ic_headtop4;
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headtop4);
            } else if (rate >= 5.0 ){
                return R.drawable.ic_headteemo0;
//                imgHeaderHeadPro.setImageResource(R.drawable.ic_headteemo0);
            }
        }
        return 0;
    }

}