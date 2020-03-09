package com.example.quicar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.quicar.R;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RiderSelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback, OnGetRequestDataListener {
    private EditText pickUp;
    private EditText destination;
    private Button confirmButton;

    // added for request database helper
    public OnGetRequestDataListener listener = this;

    String address,locality,subLocality,state,postalCode,country,knownname,phone;
    TextView txtaddress, txtlocality, txtsubLocality, txtstate,txtpostalCode,txtcountry,txtknownname,txtphone;
    private double currentLat,currentLng;

    private GoogleMap mMap;

    Marker marker;
    PlacesClient placesClient;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_select_location);

        // add back button on action bar
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);


        //get data from intent, i.e., current address
//        Intent intent = getIntent();
//        String current_address = (String) intent.getSerializableExtra("current pos");
//
//        pickUp = findViewById(R.id.pick_up);
//        destination = findViewById(R.id.destination);
//
//        pickUp.setText(current_address, TextView.BufferType.EDITABLE);
//
//        final String destination_location_input = destination.getText().toString();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//        txtaddress = findViewById(R.id.address);
//        txtlocality = findViewById(R.id.locality);
//        txtsubLocality = findViewById(R.id.subLocality);
//        txtstate = findViewById(R.id.state);
//        txtpostalCode = findViewById(R.id.postalCode);
//        txtcountry = findViewById(R.id.country);
//        txtknownname = findViewById(R.id.knownname);
//        txtphone = findViewById(R.id.phone);


        String apiKey= "AIzaSyCyECZAmZ2NxQz10Qijm-ngagqBdHJblzk";
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),apiKey);
        }

        placesClient = Places.createClient(this);


        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        //autocompleteSupportFragment.setText("where to");


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
                        DatabaseHelper.setSecondLocation(new Location(currentLat, currentLng));
                        User newUser = new User();
                        newUser.setName("testing1");
                        Request request = new Request(new Location(), DatabaseHelper.getSecondLocation(),
                                newUser, new User(), 89.f);
                        RequestDataHelper.addNewRequest(request, listener);

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


//                                txtaddress.setText(address);
//                                txtlocality.setText(locality);
//                                txtsubLocality.setText(subLocality);
//                                txtstate.setText(state);
//                                txtcountry.setText(country);
//                                txtpostalCode.setText(postalCode);
//                                txtknownname.setText(knownname);
//                                txtphone.setText(phone);


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

    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag == RequestDataHelper.USER_REQ_TAG) {

        } else if (tag == RequestDataHelper.ALL_REQs_TAG) {
            if (requests.size() > 0) {
                //  always check if the return value is valid
                System.out.println("------------ active request obtained -----------");
            }
            else {
                System.out.println("------------ empty list obtained -----------");
            }
        } else if (tag == RequestDataHelper.SET_ACTIVE_TAG) {
            System.out.println("------------ request is set to active -----------");
            RequestDataHelper.queryAllOpenRequests( this);
            RequestDataHelper.queryUserRequest("new Driver", "driver", this);
            Toast.makeText(RiderSelectLocationActivity.this, "rider request updated to active successfully", Toast.LENGTH_SHORT).show();
        } else if (tag == RequestDataHelper.SET_PICKEDUP_TAG) {
            Toast.makeText(RiderSelectLocationActivity.this, "rider is picked up successfully", Toast.LENGTH_SHORT).show();
        } else if (tag == RequestDataHelper.CANCEL_REQ_TAG) {
            System.out.println("------------ request is deleted -----------");
            Toast.makeText(RiderSelectLocationActivity.this, "rider request deleted successfully", Toast.LENGTH_SHORT).show();
        } else if (tag == RequestDataHelper.COMPLETE_REQ_TAG) {
            System.out.println("------------ request is completed & deleted -----------");
            System.out.println("------------ new record is created -----------");
            Toast.makeText(RiderSelectLocationActivity.this, "rider request completed successfully", Toast.LENGTH_SHORT).show();
        } else if (tag == RequestDataHelper.ADD_REQ_TAG) {
            Toast.makeText(RiderSelectLocationActivity.this, "rider request added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActiveNotification(Request request) {
        System.out.println("------------- rider request updated to active -----------------");
        Toast.makeText(RiderSelectLocationActivity.this, "rider request updated to active by driver", Toast.LENGTH_SHORT).show();
    }

    @Override
    public  void onPickedUpNotification(Request request) {

    }

    @Override
    public void onCancelNotification() {

    }

    @Override
    public void onCompleteNotification() {}

    @Override
    public void onFailure(String errorMessage, String tag) {
        System.out.println("-----------" + errorMessage + "-----------");
        Toast.makeText(RiderSelectLocationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }


}

