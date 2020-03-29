package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.entity.Request;
import com.example.listener.OnGetRequestDataListener;
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

    private int currentPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper.getInstance().setCurrentMode("driver");



        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_browsing, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_open_requests);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        RequestDataHelper.getInstance().setOnNotifyListener(this);

        /* added by Jeremy */
        RequestDataHelper.getInstance().queryAllOpenRequests(this);

        System.out.println("-------------current user name: " + DatabaseHelper.getInstance().getCurrentUserName());

        requestList = new ArrayList<>();
        buildRecyclerView();


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
            RequestDataHelper
                    .getInstance()
                    .queryUserRequest(DatabaseHelper.getInstance().getCurrentUserName(),
                            "driver", this);
            Toast.makeText(this, "rider request updated to active successfully",
                    Toast.LENGTH_SHORT).show();
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
//        User newDriver = new User();
//        newDriver.setName("new Driver");
        // testing
//                DatabaseHelper.setCurrentUserName("Name");
        Request request = requestList.get(currentPosition);
        RequestDataHelper
                .getInstance()
                .setRequestActive(request.getRid(), DatabaseHelper.getInstance().getCurrentUser(),
                        request.getEstimatedCost(), DriverBrowsingActivity.this);
        Intent intent = new Intent(DriverBrowsingActivity.this, DriverPickUpActivity.class);
        intent.putExtra("current accepted request", request);
        startActivity(intent);
        finish();
    }
}
