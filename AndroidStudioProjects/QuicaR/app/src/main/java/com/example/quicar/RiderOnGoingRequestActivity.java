package com.example.quicar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.entity.Request;
import com.example.font.Button_SF_Pro_Display_Medium;
import com.example.font.TextViewSFProDisplayMedium;
import com.example.font.TextViewSFProDisplayRegular;
import com.example.listener.OnGetRequestDataListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

/**
 * When rider is picked up by driver
 * rider is able to see their current location on google map
 */
public class RiderOnGoingRequestActivity extends DrawRouteBaseActivity implements OnGetRequestDataListener {

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    TextViewSFProDisplayMedium driverName, driverRating;
    TextViewSFProDisplayRegular driverEmail, driverPhone, estimateFare, startAddress, endAddress;
    Button_SF_Pro_Display_Medium CancelButton;

    Request currentRequest = null;


    /**
     * Prob:
     * 1. Need to draw route? Or just display rider(driver)'s current location?
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_on_going_request, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_rider_on_going_request);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        RequestDataHelper.getInstance().setOnNotifyListener(this);
        currentRequest = DatabaseHelper.getInstance().getUserState().getCurrentRequest();

        // set Buttons
//        DetailButton = linearLayout.findViewById(R.id.driver_detail_button);
        CancelButton = linearLayout.findViewById(R.id.cancel_button);

//        // set TextView
        driverName = linearLayout.findViewById(R.id.driver_name_tv);
        driverEmail = linearLayout.findViewById(R.id.driver_email_tv);
        driverPhone = linearLayout.findViewById(R.id.driver_phone_tv);
        driverRating = linearLayout.findViewById(R.id.driver_rating_tv);
        estimateFare = linearLayout.findViewById(R.id.estimate_fare);
        startAddress = linearLayout.findViewById(R.id.start_address);
        endAddress = linearLayout.findViewById(R.id.end_address);
//
//        //set Text View
        driverName.setText(currentRequest.getDriver().getName());
//        driverName.setText("TYYIJOKM!!!!!!!!!!!!!!!!!");
//        driverEmail.setText("!!!!!!!!!!!!!!!!!!");

        driverEmail.setText(currentRequest.getDriver().getAccountInfo().getEmail());
        driverPhone.setText(currentRequest.getDriver().getAccountInfo().getPhone());
        driverRating.setText(currentRequest.getDriver().getAccountInfo().getDriverInfo().getRating().toString());



    }


    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {

    }

    @Override
    public void onActiveNotification(Request request) {

    }

    @Override
    public void onPickedUpNotification(Request request) {

    }

    /**
     * execute following when the state is arrived the destination
     * @param request
     */

    @Override
    public void onArrivedNotification(Request request) {
        System.out.println("------------- ride is arrived -----------------");
        Toast.makeText(RiderOnGoingRequestActivity.this, "rider is picked up by driver", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RiderOnGoingRequestActivity.this, RiderReviewActivity.class);
        startActivity(intent);
        finish();



    }

    @Override
    public void onCancelNotification() {

    }

    @Override
    public void onCompleteNotification() {

    }

    @Override
    public void onFailure(String errorMessage, String tag) {

    }
}
