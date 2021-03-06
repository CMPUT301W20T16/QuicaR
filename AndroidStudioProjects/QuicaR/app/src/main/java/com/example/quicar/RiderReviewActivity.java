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
import com.example.datahelper.RequestDataHelper;
import com.example.datahelper.UserDataHelper;
import com.example.datahelper.UserState;
import com.example.datahelper.UserStateDataHelper;
import com.example.entity.Request;
import com.example.font.TextViewSFProDisplayMedium;
import com.example.font.TextViewSFProDisplaySemibold;
import com.example.listener.OnGetRequestDataListener;
import com.example.listener.OnGetUserDataListener;
import com.example.user.User;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class RiderReviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnGetUserDataListener, OnGetRequestDataListener {

    TextView currentDate, totalFare, totalTime;
    TextViewSFProDisplayMedium startAddress, endAddress, driverName;
    TextViewSFProDisplaySemibold RatingButton;
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
    int rateLevel;

    protected FirebaseAuth mAuth;

    private Request currentRequest;

    DecimalFormat money_df = new DecimalFormat("0.00");



    private static final String TAG = "RiderRatingWindow";
    private OnGetUserDataListener listener = this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //set theme
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_review);

        //connect to database
        RequestDataHelper.getInstance().setOnNotifyListener(this);
        currentRequest = DatabaseHelper.getInstance().getUserState().getCurrentRequest();

        //get
        Intent intent = getIntent();
        Float extraCost = intent.getFloatExtra("extra cost", 0.0f);
        Integer timeRecord = intent.getIntExtra("real time", 0);


        //set up view
        RatingButton = findViewById(R.id.rating_button);
        totalFare = findViewById(R.id.total_fare);
        totalTime = findViewById(R.id.total_time);
        startAddress = findViewById(R.id.start_address);
        endAddress = findViewById(R.id.end_address);
        currentDate = findViewById(R.id.today_date);
        tip_rate = findViewById(R.id.tip_rate);
        rateSpinner = findViewById(R.id.enter_rate);
        driverName = findViewById(R.id.driver_name);

        if (extraCost != null && timeRecord != null) {
            totalFare.setText("$" + Float.toString(currentRequest.getEstimatedCost() + extraCost));
            totalTime.setText(Integer.toString(timeRecord) + "min");
        }
        else {
            totalFare.setText("$" + Float.toString(currentRequest.getEstimatedCost()));
            totalTime.setText(Integer.toString(currentRequest.getTimeRecording()) + "min");
        }

        startAddress.setText(currentRequest.getStart().getAddressName());
        endAddress.setText(currentRequest.getDestination().getAddressName());

        driverName.setText(currentRequest.getDriver().getName());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        String formatted = df.format(new Date());
        currentDate.setText(df.format(new Date()));


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
                                rate = (float)arg2 / 100;
                                System.out.println("11111111111111111111111111111111111111111111111111            " + arg2 + rate);
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

            // logout activity start
            case R.id.nav_logout:

//                startActivity(intentLogout);
// log out directly
//                mAuth.getInstance().signOut();
                //log out directly
                mAuth.getInstance().signOut();
                Intent intentLogout = new Intent(getApplicationContext(), Login.class);
                intentLogout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogout);
                return true;


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


    protected void generate_qr(ImageView qr_code) {
        RequestDataHelper.getInstance().setOnNotifyListener(this);
        Request currentRequest = DatabaseHelper.getInstance().getUserState().getCurrentRequest();
        Float money = currentRequest.getEstimatedCost();
        money = money * (1 + rate);
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
        System.out.println("1111111111111111111111111111111111111111111          "+rateLevel);
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(time + "\n" + json + "\n"  + money_df.format(money) + "\n" + Integer.toString(rateLevel), BarcodeFormat.QR_CODE.toString(), icon);
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


        smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(@BaseRating.Smiley int smiley, boolean reselected) {
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.
                switch (smiley) {
                    case SmileRating.BAD:
                        Log.i(TAG, "Bad");
                        rateLevel = 2;
                        break;
                    case SmileRating.GOOD:
                        Log.i(TAG, "Good");
                        rateLevel = 4;
                        break;
                    case SmileRating.GREAT:
                        Log.i(TAG, "Great");
                        rateLevel = 5;
                        break;
                    case SmileRating.OKAY:
                        Log.i(TAG, "Okay");
                        rateLevel = 3;
                        break;
                    case SmileRating.TERRIBLE:
                        Log.i(TAG, "Terrible");
                        rateLevel = 1;
                        break;
                }
            }
        });

        smileRating.setOnRatingSelectedListener(new SmileRating.OnRatingSelectedListener() {
            @Override
            public void onRatingSelected(int level, boolean reselected) {
                // level is from 1 to 5 (0 when none selected)
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.
                bottomSheetDialog.cancel();
                showQRBottom();
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    protected void showQRBottom() {
        BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(RiderReviewActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.ride_complete_qr, (LinearLayout) findViewById(R.id.qr_linear));
        qrCode = (ImageView) bottomSheetView2.findViewById(R.id.qr_code);
        generate_qr(qrCode);
        bottomSheetDialog2.setContentView(bottomSheetView2);
        bottomSheetDialog2.show();
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

    }

    @Override
    public void onSuccess(ArrayList<Request> requests, String tag) {

    }

    @Override
    public void onActiveNotification(Request request) {

    }

    @Override
    public void onPickedUpNotification(Request request) {

    }

    @Override
    public void onArrivedNotification(Request request) {

    }

    @Override
    public void onCancelNotification() {

    }

    @Override
    public void onCompleteNotification() {
        Toast.makeText(RiderReviewActivity.this,
                "The rider completed", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), RiderRequestActivity.class);
        UserState userState = DatabaseHelper.getInstance().getUserState();
        userState.setOnConfirm(Boolean.FALSE);
        userState.setOnMatching(Boolean.FALSE);
        userState.setActive(Boolean.FALSE);
        userState.setOnGoing(Boolean.FALSE);
        userState.setOnArrived(Boolean.FALSE);
        userState.setCurrentRequest(new Request());
        DatabaseHelper.getInstance().setUserState(userState);
        UserStateDataHelper.getInstance().recordState();
        startActivity(intent);
    }

    @Override
    public void onFailure(String errorMessage, String tag) {

    }
}
