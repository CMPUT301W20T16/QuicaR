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
        // NOT IMPLEMENTED: time out
        // call cancelRequest so ride will be deleted in the database
        // user back to the main screen
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

//        // used to send one notification
//        public void sendOnChannel1(View v) {
//            String title = "Get Ready!";
//            String message = "Your driver is on the way!";
//
//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
//                    .setSmallIcon(R.drawable.ic_cancel)
//                    .setContentTitle(title)
//                    .setContentText(message)
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .build();
//
//            notificationManager.notify(1, notification);
//        }

//    /**
//     * Draw route methods
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        if (start != null && destination != null) {
//            mMap.addMarker(start);
//            mMap.addMarker(destination);
//            showAllMarkers();
//        }
//    }
//
//    public void showAllMarkers() {
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//        for (MarkerOptions m : markerOptionsList) {
//            builder.include(m.getPosition());
//
//        }
//        LatLngBounds bounds = builder.build();
//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = getResources().getDisplayMetrics().heightPixels;
//        int padding = (int) (width * 0.30);
//
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
//        mMap.animateCamera(cu);
//
//    }
//
//
//
//    public String getUrl(LatLng origin, LatLng dest, String directionMode) {
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//        String mode = "mode=" + directionMode;
//        String parameter = str_origin + "&" + str_dest + "&" + mode;
//        String format = "json";
//        String url = "https://maps.googleapis.com/maps/api/directions/" + format + "?"
//                + parameter + "&key=AIzaSyC2x1BCzgthK4_jfvqjmn6_uyscCiKSc34";
//
//
//        return url;
//
//    }
//
//
//    @Override
//    public void onTaskDone(Object... values) {
//        if (currentPolyline != null)
//            currentPolyline.remove();
//
//        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
//    }


    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag == RequestDataHelper.CANCEL_REQ_TAG) {
            System.out.println("------------- rider request has been canceled-----------------");
            Toast.makeText(RiderWaitingRideActivity.this, "rider request canceled successfully", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onActiveNotification(Request request) {
        System.out.println("------------- rider request updated to active -----------------");
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
