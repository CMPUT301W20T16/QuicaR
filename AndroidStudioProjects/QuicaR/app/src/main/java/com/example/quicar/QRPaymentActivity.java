package com.example.quicar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import java.time.LocalDateTime;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class QRPaymentActivity extends AppCompatActivity {

    Button scanButton;
    String test = "User: Pangkaikai\nDriver: PANGKAIKAI\nStart: U of A\nDestination: Windsor Park Plaza\nTime: 13:13\nCost: $20";
    ImageView imageView;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_qr);

        scanButton = (Button) findViewById(R.id.refresh);
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
                Animation animation = AnimationUtils.loadAnimation(QRPaymentActivity.this, R.anim.rotate);
                scanButton.setAnimation(animation);
                scanButton.startAnimation(animation);
                String time = LocalDateTime.now().toString();
                WindowManager windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
                Display display = windowManager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int x = point.x;
                int y = point.y;
                int icon = x < y ? x : y;
                icon = icon * 3 / 4;
                QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(test + "\n" + time, BarcodeFormat.QR_CODE.toString(), icon);
                try {
                    Bitmap bitmap = qrCodeGenerator.encodeAsBitmap();
                    imageView.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
