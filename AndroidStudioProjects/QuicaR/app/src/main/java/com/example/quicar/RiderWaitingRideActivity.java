package com.example.quicar;

import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

import static com.example.quicar.App.CHANNEL_1_ID;


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


    private NotificationManagerCompat notificationManager;


    /**
     * 问题：
     * 1.目前只有一个default bottom sheet，没法区分是否被接单
     * 1.目前还不能更新bottom sheet的detail
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_waiting_ride, frameLayout);

        notificationManager = NotificationManagerCompat.from(this);

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
        RequestDataHelper.setOnNotifyListener(this);
//        RequestDataHelper.queryUserRequest(DatabaseHelper.getCurrentUserName(), "rider", this);


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
         * 问题：还不能确定允许cancel的时间
         */
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRequest != null) {
                    RequestDataHelper.cancelRequest(mRequest.getRid(), RiderWaitingRideActivity.this);
                }
                Intent intent = new Intent(RiderWaitingRideActivity.this, RiderRequestActivity.class);
                startActivity(intent);

            }
        });


    }

    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
//        if (tag == RequestDataHelper.USER_REQ_TAG) {
//            if (requests.size() > 0) {
//                //  always check if the return value is valid
//                System.out.println("------------ all open request obtained -----------");
//                for (Request request: requests) {
//                    if (request.getAccepted()) {
//                        mRequest = request;
//                    }
//                }
//            }
//            else {
//                System.out.println("------------ empty list obtained -----------");
//            }
//        } else if (tag == RequestDataHelper.CANCEL_REQ_TAG) {
//            System.out.println("------------- rider request has been canceled-----------------");
//            Toast.makeText(RiderWaitingRideActivity.this, "rider request canceled successfully", Toast.LENGTH_SHORT).show();
//
//        }
    }

    @Override
    public void onActiveNotification(Request request) {
        System.out.println("------------- rider request updated to active -----------------");
        DatabaseHelper.sendPopUpNotification("Notification test", "Ride is being accepted");
        mRequest = request;
        Toast.makeText(RiderWaitingRideActivity.this, "rider request updated to active by driver", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPickedUpNotification(Request request) {
        System.out.println("------------- rider has been picked up -----------------");
        Toast.makeText(RiderWaitingRideActivity.this, "rider is picked up by driver", Toast.LENGTH_SHORT).show();

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
