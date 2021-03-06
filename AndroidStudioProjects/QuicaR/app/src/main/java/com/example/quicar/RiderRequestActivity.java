package com.example.quicar;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.font.Button_SF_Pro_Display_Medium;
import com.example.quicar.R;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.type.LatLng;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.example.datahelper.DatabaseHelper;
import com.example.entity.Location;


public class RiderRequestActivity extends BaseActivity {

    private Button_SF_Pro_Display_Medium startLocation;

//    private EditText stopLocation;
//    private Button confirmButton;

    private LatLng pickupLocation;
    private LatLng destinationLocation;


    /**
     * activity displayed as soon as user logged in as a default activity
     * rider in this activity can go to selection act which allows to choose start and end
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper.getInstance().setCurrentMode("rider");

        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_request, frameLayout);

        setTitle("rider map");


        // set up EditText and button
        startLocation = findViewById(R.id.start_location);




        // if user trying to enter pick up location
        // the program will automatically generate user's current location
        // transfer to a new page where user can enter or select destination from historical destinations
        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLastLocation == null){
                    Toast.makeText(RiderRequestActivity.this, "Cannot access current location", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RiderRequestActivity.this,RiderRequestActivity.class);
                    startActivity(intent);
                    finish();
                }
                String current_address = findAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                //String current_featureName = findFeatureName(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                if (current_address != null && DatabaseHelper.getInstance().getCurrentUser().getAccountInfo().getWallet().getBalance() > 0) {
                    Location currentLocation = new Location(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    currentLocation.setAddressName(current_address);
                    currentLocation.setName(current_address);
                    Intent intent = new Intent(RiderRequestActivity.this, RiderSelectLocationActivity.class);
                    intent.putExtra("current pos", current_address);
                    intent.putExtra("current location", currentLocation);
                    //startActivityForResult(intent, 1);
                    startActivity(intent);
                }
                else if(current_address == null){
                    Toast.makeText(RiderRequestActivity.this, "Cannot access current location", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RiderRequestActivity.this, "Your charge pocket doesn't have enough money, please recharge", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    /**
     * helper function for address of selected location
     * @param lat
     * @param lng
     * @return
     */

    // get address name in String from lat and long




}
