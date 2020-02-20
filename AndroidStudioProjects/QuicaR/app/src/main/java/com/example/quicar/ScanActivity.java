package com.example.quicar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView ScanView;
    public static Result result;
    Integer MY_PERMISSION_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScanView = new ZXingScannerView(this);
        setContentView(ScanView);
    }

    @Override
    public void handleResult(Result rawResult) {
        //textUsername.setText(rawResult.getText());
        result = rawResult;
        openDialog();
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

    protected void openDialog(){
        RiderDialog riderDialog = new RiderDialog();
        riderDialog.show(getSupportFragmentManager(), "Rider Dialog");
        if (!riderDialog.isOpen){
            Toast.makeText(ScanActivity.this,"The rider done.",Toast.LENGTH_SHORT ).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}
