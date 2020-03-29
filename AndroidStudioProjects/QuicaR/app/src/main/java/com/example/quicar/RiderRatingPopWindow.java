package com.example.quicar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.UserDataHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.DriverInfo;
import com.example.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;


/**
 * source:
 * Smiley Rating by Sujith Niraikulathan (sujithkanna)
 * https://android-arsenal.com/details/1/5424#!resources
 *
 * Ask rider to rate their driver in scale 1 - 5
 * e.g, 1 for poor service, 5 for excellent service
 */

public class RiderRatingPopWindow extends Activity implements OnGetUserDataListener{

    User currentUser = DatabaseHelper.getInstance().getCurrentUser();

    private static final String TAG = "RiderRatingPopWindow";
    private OnGetUserDataListener listener = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_window_rider_rating);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        SmileRating smileRating = (SmileRating) findViewById(R.id.smile_rating);



        @BaseRating.Smiley int smiley = smileRating.getSelectedSmile();
        switch (smiley) {
            case SmileRating.BAD:
                Log.i(TAG, "Bad");
                break;
            case SmileRating.GOOD:
                Log.i(TAG, "Good");
                break;
            case SmileRating.GREAT:
                Log.i(TAG, "Great");
                break;
            case SmileRating.OKAY:
                Log.i(TAG, "Okay");
                break;
            case SmileRating.TERRIBLE:
                Log.i(TAG, "Terrible");
                break;
            case SmileRating.NONE:
                Log.i(TAG, "None");
                break;
        }

        int rateLevel = smileRating.getRating(); // level is from 1 to 5


        smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(@BaseRating.Smiley int smiley, boolean reselected) {
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.
                switch (smiley) {
                    case SmileRating.BAD:
                        Log.i(TAG, "Bad");
                        break;
                    case SmileRating.GOOD:
                        Log.i(TAG, "Good");
                        break;
                    case SmileRating.GREAT:
                        Log.i(TAG, "Great");
                        break;
                    case SmileRating.OKAY:
                        Log.i(TAG, "Okay");
                        break;
                    case SmileRating.TERRIBLE:
                        Log.i(TAG, "Terrible");
                        break;
                }
            }
        });

        // update rate first
        this.updateDriverRate(rateLevel);

        smileRating.setOnRatingSelectedListener(new SmileRating.OnRatingSelectedListener() {
            @Override
            public void onRatingSelected(int level, boolean reselected) {
                // level is from 1 to 5 (0 when none selected)
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.
                Intent intent = new Intent(RiderRatingPopWindow.this, QRPaymentActivity.class);
                intent.putExtra("rating", level);
                startActivity(intent);
                finish();
            }
        });
    }



    /**
     *Design for update rate
     */
    public void updateDriverRate(int newRateLevel) {
//        int orderNumPre = currentUser.getAccountInfo().getDriverInfo().getOrderNumber();
//        double avgRatePre  = currentUser.getAccountInfo().getDriverInfo().getRating();
//        double preSumRate = orderNumPre * avgRatePre;
//
//        // plus one for current finished order
//        int orderNumNew = orderNumPre + 1;
//        double avgRateNew = (preSumRate + newRateLevel) / orderNumNew;
//        // update new order num and rate
//        currentUser.getAccountInfo().getDriverInfo().setOrderNumber(orderNumNew);
//        currentUser.getAccountInfo().getDriverInfo().setRating(avgRateNew);
        currentUser.getAccountInfo().getDriverInfo().autoCmputAndSetRate((double) newRateLevel);
        UserDataHelper.getInstance().updateUserProfile(currentUser,listener);

    }

    /**
     *
     * for implement to get databasework
     */
    @Override
    public void onSuccess(User user, String tag) {
        ;

    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("isFalse");
        System.out.println(errorMessage);
        Toast.makeText(RiderRatingPopWindow.this,
                "Error loading user data, try later", Toast.LENGTH_SHORT).show();

    }




}
