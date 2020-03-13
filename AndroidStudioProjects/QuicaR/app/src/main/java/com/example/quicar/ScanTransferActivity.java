package com.example.quicar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanTransferActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView ScanView;
    public static Result result;
    Integer MY_PERMISSION_REQUEST_CAMERA = 1;

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
        result = rawResult;
        startActivity(new Intent(getApplicationContext(), SetAmountActivity.class));
        //Toast.makeText(ScanActivity.this,"The rider done.",Toast.LENGTH_SHORT ).show();
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
}
