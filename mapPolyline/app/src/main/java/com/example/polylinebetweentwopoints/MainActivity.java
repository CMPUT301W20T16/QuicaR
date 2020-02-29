package com.example.polylinebetweentwopoints;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.polylinebetweentwopoints.DirectionHelpers.FetchURL;
import com.example.polylinebetweentwopoints.DirectionHelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private MarkerOptions start,destination;
    Button getDirection;
    private Polyline currentPolyline;
    List<MarkerOptions> markerOptionsList = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDirection = findViewById(R.id.btnGetDirection);
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchURL(MainActivity.this)
                        .execute(getUrl(start.getPosition(),destination.getPosition(),"driving"),"driving");


            }
        });

        start = new MarkerOptions().position(new LatLng(27.658143,85.3199503)).title("origin");
        destination = new MarkerOptions().position(new LatLng(27.667491,85.3208583)).title("destination");

        markerOptionsList.add(start);
        markerOptionsList.add(destination);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(start);
        mMap.addMarker(destination);
        showAllMarkers();
        

    }

    private void showAllMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (MarkerOptions m : markerOptionsList){
            builder.include(m.getPosition());

        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int)(width * 0.30);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width,height,padding);
        mMap.animateCamera(cu);





    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode){
        String str_origin = "origin=" + origin.latitude+","+origin.longitude;
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        String mode = "mode=" + directionMode;
        String parameter = str_origin +"&"+ str_dest +"&"+ mode;
        String format = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + format + "?"
                + parameter + "&key=AIzaSyC2x1BCzgthK4_jfvqjmn6_uyscCiKSc34";


        return url;

    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();

        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);


    }
}
