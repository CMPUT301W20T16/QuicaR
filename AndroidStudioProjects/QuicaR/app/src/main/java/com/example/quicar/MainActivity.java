package com.example.quicar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.datahelper.DatabaseHelper;
import com.example.util.MyUtil;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize data helper
//        DatabaseHelper.getInstance();
//        RequestDataHelper.getInstance();
//        RecordDataHelper.getInstance();
//        UserDataHelper.getInstance();

        System.out.println(DatabaseHelper.getInstance().getUserState().getCurrentUserName());
        if (DatabaseHelper.getInstance().getUserState().getCurrentUserName() != null)
            MyUtil.goToIntent(this);
        else
            startActivity(new Intent(getApplicationContext(), Login.class));

    }

}
