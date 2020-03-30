package com.example.quicar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.entity.Request;
import com.example.font.Button_SF_Pro_Display_Medium;
import com.example.font.TextViewSFProDisplayLight;
import com.example.font.TextViewSFProDisplayMedium;
import com.example.font.TextViewSFProDisplayRegular;
import com.example.listener.OnGetRequestDataListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.io.IOException;
import java.sql.Driver;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DriverOnGoingActivity extends DrawRouteBaseActivity implements OnGetRequestDataListener {

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;
    private DirectionsResult directionsResult;

    TextViewSFProDisplayRegular riderEmail, riderPhone, startAddress, endAddress;
    TextViewSFProDisplayMedium riderName;
    Button_SF_Pro_Display_Medium confirmButton;
    TextViewSFProDisplayRegular callButton, emailButton;
    Request currentRequest = null;
    ImageView qrCode;
    /**
     * when going to this activity, following is executed automatically
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set up view
        navigationView.inflateMenu(R.menu.drawer_menu_driver);
        View rootView = getLayoutInflater().inflate(R.layout.activity_driver_on_going, frameLayout);

        linearLayout = (LinearLayout) findViewById(R.id.bottom_sheet_driver_on_going);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        //set up firebase connection
        RequestDataHelper.getInstance().setOnNotifyListener(this);
        /**
         * current request cannot be updated at driver end
         */
        currentRequest = DatabaseHelper.getInstance().getUserState().getCurrentRequest();



        //set up textView and buttons
        confirmButton = linearLayout.findViewById(R.id.confirm_button);
        riderEmail = linearLayout.findViewById(R.id.userEmail_textView);
        riderPhone = linearLayout.findViewById(R.id.userPhone_textView);
        riderName = linearLayout.findViewById(R.id.userName_textView);
        startAddress = linearLayout.findViewById(R.id.start_address);
        endAddress = linearLayout.findViewById(R.id.end_address);

        startAddress.setText(currentRequest.getStartAddrName());
        endAddress.setText(currentRequest.getDestAddrName());
        riderEmail.setText(currentRequest.getRider().getAccountInfo().getEmail());
        riderPhone.setText(currentRequest.getRider().getAccountInfo().getPhone());
        riderName.setText(currentRequest.getRider().getName());


        start_location = currentRequest.getStart();
        end_location = currentRequest.getDestination();


        start = new MarkerOptions().position(new LatLng(start_location.getLat(), start_location.getLon())).title("origin");
        destination = new MarkerOptions().position(new LatLng(end_location.getLat(), end_location.getLon())).title("destination");

        markerOptionsList.add(start);
        markerOptionsList.add(destination);


        DateTime now = new DateTime();
        String start_address = start_location.getAddressName();
        String end_address = end_location.getAddressName();
        try {
            //GeoApiContext geoApiContext = getGeoContext();
            directionsResult = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(start_address)
                    .destination(end_address).departureTime(Instant.now())
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentRequest == null)
                    return;

                System.out.println("set arrived----------");

                RequestDataHelper
                        .getInstance()
                        .setRequestArrived(currentRequest.getRid(),
                                DriverOnGoingActivity.this);

            }
        });
    }

    protected void showQRBottom() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DriverOnGoingActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.driver_scan_qr, (LinearLayout) findViewById(R.id.qr_linear));
        Button scan = (Button)bottomSheetView.findViewById(R.id.scan);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DriverScanActivity.class));
            }
        });

    }

    /**
     * when add new request, following will be executed automatically
     * @param requests
     * @param tag
     */
    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {
        if (tag.equals(RequestDataHelper.SET_ARRIVED_TAG)) {


//            RequestDataHelper
//                    .getInstance()
//                    .queryUserRequest(DatabaseHelper.getInstance().getCurrentUserName(),
//                            "rider", this);

            System.out.println("susccess------------------");
//            Intent intent = new Intent(DriverOnGoingActivity.this, DriverScanActivity.class);
//            startActivity(intent);
//            finish();
            showQRBottom();

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.addMarker(start);
//        mMap.addMarker(destination);
//        showAllMarkers();
//        addPolyline(directionsResult,mMap);

    }

    @Override
    public void onActiveNotification(Request request) {

    }

    @Override
    public void onPickedUpNotification(Request request) {

    }

    @Override
    public  void onArrivedNotification(Request request) {

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
