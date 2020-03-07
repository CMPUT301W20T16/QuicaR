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

public class MainActivity extends AppCompatActivity {

    Button scanButton;
    String test = "User: Pangkaikai\nDriver: PANGKAIKAI\nStart: U of A\nDestination: Windsor Park Plaza\nTime: 13:13\nCost: $20";
    ImageView imageView;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_qr);

    }
}
