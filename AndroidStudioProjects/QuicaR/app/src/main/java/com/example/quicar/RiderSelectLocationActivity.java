package com.example.quicar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.quicar_mapview.R;
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

public class RiderSelectLocationActivity extends AppCompatActivity{
    private EditText pickUp;
    private EditText destination;
    private Button confirmButton;


    String address,locality,subLocality,state,postalCode,country,knownname,phone;
//    TextView txtaddress, txtlocality, txtsubLocality, txtstate,txtpostalCode,txtcountry,txtknownname,txtphone;
    private double currentLat,currentLng;
    private Location start_location, end_location;

//    private GoogleMap mMap;

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


        String apiKey= "AIzaSyCyECZAmZ2NxQz10Qijm-ngagqBdHJblzk";
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),apiKey);
        }

        placesClient = Places.createClient(this);


        start_location = new Location();
        end_location = new Location();



        final AutocompleteSupportFragment pickUpAutoComplete =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.pick_up);

        final AutocompleteSupportFragment destinationAutoComplete =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.destination);

        //get data from intent, i.e., current address
        Intent intent = getIntent();
        String pick_up_address = (String) intent.getSerializableExtra("current pos");
        start_location = (Location) intent.getSerializableExtra("current location");

        pickUpAutoComplete.setText(pick_up_address);
        destinationAutoComplete.setText("MY DESTINATION");
        onCreateAutoCompletion(pickUpAutoComplete, start_location);
        onCreateAutoCompletion(destinationAutoComplete, end_location);


    }


    public void onCreateAutoCompletion(AutocompleteSupportFragment autocompleteSupportFragment, final Location location) {
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
                        //如果user没有更新start lcoation的情况
//                        if (currentLat == 0.0f || currentLng == 0.0f){
//                            return;
//                        }
                        location.setLat(currentLat);
                        location.setLon(currentLng);
//                        DatabaseHelper.setSecondLocation(new Location(currentLat, currentLng));
//                        User newUser = new User();
//                        newUser.setName("testing1");
//                        Request request = new Request(new Location(), DatabaseHelper.getSecondLocation(),
//                                newUser, new User(), 89.f);
//                        RequestDataHelper.addNewRequest(request, listener);

                        phone = place.getPhoneNumber();
                        address = place.getAddress();

//                        if ( marker != null){
//                            marker.remove();
//                        }

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


    /**
     * listview包含近10(?)个rider去过的location
*/


    // Used to add check bar on top right of the app bar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_confirm, menu);
        return true;
    }


     // When user clicks on the tick button, this function checks if any of the entries are left blank.
    // If so, a Toast object is used to notify that the
    // user left a field empty. Otherwise, we add the measurement.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm_button:
                // check if inputs are left blank
                if (start_location == null || end_location == null){
                    Toast.makeText(this, "One or more fields is empty!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                Intent intent = new Intent(RiderSelectLocationActivity.this, RiderConfirmRiderActivity.class);
                intent.putExtra("start location", start_location);
                intent.putExtra("end location", end_location);
                startActivity(intent);
                return true;

            default:
                // if we got here, the user's action was not recognized.
                // invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


}

