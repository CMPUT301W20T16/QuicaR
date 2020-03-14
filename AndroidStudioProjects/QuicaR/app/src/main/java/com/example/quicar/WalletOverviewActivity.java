package com.example.quicar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.time.LocalDateTime;

public class WalletOverviewActivity extends AppCompatActivity {

    Button change_pay;
    Button card_pay;
    Button camera_scan;
    Button card_info;
    ImageView qr_code;
    TextView balance;
    Handler handler = new Handler();
    Handler handler2 = new Handler();
    String currentBalance;
    User user;

    // every 30 seconds refresh the qr code 1 time
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(runnable, 1000 * 30);
        }
        void update() {
            generate_qr(qr_code);
        }
    };

    // every 30 seconds refresh the qr code 1 time
    private Runnable runnable2 = new Runnable() {
        public void run() {
            this.update();
            handler2.postDelayed(runnable2, 1000 * 2);
        }
        void update() {
            if (user != null) {
                user = DatabaseHelper.getInstance().getCurrentUser();
                currentBalance = "( $ " + user.getAccountInfo().getWallet().getBalance().toString() + " )";
                balance.setText(currentBalance);
                balance.bringToFront();
            }
        }
    };

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_overview);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        qr_code = (ImageView) findViewById(R.id.qr_code);
        change_pay = (Button)findViewById(R.id.change_pay);
        card_pay = (Button)findViewById(R.id.card_pay);
        camera_scan = (Button)findViewById(R.id.camera_scan);
        card_info = (Button)findViewById(R.id.card_information);
        balance = (TextView)findViewById(R.id.balance);

        handler.postDelayed(runnable, 1000 * 30);
        generate_qr(qr_code);

        handler2.postDelayed(runnable2, 1000 * 2);
        user = DatabaseHelper.getInstance().getCurrentUser();
        if(user.getAccountInfo().getWallet() == null) {
            System.out.println(user.getAccountInfo().getUserName());
        }
        String currentBalance = "( $ " + user.getAccountInfo().getWallet().getBalance().toString() + " )";
        balance.setText(currentBalance);
        balance.bringToFront();

        camera_scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanTransferActivity.class));
            }
        });

        card_info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ManageCardActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    protected void generate_qr(ImageView qr_code) {
        String time = LocalDateTime.now().toString();
        Gson gson = new Gson();
        String json = gson.toJson(DatabaseHelper.getInstance().getCurrentUser());
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int x = point.x;
        int y = point.y;
        int icon = x < y ? x : y;
        icon = icon * 3 / 4;
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(time + "\n" + json, BarcodeFormat.QR_CODE.toString(), icon);
        try {
            Bitmap bitmap = qrCodeGenerator.encodeAsBitmap();
            qr_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
