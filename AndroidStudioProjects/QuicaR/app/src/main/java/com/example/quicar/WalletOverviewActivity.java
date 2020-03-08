package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WalletOverviewActivity extends AppCompatActivity {

    Button change_pay;
    Button card_pay;
    Button camera_scan;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_overview);

        change_pay = (Button)findViewById(R.id.change_pay);
        card_pay = (Button)findViewById(R.id.card_pay);
        camera_scan = (Button)findViewById(R.id.camera_scan);



        camera_scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
            }
        });
    }


}
