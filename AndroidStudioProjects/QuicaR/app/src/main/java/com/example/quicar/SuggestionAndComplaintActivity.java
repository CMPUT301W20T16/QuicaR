package com.example.quicar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.entity.Record;
import com.example.user.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class SuggestionAndComplaintActivity extends AppCompatActivity {

    Spinner feedbackType;
    EditText feedback;
    Spinner riderNum;
    User user;
    String recordRid;
    String reason;
    String comment;
    Date datetime;
    RelativeLayout driverLayout;
    ArrayList<Record> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedbackType = (Spinner)findViewById(R.id.feedback_type);
        feedback = (EditText)findViewById(R.id.feedback);
        riderNum = (Spinner)findViewById(R.id.rider_num);
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
                arg0.setVisibility(View.VISIBLE);
                if (arg2 == 1){
                    driverLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        user = DatabaseHelper.getInstance().getCurrentUser();
//        cardDataList = user.getAccountInfo().getWallet().getBankAccountArrayList();
//        cardNumList = new ArrayList<String>();
//        for (int i = 0; i < cardDataList.size(); i++){
//            //System.out.println(cardDataList.get(i).getCardnumber());
//            cardNumList.add("**** **** **** " + cardDataList.get(i).getCardnumber().substring(12));
//        }
//        cardNumList.add("Select the card");
//        cardAdapter = new MyAdapter<String>(this, android.R.layout.simple_spinner_item, cardNumList);
//        cardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        card_spinner.setAdapter(cardAdapter);
//        card_spinner.setSelection(cardNumList.size() - 1, true);
//
//        card_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                is_card_select = true;
//                select_card = cardDataList.get(arg2);
//                arg0.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//
//        });



    }
}
