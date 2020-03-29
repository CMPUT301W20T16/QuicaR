package com.example.quicar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.quicar.R;
import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RecordDataHelper;
import com.example.datahelper.UserStateDataHelper;
import com.example.entity.Location;
import com.example.listener.OnGetRecordDataListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

public class RiderSelectLocationActivity extends AppCompatActivity implements OnGetRecordDataListener {
    private EditText pickUp;
    private EditText destination;
    private Button confirmButton;

    private int currentPosition;


    String address,adminiArea = null,phone;
    //    TextView txtaddress, txtlocality, txtsubLocality, txtstate,txtpostalCode,txtcountry,txtknownname,txtphone;
    private double currentLat,currentLng;
    private Location start_location, end_location;


    Marker marker;
    PlacesClient placesClient;

    private RecyclerView mRecyclerView;
    private LocationAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Location> locationList;

    ImageView swapIcon;
    TextView start, end;

    AutocompleteSupportFragment pickUpAutoComplete, destinationAutoComplete;


    /**
     * activity for riders to select start and destination
     * use google autocomplete fragment to help
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_select_location);

        // add back button on action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.paymentBackground)));



        String apiKey= "AIzaSyCyECZAmZ2NxQz10Qijm-ngagqBdHJblzk";
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),apiKey);
        }

        placesClient = Places.createClient(this);


        start_location = new Location();
        end_location = new Location();

        /**
         * 问题：history location得在点击auto complete fragment之后才显示
         */
        locationList = new ArrayList<>();
        RecordDataHelper
                .getInstance()
                .queryHistoryLocation(DatabaseHelper.getInstance().getCurrentUserName(), 10, this);
        buildRecyclerView();



        pickUpAutoComplete =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.pick_up);

        destinationAutoComplete =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.destination);

        swapIcon = findViewById(R.id.swap_icon);
        start = findViewById(R.id.start_tv);
        end = findViewById(R.id.end_tv);

        //get data from intent, i.e., current address
        Intent intent = getIntent();
        String pick_up_address = (String) intent.getSerializableExtra("current pos");
        start_location = (Location) intent.getSerializableExtra("current location");

        /*
        Added something here to prevent error when re-selecting new location after invalid route
         */

        if (start_location == null) start_location = new Location();

        /* End here */

//        pickUpAutoComplete.setHint(pick_up_address);
//        destinationAutoComplete.setHint("Select Destination");
        start.setText(pick_up_address);

        onCreateAutoCompletion(pickUpAutoComplete, start_location, true);
        onCreateAutoCompletion(destinationAutoComplete, end_location, false);

        //enable user to swap start and end address
        swapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Location temp = new Location();
                Location temp = start_location;
                start_location = end_location;
                end_location = temp;


                start.setText(start_location.getAddressName());
                end.setText(end_location.getAddressName());
            }
        });


    }

    /**
     * helper function makes user more convenient when browsing result items
     */
    public void buildRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.history_loc_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new LocationAdapter(locationList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // if user clicked on of the past address
        mAdapter.setOnItemClickListener(new LocationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                currentPosition = position;
                /**
                 * 问题：
                 * 1.没法确定选择的location是start还是end location
                 */
                start_location = (Location) locationList.get(position);
                System.out.println(start_location.getLon() + start_location.getLat());


            }
        });
    }

    /**
     * execute as soon as they click the auto complete fragment and begin to enter
     * @param autocompleteSupportFragment
     * @param location
     */
    public void onCreateAutoCompletion(final AutocompleteSupportFragment autocompleteSupportFragment, final Location location, boolean isStart) {
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

                        location.setLat(currentLat);
                        location.setLon(currentLng);
//

                        phone = place.getPhoneNumber();
                        address = place.getAddress();


                        Geocoder gcd =  new Geocoder(getBaseContext(), Locale.getDefault());

                        List<Address> addresses;

                        try {
                            addresses = gcd.getFromLocation(currentLat,currentLng,1);
                            if (addresses.size()> 0){
                                String current_adminiArea = addresses.get(0).getAdminArea();
                                if (adminiArea != null){
                                    if(!current_adminiArea.equals(adminiArea)){
                                        Toast.makeText(RiderSelectLocationActivity.this, "Start place and end place must be within the same administration area!", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(RiderSelectLocationActivity.this,RiderRequestActivity.class);
                                        startActivity(intent);



                                    }
                                }
                                else{
                                    adminiArea = current_adminiArea;
                                }



                                //set address in Location object
                                location.setAddressName(place.getAddress());
                                //System.out.println(destinationAutoComplete);
                                if (isStart) {
                                    start.setText(place.getAddress());
                                } else {
                                    end.setText(place.getAddress());
                                }


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

    /**
     * execute when an result item of google autocomplete fragment is clicked
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm_button:
                // check if inputs are left blank
                if (start_location.getLat() == null || end_location.getLat() == null){
                    Toast.makeText(this, "One or more fields is empty!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                Intent intent = new Intent(RiderSelectLocationActivity.this, RiderConfirmRiderActivity.class);
                intent.putExtra("start location", start_location);
                intent.putExtra("end location", end_location);

                /* added for user state */
                DatabaseHelper.getInstance().getUserState().setOnConfirm(true);
                UserStateDataHelper.getInstance().recordState();

                startActivity(intent);
                return true;

            default:
                // if we got here, the user's action was not recognized.
                // invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onSuccess(ArrayList<Location> history) {
        for (Location loc: history) {
            System.out.println(loc.getLat() + " " + loc.getLon() + " --------- history here");
            locationList.add(loc);


        }
    }

    @Override
    public void onFailure(String errorMessage) {

    }

}