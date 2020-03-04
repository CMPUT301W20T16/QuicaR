package com.example.quicar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.quicar_mapview.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RiderSelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Button confirmButton;

    String address,locality,subLocality,state,postalCode,country,knownname,phone;
    TextView txtaddress, txtlocality, txtsubLocality, txtstate,txtpostalCode,txtcountry,txtknownname,txtphone;
    private double destinationLat,destinationLng;
    private com.example.quicar.Location destination_location, pick_up_location;

    private GoogleMap mMap;

    Marker marker;
    PlacesClient placesClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_select_location);

        // add back button on action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        //get data from intent, i.e., current address
        Intent intent = getIntent();
        String current_address = (String) intent.getSerializableExtra("current address name");
        pick_up_location = (Location) intent.getSerializableExtra("current location") ;
//
//
//        final String destination_location_input = destination.getText().toString();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        String apiKey= "AIzaSyCyECZAmZ2NxQz10Qijm-ngagqBdHJblzk";
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),apiKey);
        }

        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment pickUp =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.pick_up);
        pickUp.setText(current_address);

        com.example.quicar.Location result = onCreateAutoCompletionFragment(pickUp);
        if (result != null) {
            pick_up_location = result;
        }


        final AutocompleteSupportFragment destination =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.destination);
        onCreateAutoCompletionFragment(destination);

        result = onCreateAutoCompletionFragment(destination);
        if (result != null) {
            destination_location = result;
        }










    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.setMyLocationEnabled(true);
    }

    public com.example.quicar.Location onCreateAutoCompletionFragment(AutocompleteSupportFragment autocompleteSupportFragment) {
        float currentLat, currentLng;

        autocompleteSupportFragment.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.LAT_LNG,
                        Place.Field.PHONE_NUMBER,
                        Place.Field.ADDRESS
                )
        );

        autocompleteSupportFragment.setOnPlaceSelectedListener(
                new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(@NonNull Place place) {
                        LatLng latLng = place.getLatLng();
                        currentLat = (float) latLng.latitude;
                        currentLng = (float) latLng.longitude;

                        phone = place.getPhoneNumber();
                        address = place.getAddress();

                        if ( marker != null){
                            marker.remove();
                        }

                        mMap.clear();
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.5f ),null);


                        Geocoder gcd =  new Geocoder(getBaseContext(), Locale.getDefault());

                        List<Address> addresses;

                        try {
                            addresses = gcd.getFromLocation(currentLat,currentLng,1);
                            if (addresses.size()> 0){
                                locality = addresses.get(0).getLocality();
                                subLocality = addresses.get(0).getSubLocality();
                                state = addresses.get(0).getAdminArea();
                                country = addresses.get(0).getCountryName();
                                postalCode = addresses.get(0).getPostalCode();
                                knownname = addresses.get(0).getFeatureName();


                            }


                        }catch (IOException e){
                            e.printStackTrace();
                        }



                    }

                    @Override
                    public void onError(@NonNull Status status) {

                    }
                }
        );

        return new com.example.quicar.Location(currentLat, currentLng);
    }


    /**
     * listview包含近10(?)个rider去过的location
     * 传两个location（pickup和destination）去rider_confirm_request.activity


        confirmButton = findViewById(R.id.confirm_button);

        // if customer pressed confirm button
        // a new request is made and stored to Firebase database
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RiderSelectLocationActivity.this, RiderConfirmRiderActivity.class);
                startActivity(intent);
            }
        });
     */


    }

