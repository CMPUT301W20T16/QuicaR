package com.example.quicar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.quicar_mapview.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_waiting_ride, frameLayout);


        linearLayout = (LinearLayout)findViewById(R.id.bottom_sheet_ride_status);
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

        //send email需要拿到user的email，先放后面


    }
}
