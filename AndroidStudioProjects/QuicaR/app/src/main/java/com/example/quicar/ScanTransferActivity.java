package com.example.quicar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanTransferActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView ScanView;
    public static Result result;
    Integer MY_PERMISSION_REQUEST_CAMERA = 1;
    LocalDateTime generate_time;
    LocalDateTime current_time;
    long duration;
    int dur;

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
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        current_time = LocalDateTime.parse(df.format(LocalDateTime.now()), df);
        generate_time = LocalDateTime.parse(rawResult.getText().split("\n")[0], df);
        duration = ChronoUnit.MINUTES.between(generate_time, current_time);

        System.out.println("11111111111111111111111111111111111111111 " + generate_time + " " + current_time + " " + duration);
        if (duration > 0.5){
            Toast.makeText(getApplicationContext(), "The QR code has been generated more than 30 seconds, please scan the updated one.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), ScanTransferActivity.class));
        }else {
            result = rawResult;
            startActivity(new Intent(getApplicationContext(), SetAmountActivity.class));
            //Toast.makeText(DriverScanActivity.this,"The rider done.",Toast.LENGTH_SHORT ).show();
            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
            //onBackPressed();
        }
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

    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(), WalletOverviewActivity.class));
    }
}
