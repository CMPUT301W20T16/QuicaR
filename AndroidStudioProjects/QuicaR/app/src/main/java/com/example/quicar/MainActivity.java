package com.example.quicar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.datahelper.DatabaseHelper;
import com.example.util.MyUtil;


public class MainActivity extends AppCompatActivity {

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
        System.out.println(DatabaseHelper.getInstance().getUserState().getCurrentUserName());
        if (DatabaseHelper.getInstance().getUserState().getCurrentUserName() != null)
            MyUtil.goToIntent(this);
        else {
            setContentView(R.layout.activity_logo);
            handler.postDelayed(runnable, 1000 * 2);
        }
    }

}
