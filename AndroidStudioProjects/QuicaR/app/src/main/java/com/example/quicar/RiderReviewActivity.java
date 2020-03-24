package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

public class RiderReviewActivity extends AppCompatActivity {

    TextView currentDate, totalFare, totalTime, startAddress, endAddress, RatingButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_review);

        RatingButton = findViewById(R.id.rating_button);
        totalFare = findViewById(R.id.total_fare);
        totalTime = findViewById(R.id.total_time);
        startAddress = findViewById(R.id.start_address);
        endAddress = findViewById(R.id.end_address);
        currentDate = findViewById(R.id.today_date);

        RatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RiderReviewActivity.this, RiderRatingPopWindow.class);
                startActivity(intent);
            }
        });


    }
}
