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

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;


/**
 * rider has successfully sent out a request
 * rider is waiting the system to pair them with a driver
 * rider will get a notification if the match is successful
 * rider is able to cancel ride before driver arrived at pick up location
 * (rider can only cancel ride in a reasonable amount of time)
 */

public class RiderWaitingRideActivity extends DrawRouteBaseActivity implements OnGetRequestDataListener{

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;

    TextView driverDistance;
    TextView driverName;
    TextView driverEmail;
    TextView driverPhone;

    Button DetailButton;
    Button CallButton;
    Button EmailButton;
    Button CancelButton;

    /**
     * 问题：
     * 1.目前还不能更新bottom sheet的detail
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_waiting_ride, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_ride_status);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        // set Buttons
        DetailButton = linearLayout.findViewById(R.id.driver_detail_button);
        CallButton = linearLayout.findViewById(R.id.call_driver_button);
        EmailButton = linearLayout.findViewById(R.id.email_driver_button);
        CancelButton = linearLayout.findViewById(R.id.cancel_button);

        // set TextView
        driverName = linearLayout.findViewById(R.id.driver_name_tv);
        driverEmail = linearLayout.findViewById(R.id.driver_email_tv);
        driverPhone = linearLayout.findViewById(R.id.driver_phone_tv);
        driverDistance = linearLayout.findViewById(R.id.driver_distance_tv);

        // get activated request from firebase
        RequestDataHelper.getInstance().setOnNotifyListener(this);
//        RequestDataHelper.getInstance().queryUserRequest(DatabaseHelper.getInstance().getCurrentUserName(), "rider", this);


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
                if (mRequest != null) {
                    RequestDataHelper
                            .getInstance()
                            .cancelRequest(mRequest.getRid(), RiderWaitingRideActivity.this);
                }
                Intent intent = new Intent(RiderWaitingRideActivity.this, RiderRequestActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
    }

    @Override
    public void onActiveNotification(Request request) {

    }

    @Override
    public void onPickedUpNotification(Request request) {
        System.out.println("------------- rider has been picked up -----------------");
        DatabaseHelper.getInstance().sendPopUpNotification("Notification test", "Rider is being picked up");
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
