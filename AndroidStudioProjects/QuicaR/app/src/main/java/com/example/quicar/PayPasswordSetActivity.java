package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.UserDataHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.User;
import com.hanks.passcodeview.PasscodeView;

public class PayPasswordSetActivity extends AppCompatActivity implements OnGetUserDataListener{
    //Initialize variables
    private PasscodeView passcodeView;
    private User user;
    private OnGetUserDataListener listener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_password_set);

        passcodeView = findViewById(R.id.password_set);
        passcodeView.setPasscodeType(PasscodeView.PasscodeViewType.TYPE_SET_PASSCODE);
        passcodeView.setCorrectInputTip("Successfully set your password");

        user = DatabaseHelper.getInstance().getCurrentUser();

        passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {

            }

            @Override
            public void onSuccess(String number) {
                //System.out.println("2222222222222222222222222222222222222222222" + number);
                user.getAccountInfo().getWallet().setPayPassword(number);
                user.getAccountInfo().getWallet().setOpen(true);
                UserDataHelper.getInstance().updateUserProfile(user, listener);
                //System.out.println("3333333333333333333333333333333333333333333" + user.getAccountInfo().getWallet().getPayPassword());
                startActivity(new Intent(PayPasswordSetActivity.this, WalletOverviewActivity.class));
            }
        });
//        user.getAccountInfo().getWallet().setPayPassword(passcodeView.getLocalPasscode());

//        passcodeView.setPasscodeLength(6).setLocalPasscode("123456").setListener(new PasscodeView.PasscodeViewListener() {
//            @Override
//            public void onFail() {
//                // If Password is wrong
//                Toast.makeText(getApplicationContext(), "Password is wrong. Please enter again", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onSuccess(String number) {
//                // If Password is correct
//                startActivity(new Intent(PayPasswordActivity.this, WalletOverviewActivity.class));
//            }
//        });
    }

    @Override
    public void onSuccess(User user, String tag) {

    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }
}
