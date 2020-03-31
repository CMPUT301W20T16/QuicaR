package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.UserDataHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.User;
import com.example.util.MyUtil;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class ResetWalletActivity extends AppCompatActivity implements OnGetUserDataListener {


    TextInputLayout logPassword;
    TextInputLayout reenterPassword;
    Button reset;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_wallet);

        logPassword = (TextInputLayout)findViewById(R.id.log_password);
        reenterPassword = (TextInputLayout)findViewById(R.id.reenter_password);
        reset = (Button)findViewById(R.id.reset);
        user = DatabaseHelper.getInstance().getCurrentUser();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("1" + logPassword.getEditText().getText() + "111111111111111111111111111111111111111" + reenterPassword.getEditText().getText() + "1");
                //System.out.println(logPassword.getEditText().getText().toString().equals(reenterPassword.getEditText().getText().toString()));
                if (!logPassword.getEditText().getText().toString().equals(reenterPassword.getEditText().getText().toString())){
                    logPassword.setError(null);
                    reenterPassword.setError("Different from the first enter");
                }
                else if (!logPassword.getEditText().getText().toString().equals(user.getAccountInfo().getPassword())){
                    logPassword.setError("Wrong log in password");
                    reenterPassword.setError(null);
                }else{
                    logPassword.setError(null);
                    reenterPassword.setError(null);
                    user.getAccountInfo().getWallet().setOpen(false);
                    user.getAccountInfo().getWallet().setPayPassword("");
                    user.getAccountInfo().getWallet().setBankAccountArrayList(new ArrayList<>());
                    UserDataHelper.getInstance().updateUserProfile(user, ResetWalletActivity.this);
                }
            }
        });

    }

    @Override
    public void onSuccess(User user, String tag) {
        Toast.makeText(getApplicationContext(), "Reset Successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), RiderRequestActivity.class));
    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (MyUtil.isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                MyUtil.disableSoftInputFromAppearing(this);  //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }
}
