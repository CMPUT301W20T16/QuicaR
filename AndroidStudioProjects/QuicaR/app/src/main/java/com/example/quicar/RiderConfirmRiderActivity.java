package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import com.example.quicar_mapview.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class RiderConfirmRiderActivity extends BaseActivity {

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;


    Button confirmButton;
    Button cancelButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_rider_confirm_ride, frameLayout);


        //set bottom sheet
        linearLayout = (LinearLayout)findViewById(R.id.bottom_sheet_order_detail);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        // set Buttons
        confirmButton = linearLayout.findViewById(R.id.confirm_button);
        cancelButton = linearLayout.findViewById(R.id.cancel_button);


        //get data from intent, i.e., current address
        Intent intent = getIntent();
        // 接受location
//        final String current_address = (Location) intent.getSerializableExtra("start position");
//        final String stop_address = (String) intent.getSerializableExtra("stop position");


//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Request request = new Request;
//                request.setStart(current_location);
//                request.setDestination(stop_location);
//
//                DatabaseHelper.addRequest(request);

//                linearLayout = (LinearLayout)findViewById(R.id.bottom_sheet_loading);
//                bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
//            }
//        });






    }
}
