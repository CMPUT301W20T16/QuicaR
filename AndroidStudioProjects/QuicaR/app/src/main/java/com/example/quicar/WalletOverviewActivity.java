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
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.time.LocalDateTime;

public class WalletOverviewActivity extends AppCompatActivity {

    Button change_pay;
    Button card_pay;
    Button camera_scan;
    ImageView qr_code;
    Handler handler = new Handler();

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

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_overview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        change_pay = (Button)findViewById(R.id.change_pay);
        card_pay = (Button)findViewById(R.id.card_pay);
        camera_scan = (Button)findViewById(R.id.camera_scan);
        qr_code = (ImageView) findViewById(R.id.qr_code);

        handler.postDelayed(runnable, 1000 * 30);

        generate_qr(qr_code);

        camera_scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
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
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int x = point.x;
        int y = point.y;
        int icon = x < y ? x : y;
        icon = icon * 3 / 4;
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(time, BarcodeFormat.QR_CODE.toString(), icon);
        try {
            Bitmap bitmap = qrCodeGenerator.encodeAsBitmap();
            qr_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
