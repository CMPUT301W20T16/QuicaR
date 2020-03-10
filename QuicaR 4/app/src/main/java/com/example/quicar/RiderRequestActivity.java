package com.example.quicar;



import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quicar.R;
import com.google.type.LatLng;

import java.io.IOException;
import java.util.Locale;

public class RiderRequestActivity extends BaseActivity {

    private EditText startLocation;

//    private EditText stopLocation;
//    private Button confirmButton;

    private LatLng pickupLocation;
    private LatLng destinationLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper.setCurrentMode("rider");
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
                    Toast.makeText(RiderRequestActivity.this, "need to choose destination!", Toast.LENGTH_SHORT);

                }
                else {
                    String current_address = findAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    if (current_address != null) {
                        Intent intent = new Intent(RiderRequestActivity.this, RiderSelectLocationActivity.class);
                        intent.putExtra("current pos", current_address);
                        intent.putExtra("current location", new Location(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                        //startActivityForResult(intent, 1);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RiderRequestActivity.this, "Cannot access current location", Toast.LENGTH_SHORT);
                    }
                }
            }
        });

    }

    // get address name in String from lat and long
    public String findAddress(double lat, double lng) {
        // set pick up location automatically as customer's current location
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        if (lat != 0 && lng != 0) {
            try {
                addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            if (address.length() != 0) {
                return address;
            }
        }
        return null;

    }




    /**
     *     database method

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
     */
}
