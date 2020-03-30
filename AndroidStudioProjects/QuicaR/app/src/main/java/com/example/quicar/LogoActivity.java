package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LogoActivity extends AppCompatActivity {
    Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        public void run() {
            startActivity(new Intent(getApplicationContext(), Login.class));
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        handler.postDelayed(runnable, 1000 * 2);
    }
}
