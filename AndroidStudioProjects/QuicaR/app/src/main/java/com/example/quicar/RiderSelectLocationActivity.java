package com.example.quicar_mapview.Activity.RiderActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quicar_mapview.R;

public class RiderSelectLocationActivity extends AppCompatActivity {
    private EditText pickUp;
    private EditText destination;
    private Button confirmButton;

    String address,locality,subLocality,state,postalCode,country,knownname,phone;
    TextView txtaddress, txtlocality, txtsubLocality, txtstate,txtpostalCode,txtcountry,txtknownname,txtphone;
    private double currentLat,currentLng;


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
        String current_address = (String) intent.getSerializableExtra("current pos");
//
//        pickUp = findViewById(R.id.pick_up);
//        destination = findViewById(R.id.destination);
//
//        pickUp.setText(current_address, TextView.BufferType.EDITABLE);
//
//        final String destination_location_input = destination.getText().toString();


        /**
         * autocompletion

        txtaddress = findViewById(R.id.address);
        txtlocality = findViewById(R.id.locality);
        txtsubLocality = findViewById(R.id.subLocality);
        txtstate = findViewById(R.id.state);
        txtpostalCode = findViewById(R.id.postalCode);
        txtcountry = findViewById(R.id.country);
        txtknownname = findViewById(R.id.knownname);
        txtphone = findViewById(R.id.phone);


        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


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

//                        if ( marker != null){
//                            marker.remove();
//                        }
//
//                        mMap.clear();
//                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.5f ),null);


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
         */




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
}
