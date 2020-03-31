package com.example.quicar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.PayRecordDataHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.datahelper.UserDataHelper;
import com.example.entity.PayRecord;
import com.example.entity.Request;
import com.example.listener.OnGetRequestDataListener;
import com.example.listener.OnGetUserDataListener;
import com.example.user.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.zxing.Result;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class DriverScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, OnGetUserDataListener, OnGetRequestDataListener {

    ZXingScannerView ScanView;
    Integer MY_PERMISSION_REQUEST_CAMERA = 1;
    User currentUser;
    User rider;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ScanView = new ZXingScannerView(this);
        setContentView(ScanView);
    }

    @Override
    public void handleResult(Result rawResult) {
        //textUsername.setText(rawResult.getText());
        showRideBottom(rawResult);
        //Toast.makeText(DriverScanActivity.this,"The rider done.",Toast.LENGTH_SHORT ).show();
        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
        //onBackPressed();
    }

    @Override
    protected void onPause(){
        super.onPause();
        ScanView.stopCamera();
    }

    @Override
    protected void onPostResume(){
        super.onPostResume();
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CAMERA);
        }
        ScanView.setResultHandler(this);
        ScanView.startCamera();
    }

    protected void showRideBottom(Result rawResult) {
        RequestDataHelper.getInstance().setOnNotifyListener(this);
        Request currentRequest = DatabaseHelper.getInstance().getUserState().getCurrentRequest();
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DriverScanActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.rider_bottom_sheet, (LinearLayout) findViewById(R.id.rider_linear));
        Button confirm = (Button)bottomSheetView.findViewById(R.id.confirm);
        TextView rider_username = bottomSheetView.findViewById(R.id.rider_username);
        TextView start_place = bottomSheetView.findViewById(R.id.start_place);
        TextView destination_place = bottomSheetView.findViewById(R.id.destination_place);
        TextView money = bottomSheetView.findViewById(R.id.total_money);
        String[] info = rawResult.getText().split("\n");
        String time = info[0];
        String rider_name = info[1];
        Gson gson = new Gson();
        if (rider_name != null){
            rider = gson.fromJson(rider_name, User.class);
            User fromQr = currentRequest.getRider();
            //System.out.println("11111111111111111111111111111111111111111 " + rider.getName() + " " + fromQr.getName());
            if (!rider.getName().equals(fromQr.getName())){
                Toast.makeText(DriverScanActivity.this,
                        "Scan a QR from a wrong user, please scan again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), DriverScanActivity.class));
            }
        } else{
            Toast.makeText(DriverScanActivity.this,
                    "Cannot transfer to a user not exists.", Toast.LENGTH_SHORT).show();
        }
        rider_username.setText("Passenger: " + rider.getName());
        start_place.setText("Start Location: " + currentRequest.getStart().getName());
        destination_place.setText("Destination: " + currentRequest.getDestination().getName());
        money.setText("Total Fare: " + info[2]);
        currentUser = DatabaseHelper.getInstance().getCurrentUser();
        Float amount = Float.parseFloat(info[2]);
        Float rateLevel = Float.parseFloat(info[3]);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("33333333333333333333333333333333333333");
                // finally should be connected with the password enter
                // update rate first
                updateDriverRate(rateLevel);
                RequestDataHelper.getInstance().completeRequest(currentRequest.getRid(), amount, rateLevel,DriverScanActivity.this);
                // the balance is not enough to pay
                rider.getAccountInfo().getWallet().setBalance(rider.getAccountInfo().getWallet().getBalance() - amount);
                currentUser.getAccountInfo().getWallet().setBalance(currentUser.getAccountInfo().getWallet().getBalance() + amount);
                UserDataHelper.getInstance().updateUserProfile(rider, DriverScanActivity.this);
                UserDataHelper.getInstance().updateUserProfile(currentUser, DriverScanActivity.this);
                PayRecord payRecord = new PayRecord(currentUser, rider, null, amount);
                PayRecordDataHelper.getInstance().addPayRecord(payRecord);

            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    /**
     *Design for update rate
     */
    protected void updateDriverRate(Float newRateLevel) {
        int orderNumPre = currentUser.getAccountInfo().getDriverInfo().getOrderNumber();
        double avgRatePre  = currentUser.getAccountInfo().getDriverInfo().getRating();
        double preSumRate = orderNumPre * avgRatePre;

        // plus one for current finished order
        int orderNumNew = orderNumPre + 1;
        double avgRateNew = (preSumRate + newRateLevel) / orderNumNew;
        // update new order num and rate
        currentUser.getAccountInfo().getDriverInfo().setOrderNumber(orderNumNew);
        currentUser.getAccountInfo().getDriverInfo().setRating(avgRateNew);
//        UserDataHelper.getInstance().updateUserProfile(currentUser, this);

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

    @Override
    public void onArrivedNotification(Request request) {

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

    @Override
    public void onSuccess(User user, String tag) {
        Toast.makeText(getApplicationContext(), "Rider completed", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), RiderRequestActivity.class));
    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("isFalse");
        System.out.println(errorMessage);
        Toast.makeText(DriverScanActivity.this,
                "Error loading user data, try later", Toast.LENGTH_SHORT).show();

    }
}
