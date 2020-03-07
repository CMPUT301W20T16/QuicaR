package com.example.quicar;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicar.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

/**
 * Driver is able to browse
 */
public class DriverBrowsingActivity extends BaseActivity implements OnGetRequestDataListener {

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    private RecyclerView mRecyclerView;
    private RequestAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Request> requestList;

    private OnGetRequestDataListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_browsing, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_open_requests);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);


        requestList = new ArrayList<>();
        buildRecyclerView();

        RequestDataHelper.queryAllOpenRequests(this);

    }

    public void insertItem(int position) {
//        requestList.add(position, new Request(R.drawable.ic_android, "New Item At Position" + position, "This is Line 2"));
        mAdapter.notifyItemInserted(position);
    }

    public void removeItem(int position) {
        requestList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }


    public void buildRecyclerView() {
        mRecyclerView = (RecyclerView) linearLayout.findViewById(R.id.open_requests_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RequestAdapter(requestList);
        System.out.println("-------------recycler view build successful-----------");

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RequestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                System.out.println("clicked");
            }
        });
    }

    @Override
    public void onSuccess(Request request, ArrayList<Request> requests, String tag) {
        if (tag == RequestDataHelper.ALL_REQs_TAG) {
            if (requests.size() > 0) {
                //  always check if the return value is valid
                System.out.println("------------ active request obtained -----------");

                // update newly added open requests to RecyclerView adapter
                requestList.addAll(requests);
                mAdapter.notifyDataSetChanged();

            } else {
                System.out.println("------------ empty list obtained -----------");
            }

        }
    }

        @Override
        public void onActiveNotification (Request request){

        }

        @Override
        public void onPickedUpNotification (Request request){

        }

        @Override
        public void onCancelNotification () {

        }

        @Override
        public void onFailure (String errorMessage){

        }

}
