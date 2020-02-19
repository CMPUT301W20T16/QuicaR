package com.example.quicar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper databaseHelper = new DatabaseHelper();
        Request request = new Request(new Location(), new Location(), new User(), new User());
        Record record = new Record(request, 10.0f, 5.0f);
        //System.out.println(record.getDateTime());
        databaseHelper.addRequest(request);
        databaseHelper.addRecord(record);
        Request query = databaseHelper.queryRequest("testing");
        System.out.println("------------------------------" + query.getRider().getName());
    }
}
