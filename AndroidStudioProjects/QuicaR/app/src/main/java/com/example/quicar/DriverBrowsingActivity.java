package com.example.quicar;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.quicar.R;

/**
 * Driver is able to browse
 */
public class DriverBrowsingActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationView.inflateMenu(R.menu.drawer_menu_driver);

    }
}
