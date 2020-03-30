package com.example.util;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.UserState;
import com.example.quicar.DriverBrowsingActivity;
import com.example.quicar.DriverOnGoingActivity;
import com.example.quicar.DriverPickUpActivity;
import com.example.quicar.Login;
import com.example.quicar.RiderConfirmRiderActivity;
import com.example.quicar.RiderMatchingActivity;
import com.example.quicar.RiderOnGoingRequestActivity;
import com.example.quicar.RiderRequestActivity;
import com.example.quicar.RiderReviewActivity;
import com.example.quicar.RiderWaitingRideActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyUtil {

    /**
     * Disable soft keyboard from appearing, use in conjunction with android:windowSoftInputMode="stateAlwaysHidden|adjustNothing"
     */
    public static void disableSoftInputFromAppearing(AppCompatActivity appCompatActivity) {
        View view = appCompatActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) appCompatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],    //得到输入框在屏幕中上下左右的位置
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略
        return false;
    }

    public static void goToIntent(Context context) {
        // handle previous activity here
        UserState userState = DatabaseHelper.getInstance().getUserState();
        Intent directIntent;
        if (userState.getCurrentMode().equals("rider")) {
            if (!userState.getOnConfirm())
                directIntent = new Intent(context, RiderRequestActivity.class);
            else if (!userState.getOnMatching())
                directIntent = new Intent(context, RiderConfirmRiderActivity.class);
            else if (!userState.getActive())
                directIntent = new Intent(context, RiderMatchingActivity.class);
            else if (!userState.getOnGoing())
                directIntent = new Intent(context, RiderWaitingRideActivity.class);
            else if (!userState.getOnArrived())
                directIntent = new Intent(context, RiderOnGoingRequestActivity.class);
            else
                directIntent = new Intent(context, RiderReviewActivity.class);
        } else {
            if (!userState.getActive() && !userState.getOnGoing())
                directIntent = new Intent(context, DriverBrowsingActivity.class);
            else if (userState.getActive() && !userState.getOnGoing())
                directIntent = new Intent(context, DriverPickUpActivity.class);
            else if (userState.getActive() && userState.getOnGoing())
                directIntent = new Intent(context, DriverOnGoingActivity.class);
            else
                directIntent = new Intent(context, DriverBrowsingActivity.class);
        }

        context.startActivity(directIntent);
    }

}
