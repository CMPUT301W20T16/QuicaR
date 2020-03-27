package com.example.quicar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.entity.Request;
import com.example.font.Button_SF_Pro_Display_Medium;
import com.example.font.TextViewSFProDisplayRegular;
import com.example.listener.OnGetRequestDataListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;


/**
 * rider has successfully sent out a request
 * rider is waiting the system to pair them with a driver
 * rider will get a notification if the match is successful
 * rider is able to cancel ride before driver arrived at pick up location
 * (rider can only cancel ride in a reasonable amount of time)
 */

public class RiderWaitingRideActivity extends DrawRouteBaseActivity implements OnGetRequestDataListener {

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    TextView driverDistance;
    TextView driverName, driverRating, driverEmail, driverPhone, estimateFare, startAddress, endAddress;

    Button DetailButton;
    TextViewSFProDisplayRegular CallButton;
    TextViewSFProDisplayRegular EmailButton;
    Button_SF_Pro_Display_Medium CancelButton;
    Request currentRequest = null;


    /**
     * 问题：
     * 1.目前还不能更新bottom sheet的detail
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        Intent intent = getIntent();
//        currentRequest = (Request) intent.getSerializableExtra("current request");
        /** Added by Jeremy */
        //mRequest = currentRequest;
        /** End here */

        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_waiting_ride, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_ride_status);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        // set Buttons
        /**
         * havent implement driver detail
         */
//        DetailButton = linearLayout.findViewById(R.id.driver_detail_button);
        CallButton = linearLayout.findViewById(R.id.call_driver_button);
        EmailButton = linearLayout.findViewById(R.id.email_driver_button);
        CancelButton = linearLayout.findViewById(R.id.cancel_button);

        // set TextView
        driverName = linearLayout.findViewById(R.id.driver_name_tv);
        driverEmail = linearLayout.findViewById(R.id.driver_email_tv);
        driverPhone = linearLayout.findViewById(R.id.driver_phone_tv);
        driverRating = linearLayout.findViewById(R.id.driver_rating_tv);
        estimateFare = linearLayout.findViewById(R.id.estimate_fare);
        startAddress = linearLayout.findViewById(R.id.start_address);
        endAddress = linearLayout.findViewById(R.id.end_address);

        // get activated request from firebase
        RequestDataHelper.getInstance().setOnNotifyListener(this);

        currentRequest = (Request) DatabaseHelper.getInstance().getUserState().getCurrentRequest();

        //set Text View
        driverName.setText(currentRequest.getDriver().getName());
        /**
         * Prob:
         * Driver doesn't have Email or Phone attributes
         */
        driverEmail.setText(currentRequest.getDriver().getAccountInfo().getEmail());
        driverPhone.setText(currentRequest.getDriver().getAccountInfo().getPhone());
        driverRating.setText(currentRequest.getDriver().getAccountInfo().getDriverInfo().getRating().toString());
        estimateFare.setText(currentRequest.getEstimatedCost().toString());

        // set on click listener for buttons
        // transfer to default dial page
        CallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + driverPhone.getText().toString()));
                startActivity(intent);

            }
        });

        //send email需要拿到user的email，格式类似上面打电话

        // if user tries to cancel the ride while driver is on their way
        // call cancelRequest so ride will be deleted in the database
        // user back to the main screen
        /**
         * 问题：1.
         * 还不能确定允许cancel的时间
         */
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRequest != null) {
                    RequestDataHelper
                            .getInstance()
                            .cancelRequest(mRequest.getRid(), RiderWaitingRideActivity.this);
                }
            }
        });


    }

    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag.equals(RequestDataHelper.CANCEL_REQ_TAG)) {
            Intent intent = new Intent(RiderWaitingRideActivity.this, RiderRequestActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * execute following when an request becomes active (matches a driver successfully)
     * @param request
     */
    @Override
    public void onActiveNotification(Request request) {
    }

    /**
     * execute when the correspond driver cliks picked up rider
     * @param request
     */
    @Override
    public void onPickedUpNotification(Request request) {
        //System.out.println("------------- rider has been picked up -----------------");

        Toast.makeText(RiderWaitingRideActivity.this, "rider is picked up by driver", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(RiderWaitingRideActivity.this, RiderOnGoingRequestActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public  void onArrivedNotification(Request request) {

    }

    @Override
    public void onCancelNotification() {
        System.out.println("------------- rider has been canceled -----------------");
        Toast.makeText(RiderWaitingRideActivity.this, "rider is canceled by driver", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onCompleteNotification() {

    }

    @Override
    public void onFailure(String errorMessage, String tag) {

    }
}
