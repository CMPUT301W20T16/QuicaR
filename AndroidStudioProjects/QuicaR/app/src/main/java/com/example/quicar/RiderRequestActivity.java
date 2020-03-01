package com.example.quicar_mapview.Activity.RiderActivity;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.quicar_mapview.Activity.BaseActivity;
import com.example.quicar_mapview.Class.Request;
import com.example.quicar_mapview.OnGetDataListener;
import com.example.quicar_mapview.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.type.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class RiderRequestActivity extends BaseActivity implements OnGetDataListener {

    private EditText startLocation;
    private EditText stopLocation;
//    private Button confirmButton;

    private LatLng pickupLocation;
    private LatLng destinationLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_request, frameLayout);

        setTitle("rider map");

        // set up EditText and button
        startLocation = findViewById(R.id.start_location);
//        stopLocation = findViewById(R.id.stop_location);
//        confirmButton = findViewById(R.id.confirm_button);

        // if customer pressed confirm button
        // a new request is made and stored to Firebase database
//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //DatabaseHelper.getCurrentUser()
////                setPickupLocationDefault();
//
//            }
//        });

        // if user trying to enter pick up location
        // the program will automatically generate user's current location
        // transfer to a new page where user can enter or select destination from historical destinations
        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current_address = setPickupLocationDefault();
                if (current_address != null) {
                    Intent intent = new Intent(RiderRequestActivity.this, RiderSelectLocationActivity.class);
                    intent.putExtra("current pos", current_address);
                    startActivityForResult(intent, 1);
                }
                else {
                    Toast.makeText(RiderRequestActivity.this, "Cannot access current location", Toast.LENGTH_SHORT);
                }
            }
        });

    }



    /**
     *  request method
     */

    public String setPickupLocationDefault() {
        // set pick up location automatically as customer's current location
        geocoder = new Geocoder(RiderRequestActivity.this, Locale.getDefault());

        if (mLastLocation != null) {
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            if (address.length() != 0) {
//                pickUp.setText(address, TextView.BufferType.EDITABLE);
                return address;
            }
        }
        return null;

    }

    /**
     *     database method
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSuccessRiderOpenRequest(Request request) {
        if (request != null) {
            //  always check if the return value is valid
            System.out.println("---------------" + request.getRider().getName() + "---------------");
        }
    }

    @Override
    public void onSuccessAllOpenRequests(ArrayList<Request> requests) {
        if (requests.size() > 0) {
            //  always check if the return value is valid
            System.out.println("------------ active request obtained -----------");
        }
        else {
            System.out.println("------------ empty list obtained -----------");
        }
    }

    @Override
    public void onSuccessDriverActiveRequest(Request request) {
        if (request != null) {
            //  always check if the return value is valid
            System.out.println("---------------" + request.getDriver().getName() + "---------------");
        }
    }


    @Override
    public void onSuccessSetActive() {
        System.out.println("------------ request is set to active -----------");
//        DatabaseHelper.queryAllOpenRequests(new Location(), this);
//        DatabaseHelper.queryDriverActiveRequest("new Driver", this);
    }

    @Override
    public void onSuccessDelete() {
        System.out.println("------------ request is deleted -----------");
    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("-----------" + errorMessage + "-----------");

    }

}
