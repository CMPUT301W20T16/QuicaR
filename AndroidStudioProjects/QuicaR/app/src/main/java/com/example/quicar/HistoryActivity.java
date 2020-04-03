package com.example.quicar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RecordDataHelper;
import com.example.entity.Location;
import com.example.entity.Record;
import com.example.listener.OnGetRecordDataListener;
import com.example.user.HistoryList;
import com.example.user.User;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements OnGetRecordDataListener {

    User user;
    ListView historyList;
    ArrayAdapter<Record> recordAdapter;
    ArrayList<Record> recordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        user = DatabaseHelper.getInstance().getCurrentUser();
        historyList = (ListView)findViewById(R.id.history_list);

        RecordDataHelper.getInstance().queryHistory(user.getName(), 20, this);
    }


    @Override
    public void onSuccess(ArrayList<Location> history) {

    }

    @Override
    public void onGetAllRecords(ArrayList<Record> records) {
        recordList = records;
        recordAdapter = new HistoryList(this, recordList);
        historyList.setAdapter(recordAdapter);

    }

    @Override
    public void onFailure(String errorMessage) {
//        Toast.makeText(HistoryActivity.this,
//                errorMessage, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed(){

    }

}
