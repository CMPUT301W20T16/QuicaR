package com.example.autocompletemap;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    String address,locality,subLocality,state,postalCode,country,knownname,phone;
    TextView txtaddress, txtlocality, txtsubLocality, txtstate,txtpostalCode,txtcountry,txtknownname,txtphone;

    private GoogleMap mMap;

    private double currentLat,currentLng;
    Marker marker;
    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txtaddress = findViewById(R.id.address);
        txtlocality = findViewById(R.id.locality);
        txtsubLocality = findViewById(R.id.subLocality);
        txtstate = findViewById(R.id.state);
        txtpostalCode = findViewById(R.id.postalCode);
        txtcountry = findViewById(R.id.country);
        txtknownname = findViewById(R.id.knownname);
        txtphone = findViewById(R.id.phone);




        String apiKey= "AIzaSyCyECZAmZ2NxQz10Qijm-ngagqBdHJblzk";
        if (!Places.isInitialized()){
            Places.initialize(MapsActivity.this,apiKey);
        }

        placesClient = Places.createClient(this);


        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        autocompleteSupportFragment.setText("where to");


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
                        currentLat = latLng.latitude;
                        currentLng = latLng.longitude;

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


                                txtaddress.setText(address);
                                txtlocality.setText(locality);
                                txtsubLocality.setText(subLocality);
                                txtstate.setText(state);
                                txtcountry.setText(country);
                                txtpostalCode.setText(postalCode);
                                txtknownname.setText(knownname);
                                txtphone.setText(phone);


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

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.setMyLocationEnabled(true);




    }


}
