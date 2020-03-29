package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.datahelper.DatabaseHelper;
import com.example.user.User;
import com.google.android.material.navigation.NavigationView;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

public class RiderReviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView currentDate, totalFare, totalTime, startAddress, endAddress, RatingButton;
    protected DrawerLayout drawer;
    protected NavigationView navigationView;


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


        RatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RiderReviewActivity.this, RiderRatingPopWindow.class);
                startActivity(intent);
            }
        });


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

}
