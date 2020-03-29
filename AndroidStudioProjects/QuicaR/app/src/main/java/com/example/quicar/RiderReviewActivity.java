package com.example.quicar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.UserDataHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.User;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RiderReviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnGetUserDataListener {

    TextView currentDate, totalFare, totalTime, startAddress, endAddress, RatingButton;
    protected DrawerLayout drawer;
    protected NavigationView navigationView;
    ImageView qrCode;
    RadioGroup tip_rate;
    Spinner rateSpinner;
    float rate;
    String time;
    User currentUser = DatabaseHelper.getInstance().getCurrentUser();
    ArrayList<String> rateList;
    MyAdapter rateAdapter;

    private static final String TAG = "RiderRatingWindow";
    private OnGetUserDataListener listener = this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //set theme
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_review);

        RatingButton = findViewById(R.id.rating_button);
        totalFare = findViewById(R.id.total_fare);
        totalTime = findViewById(R.id.total_time);
        startAddress = findViewById(R.id.start_address);
        endAddress = findViewById(R.id.end_address);
        currentDate = findViewById(R.id.today_date);
        tip_rate = findViewById(R.id.tip_rate);
        rateSpinner = findViewById(R.id.enter_rate);

        // set up the action tool bar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // set up the left navigation  drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);


        // connect navigation drawer to tool bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close);
        toggle.syncState();

        rateList = new ArrayList<String>();
        for (int i = 0; i < 22; i++){
            //System.out.println(cardDataList.get(i).getCardnumber());
            rateList.add(Integer.toString(i) + " %");
        }
        rateAdapter = new MyAdapter<String>(this, android.R.layout.simple_spinner_item, rateList);
        rateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rateSpinner.setAdapter(rateAdapter);
        rateSpinner.setSelection(20, true);

        tip_rate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.eight_percent:
                        rate = 0.08f;
                        rateSpinner.setSelection(20, true);
                        rateSpinner.setVisibility(View.INVISIBLE);
                        showRatingBottom();
                        break;
                    case R.id.ten_percent:
                        rate = 0.1f;
                        rateSpinner.setSelection(20, true);
                        rateSpinner.setVisibility(View.INVISIBLE);
                        showRatingBottom();
                        break;
                    case R.id.fifteen_percent:
                        rate = 0.15f;
                        rateSpinner.setSelection(20, true);
                        rateSpinner.setVisibility(View.INVISIBLE);
                        showRatingBottom();
                        break;
                    case R.id.other:
                        rateSpinner.setVisibility(View.VISIBLE);
                        rateSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                rate = arg2 / 100;
                                arg0.setVisibility(View.VISIBLE);
                                showRatingBottom();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                            }
                        });
                        break;
                }
            }
        });


