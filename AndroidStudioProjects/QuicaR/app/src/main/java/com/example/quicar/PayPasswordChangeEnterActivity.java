package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.User;
import com.hanks.passcodeview.PasscodeView;

public class PayPasswordChangeEnterActivity extends AppCompatActivity implements OnGetUserDataListener {
        //Initialize variables
        PasscodeView passcodeView;
        User user;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pay_password_change_enter);

            //System.out.println("222222222222222222222222222222");

            passcodeView = findViewById(R.id.password_enter);
            passcodeView.setPasscodeType(PasscodeView.PasscodeViewType.TYPE_CHECK_PASSCODE);
            passcodeView.setFirstInputTip("Enter your old password first");

            user = DatabaseHelper.getInstance().getCurrentUser();
            // System.out.println("1111111111111111111111111111111111111111111111111111111111111" + user.getAccountInfo().getWallet().getPayPassword());
            // user.getAccountInfo().getWallet().getPayPassword()
            passcodeView.setPasscodeLength(6).setLocalPasscode(user.getAccountInfo().getWallet().getPayPassword()).setListener(new PasscodeView.PasscodeViewListener() {
                @Override
                public void onFail() {
                    // If Password is wrong
                    // System.out.println("7777777777777777777777777777777777");
                    Toast.makeText(getApplicationContext(), "Password is wrong. Please enter again", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String number) {
                    // If Password is correct
                    // System.out.println("00000000000000000000000000000000000000");
                    Toast.makeText(getApplicationContext(), "Correct old password", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), PayPasswordSetActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            });
            // System.out.println("8888888888888888888888888888888888888");
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
