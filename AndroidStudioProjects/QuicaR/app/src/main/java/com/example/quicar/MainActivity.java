package com.example.quicar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Button scanButton;
    String test = "User: Pangkaikai\n Driver: PANGKAIKAI\n Start: U of A\nDestination: Windsor Park Plaza\nTime: 13:13\nCost: $20";
    ImageView imageView;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanButton = (Button) findViewById(R.id.scan_button);
        imageView = (ImageView) findViewById(R.id.qr_code);

        /*
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
            }
        });
        */

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QR(imageView).execute("https://api.qr-code-generator.com/v1/create/" + test);
            }
        });
    }
}
