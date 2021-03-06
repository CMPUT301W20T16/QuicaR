package com.example.quicar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.datahelper.UserStateDataHelper;
import com.example.entity.Request;
import com.example.listener.OnGetRequestDataListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

/**
 * Driver is able to browse
 */
public class DriverBrowsingActivity extends BaseActivity implements OnGetRequestDataListener, DriverAcceptRideDialogue.OnFragmentInteractionListener {

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    private RecyclerView mRecyclerView;
    private RequestAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Request> requestList;
    private MarkerOptions start;

    private int currentPosition;

    final private String PROVİDER = LocationManager.GPS_PROVIDER;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper.getInstance().setCurrentMode("driver");
        UserStateDataHelper.getInstance().recordState();

        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_browsing, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_open_requests);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        RequestDataHelper.getInstance().setOnNotifyListener(this);

        /* added by Jeremy */
        RequestDataHelper.getInstance().queryAllOpenRequests(this);

        requestList = new ArrayList<>();
        buildRecyclerView();


        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = mLocationManager.getLastKnownLocation(PROVİDER);




    }

    /**
     * build a RecyclerView for later use
     */
    public void buildRecyclerView() {
        mRecyclerView = (RecyclerView) linearLayout.findViewById(R.id.open_requests_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RequestAdapter(requestList);
//        System.out.println("-------------recycler view build successful-----------");

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new RequestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                System.out.println("clicked");
                currentPosition = position;
//                Request request = (Request)requestList.get(position);

                /**
                 * get driver
                 */
                new DriverAcceptRideDialogue().show(getSupportFragmentManager(), "DELETE");

            }
        });
    }


    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag.equals(RequestDataHelper.ALL_REQs_TAG)) {

            /* Edited by Jeremy */


                //  always check if the return value is valid
                System.out.println("------------ open request obtained -----------");

                // update newly added open requests to RecyclerView adapter
                requestList.clear();
                requestList.addAll(requests);

                mAdapter.notifyDataSetChanged();

            /* End here */

        } else if (tag.equals(RequestDataHelper.SET_ACTIVE_TAG)) {
            System.out.println("------------ request is set to active -----------");
//            RequestDataHelper.queryAllOpenRequests(this);
//            RequestDataHelper
//                    .getInstance()
//                    .queryUserRequest(DatabaseHelper.getInstance().getCurrentUserName(),
//                            "driver", this);
            Toast.makeText(this, "rider request updated to active successfully",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(DriverBrowsingActivity.this, DriverPickUpActivity.class);
//            intent.putExtra("current accepted request", request);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        if (start != null) {
            start.position(latLng);
        }
        else {
            start = new MarkerOptions().position(latLng).title("origin");

        }

    }

    @Override
    public void onActiveNotification (Request request){

    }

    @Override
    public void onPickedUpNotification (Request request){

    }

    @Override
    public  void onArrivedNotification(Request request) {

    }

    @Override
    public void onCancelNotification () {

    }

    @Override
    public void onCompleteNotification() {

    }

    @Override
        public void onFailure (String errorMessage, String tag){
            System.out.println("-----------" + errorMessage + "-----------" + tag);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }

    @Override
    public void onOkPressed() {
        //activate selected request
        Request request = (Request)requestList.get(currentPosition);
        Location riderPickUP = new Location("rider pick up");
        riderPickUP.setLatitude(request.getStart().getLat());
        riderPickUP.setLongitude(request.getStart().getLon());

        float distance = mLastLocation.distanceTo(riderPickUP);
        if(distance > 50000){
            Toast.makeText(this, "Too far away! please select another one!",
                    Toast.LENGTH_SHORT).show();

        }
        else {


            RequestDataHelper
                    .getInstance()
                    .setRequestActive(request.getRid(), DatabaseHelper.getInstance().getCurrentUser(),
                            request.getEstimatedCost(), DriverBrowsingActivity.this);
        }

    }
}
