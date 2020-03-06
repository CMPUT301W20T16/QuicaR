package com.example.quicar;

import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import static com.example.quicar.App.CHANNEL_1_ID;


/**
 * rider has successfully sent out a request
 * rider is waiting the system to pair them with a driver
 * rider will get a notification if the match is successful
 * rider is able to cancel ride before driver arrived at pick up location
 * (rider can only cancel ride in a reasonable amount of time)
 */

public class RiderWaitingRideActivity extends BaseActivity {

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

        // set TextView
        driverName = linearLayout.findViewById(R.id.driver_name_tv);
        driverEmail = linearLayout.findViewById(R.id.driver_email_tv);
        driverPhone = linearLayout.findViewById(R.id.driver_phone_tv);
        driverDistance = linearLayout.findViewById(R.id.driver_distance_tv);


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

        /**
         * if successfully matched, call sendOnChannel1
         */

    }

        // used to send one notification
        public void sendOnChannel1(View v) {
            String title = "Get Ready!";
            String message = "Your driver is on the way!";

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_cancel)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();

            notificationManager.notify(1, notification);
        }




}
