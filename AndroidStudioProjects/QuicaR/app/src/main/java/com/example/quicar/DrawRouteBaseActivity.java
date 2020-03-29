package com.example.quicar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.entity.Location;
import com.example.entity.Request;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class DrawRouteBaseActivity extends BaseActivity implements TaskLoadedCallback{

    Request mRequest;
    Location start_location, end_location, currentLocation;
    protected MarkerOptions start, destination;
    protected Polyline currentPolyline;
    List<MarkerOptions> markerOptionsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Draw route methods
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

        if (start != null && destination != null) {
            mMap.addMarker(start);
            mMap.addMarker(destination);
            showAllMarkers();
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

    @Override
    public void onLocationChanged(android.location.Location location) {

        mLastLocation = location;

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //place a new marker for current location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_caronmap));

        mCurrLocationMarker = mMap.addMarker(markerOptions);

    }

//    public String getUrl(LatLng origin, LatLng dest, String directionMode) {
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//        String mode = "mode=" + directionMode;
//        String parameter = str_origin + "&" + str_dest + "&" + mode;
//        String format = "json";
//        String url = "https://maps.googleapis.com/maps/api/directions/" + format + "?"
//                + parameter + "&key=AIzaSyC2x1BCzgthK4_jfvqjmn6_uyscCiKSc34";
//
//
//        return url;
//
//    }
//

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
            mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(0x2e8b57));
            System.out.println("----------Time---------- :"+ results.routes[0].legs[0].duration.humanReadable);
            System.out.println("----------Distance---------- :" + results.routes[0].legs[0].distance.humanReadable);

        }
        else{
            System.out.println("------- null request queried.--------------");

        }
    }




    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();

        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
