package com.example.quicar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.ComplaintDataHelper;
import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.RecordDataHelper;
import com.example.entity.ComplaintRecord;
import com.example.entity.Location;
import com.example.entity.Record;
import com.example.listener.OnGetRecordDataListener;
import com.example.user.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class SuggestionAndComplaintActivity extends AppCompatActivity implements OnGetRecordDataListener {

    Spinner feedbackType;
    EditText feedback;
    Spinner riderNum;
    Button confirm;
    User user;
    String recordRid;
    String reason = "";
    String comment;
    int feedType = -1;
    boolean isSelect = false;
    RelativeLayout driverLayout;
    ArrayList<Record> recordsHistory;
    int clickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedbackType = (Spinner)findViewById(R.id.feedback_type);
        feedback = (EditText)findViewById(R.id.feedback);
        riderNum = (Spinner)findViewById(R.id.rider_num);
        confirm = (Button)findViewById(R.id.confirm);
        driverLayout = (RelativeLayout)findViewById(R.id.driver_mistake_layout);

        ArrayList<String> feedbackTypeList = new ArrayList<String>();
        feedbackTypeList.add("Change balance error");
        feedbackTypeList.add("Unsatisfactory driver");
        feedbackTypeList.add("Other Suggestions for us");
        feedbackTypeList.add("Select your feedback type");
        MyAdapter feedbackAdapter = new MyAdapter<String>(this, android.R.layout.simple_spinner_item, feedbackTypeList);
        feedbackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedbackType.setAdapter(feedbackAdapter);
        feedbackType.setSelection(feedbackTypeList.size() - 1, true);

        feedbackType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                reason = feedbackTypeList.get(arg2);
                feedType = arg2;
                arg0.setVisibility(View.VISIBLE);
                if (arg2 == 1){
                    driverLayout.setVisibility(View.VISIBLE);
                }else{
                    driverLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        user = DatabaseHelper.getInstance().getCurrentUser();
        RecordDataHelper.getInstance().queryHistory(user.getName(), 20, this);

//        user = DatabaseHelper.getInstance().getCurrentUser();
//        RecordDataHelper.getInstance().queryHistory(user.getName(), 20, this);
//        ArrayList<String> riderList = new ArrayList<String>();
//        for (int i = 0; i < 21; i++){
//            Record singleRecord = recordsHistory.get(i);
//            riderList.add("Rider#:" + singleRecord.getRequest().getRid() + "       Date:"+ singleRecord.getDateTimeString() + "\nStart Location:" + singleRecord.getRequest().getStartAddrName() + "\nDestination" + singleRecord.getRequest().getDestAddrName());
//        }
//        riderList.add("Select the rider");
//        MyAdapter riderAdapter = new MyAdapter<String>(this, android.R.layout.simple_spinner_item, riderList);
//        riderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        riderNum.setAdapter(riderAdapter);
//        riderNum.setSelection(riderList.size() - 1, true);
//
//        riderNum.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                recordRid = recordsHistory.get(arg2).getRequest().getRid();
//                arg0.setVisibility(View.VISIBLE);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });
//
//        confirm.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               if (clickTime == 0 && feedback.getText().length() == 0){
//                   clickTime += 1;
//                   Toast.makeText(SuggestionAndComplaintActivity.this,"Comment is empty. If you're sure to leave it blank, click confirm button again",Toast.LENGTH_SHORT ).show();
//               }
//               else{
//                   ComplaintRecord complaintRecord = new ComplaintRecord(recordRid, reason, comment, user);
//                   ComplaintDataHelper.getInstance().addComplaint(complaintRecord);
//                   finish();
//               }
//           }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu second_menu)
    {
        getMenuInflater().inflate(R.menu.feedback, second_menu);
        return super.onCreateOptionsMenu(second_menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //according to different click control different action
        if (item.getItemId() == R.id.call_button){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:5879377725"));
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onSuccess(ArrayList<Location> history) {

    }

    @Override
    public void onGetAllRecords(ArrayList<Record> records) {
        System.out.println(records);
        recordsHistory = records;
        ArrayList<String> riderList = new ArrayList<String>();
        for (int i = 0; i < recordsHistory.size(); i++){
            Record singleRecord = recordsHistory.get(i);
            riderList.add("Rider#:" + singleRecord.getRequest().getRid() + "       Date:"+ singleRecord.getDateTimeString() + "\nStart Location:" + singleRecord.getRequest().getStartAddrName() + "\nDestination" + singleRecord.getRequest().getDestAddrName());
        }
        riderList.add("Select the rider");
        MyAdapter riderAdapter = new MyAdapter<String>(this, android.R.layout.simple_spinner_item, riderList);
        riderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        riderNum.setAdapter(riderAdapter);
        riderNum.setSelection(riderList.size() - 1, true);

        riderNum.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                recordRid = recordsHistory.get(arg2).getRequest().getRid();
                isSelect = true;
                arg0.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickTime == 0 && feedback.getText().toString().length() == 0){
                    clickTime += 1;
                    Toast.makeText(SuggestionAndComplaintActivity.this,"Comment is empty. If you're sure to leave it blank, click confirm button again",Toast.LENGTH_SHORT ).show();
                }
                else if(feedType < 0){
                    Toast.makeText(SuggestionAndComplaintActivity.this,"Please select you feedback type",Toast.LENGTH_SHORT ).show();
                }
                else if(feedType == 2 && !isSelect){
                    Toast.makeText(SuggestionAndComplaintActivity.this,"Please select the rider that you want to complain",Toast.LENGTH_SHORT ).show();
                }
                else {
                    comment = feedback.getText().toString();
                    ComplaintRecord complaintRecord = new ComplaintRecord(recordRid, reason, comment, user);
                    ComplaintDataHelper.getInstance().addComplaint(complaintRecord);
                    Toast.makeText(SuggestionAndComplaintActivity.this,"We have received your suggestions, and we will try our best to timely feedback",Toast.LENGTH_SHORT ).show();
                    startActivity(new Intent(getApplicationContext(), RiderRequestActivity.class));
                }
            }
        });
    }

    @Override
    public void onFailure(String errorMessage) {

    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(), RiderRequestActivity.class));
    }
}
