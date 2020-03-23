package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.user.User;
import com.hanks.passcodeview.PasscodeView;

public class PayPasswordEnterActivity extends AppCompatActivity {
    //Initialize variables
    PasscodeView passcodeView;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_password);

        passcodeView = findViewById(R.id.password_enter);
        passcodeView.setPasscodeType(PasscodeView.PasscodeViewType.TYPE_CHECK_PASSCODE);
        user = DatabaseHelper.getInstance().getCurrentUser();

        passcodeView.setPasscodeLength(6).setLocalPasscode(user.getAccountInfo().getWallet().getPayPassword()).setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
                // If Password is wrong
                Toast.makeText(getApplicationContext(), "Password is wrong. Please enter again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String number) {
                // If Password is correct
                Toast.makeText(getApplicationContext(), "Pay successfully. Go to wallet page to check your balance", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PayPasswordEnterActivity.this, WalletOverviewActivity.class));
            }
        });
    }
}
