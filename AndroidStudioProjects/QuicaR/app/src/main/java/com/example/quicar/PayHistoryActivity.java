package com.example.quicar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.PayRecordDataHelper;
import com.example.entity.PayRecord;
import com.example.listener.OnGetPayDataListener;
import com.example.user.PayHistoryList;
import com.example.user.User;
import java.util.ArrayList;

public class PayHistoryActivity extends AppCompatActivity implements OnGetPayDataListener {

    User user;
    ListView payHistoryList;
    ArrayAdapter<PayRecord> payRecordAdapter;
    ArrayList<PayRecord> payRecordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        user = DatabaseHelper.getInstance().getCurrentUser();
        payHistoryList = (ListView)findViewById(R.id.history_list);

        PayRecordDataHelper.getInstance().queryHistoryPayments(user.getName(), 50, this);
    }


    @Override
    public void onSuccess(ArrayList<PayRecord> records) {
        payRecordList = records;
        payRecordAdapter = new PayHistoryList(this, payRecordList);
        payHistoryList.setAdapter(payRecordAdapter);
    }

    @Override
    public void onFailure(String e) {

    }

    @Override
    public void onBackPressed(){

    }
}