//        RatingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomSheet = new ButtomSheetDialogFragment;
////                Intent intent = new Intent(RiderReviewActivity.this, RiderRatingPopWindow.class);
////                startActivity(intent);
//            }
//        });


    }


    /**
     * drawer method
     */
    // enable user to close the navigation drawer
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // enable user to select item from navigation drawer

    /**
     * This is executed when the one item is selected
     *
     *
     *
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                // change here
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
//                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivityForResult(intent, 2);
                break;


            case R.id.nav_driver_mode:
                if(DatabaseHelper.getInstance().getCurrentMode() == "rider") {

                    User currentUser = DatabaseHelper.getInstance().getCurrentUser();
                    if(currentUser.isDriver()){
                        Intent intent2 = new Intent(getApplicationContext(), DriverBrowsingActivity.class);
                        startActivity(intent2);
                    }

                    else{
                        Intent intent2 = new Intent(getApplicationContext(), RegisterDriverActivity.class);
                        startActivity(intent2);
                    }

                }
                break;

            case R.id.rider_mode:
                if(DatabaseHelper.getInstance().getCurrentMode() == "driver") {
                    //Toast.makeText(this, "Enter if statement!", Toast.LENGTH_LONG).show();


                    Intent intent3 = new Intent(getApplicationContext(), RiderRequestActivity.class);
                    startActivity(intent3);
                }
                break;
            case R.id.nav_wallet:
                User user = DatabaseHelper.getInstance().getCurrentUser();
                if (user.getAccountInfo().getWallet().isOpen()){
                    Intent intent4 = new Intent(getApplicationContext(), WalletOverviewActivity.class);
                    //Intent intent4 = new Intent(getApplicationContext(), PayPasswordEnterActivity.class);
                    startActivity(intent4);
                }else{
                    //Intent intent4 = new Intent(getApplicationContext(), WalletOverviewActivity.class);
                    Intent intent4 = new Intent(getApplicationContext(), PayPasswordSetActivity.class);
                    startActivity(intent4);
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu ride_menu)
    {
        getMenuInflater().inflate(R.menu.action_bar_confirm, ride_menu);
        return super.onCreateOptionsMenu(ride_menu);
    }


    /**
     *Design for update rate
     */
    protected void updateDriverRate(int newRateLevel) {
        int orderNumPre = currentUser.getAccountInfo().getDriverInfo().getOrderNumber();
        double avgRatePre  = currentUser.getAccountInfo().getDriverInfo().getRating();
        double preSumRate = orderNumPre * avgRatePre;

        // plus one for current finished order
        int orderNumNew = orderNumPre + 1;
        double avgRateNew = (preSumRate + newRateLevel) / orderNumNew;
        // update new order num and rate
        currentUser.getAccountInfo().getDriverInfo().setOrderNumber(orderNumNew);
        currentUser.getAccountInfo().getDriverInfo().setRating(avgRateNew);
        UserDataHelper.getInstance().updateUserProfile(currentUser,listener);

    }

    protected void generate_qr(ImageView qr_code) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        time = df.format(LocalDateTime.now());
        Gson gson = new Gson();
        String json = gson.toJson(DatabaseHelper.getInstance().getCurrentUser());
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int x = point.x;
        int y = point.y;
        int icon = x < y ? x : y;
        icon = icon * 3 / 4;
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(time + "\n" + json, BarcodeFormat.QR_CODE.toString(), icon);
        try {
            Bitmap bitmap = qrCodeGenerator.encodeAsBitmap();
            qr_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    protected void showRatingBottom(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(RiderReviewActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_window_rider_rating,(LinearLayout)findViewById(R.id.rate_linear));
        SmileRating smileRating = (SmileRating)bottomSheetView.findViewById(R.id.smile_rating);
        qrCode = (ImageView)bottomSheetView.findViewById(R.id.qr_code);
        generate_qr(qrCode);
//                DisplayMetrics dm = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//                int width = dm.widthPixels;
//                int height = dm.heightPixels;
//
//                getWindow().setLayout((int)(width * 0.8), (int)(height * 0.5));
//
//                WindowManager.LayoutParams params = getWindow().getAttributes();
//                params.gravity = Gravity.CENTER;
//                params.x = 0;
//                params.y = -20;
//
//                getWindow().setAttributes(params);

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
        updateDriverRate(rateLevel);

        smileRating.setOnRatingSelectedListener(new SmileRating.OnRatingSelectedListener() {
            @Override
            public void onRatingSelected(int level, boolean reselected) {
                // level is from 1 to 5 (0 when none selected)
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.
                Intent intent = new Intent(RiderReviewActivity.this, QRPaymentActivity.class);
                intent.putExtra("rating", level);
                startActivity(intent);
                finish();
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    /**
     *
     * for implement to get databasework
     */
    @Override
    public void onSuccess(User user, String tag) {

    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println("isFalse");
        System.out.println(errorMessage);
        Toast.makeText(RiderReviewActivity.this,
                "Error loading user data, try later", Toast.LENGTH_SHORT).show();

    }
}
