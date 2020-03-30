package com.example.quicar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.LocationDataHelper;
import com.example.datahelper.RecordDataHelper;
import com.example.datahelper.RequestDataHelper;
import com.example.datahelper.UserDataHelper;
import com.example.entity.Location;
import com.example.entity.Request;
import com.example.listener.OnGetLocationDataListener;
import com.example.listener.OnGetRecordDataListener;
import com.example.listener.OnGetRequestDataListener;
import com.example.user.User;
import com.example.util.ConnectivityReceiver;
import com.example.util.MyUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize data helper
        DatabaseHelper.getInstance();
        RequestDataHelper.getInstance();
        RecordDataHelper.getInstance();
        UserDataHelper.getInstance();

        System.out.println(DatabaseHelper.getInstance().getUserState().getCurrentUserName());
        if (DatabaseHelper.getInstance().getUserState().getCurrentUserName() != null)
            MyUtil.goToIntent(this);
        else
            startActivity(new Intent(getApplicationContext(), Login.class));

    }

}
