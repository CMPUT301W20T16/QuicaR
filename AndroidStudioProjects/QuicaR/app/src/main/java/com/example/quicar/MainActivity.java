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
        Record record = new Record(request, new Float(10), new Float(5));
        databaseHelper.addRecquest(request);
        databaseHelper.addRecord(record);
        databaseHelper.addRecord(record);
        //databaseHelper.delRequest("request1");
        //databaseHelper.delRecord("record1");
        //databaseHelper.delRecord("record2");
    }
}
