package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.PayRecordDataHelper;
import com.example.datahelper.UserDataHelper;
import com.example.entity.PayRecord;
import com.example.listener.OnGetUserDataListener;
import com.example.user.User;
import com.hanks.passcodeview.PasscodeView;

public class PayPasswordEnterActivity extends AppCompatActivity implements OnGetUserDataListener {
    //Initialize variables
    PasscodeView passcodeView;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_password_enter);

        //System.out.println("222222222222222222222222222222");

        passcodeView = findViewById(R.id.password_enter);
        passcodeView.setPasscodeType(PasscodeView.PasscodeViewType.TYPE_CHECK_PASSCODE);
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
                SetAmountActivity.fromUser.getAccountInfo().getWallet().setBalance(SetAmountActivity.fromUser.getAccountInfo().getWallet().getBalance() - SetAmountActivity.money);
                SetAmountActivity.toUser.getAccountInfo().getWallet().setBalance(SetAmountActivity.toUser.getAccountInfo().getWallet().getBalance() + SetAmountActivity.money);
                UserDataHelper.getInstance().updateUserProfile(SetAmountActivity.fromUser, PayPasswordEnterActivity.this);
                UserDataHelper.getInstance().updateUserProfile(SetAmountActivity.toUser, PayPasswordEnterActivity.this);
                PayRecord payRecord = new PayRecord(SetAmountActivity.toUser, SetAmountActivity.fromUser, null, SetAmountActivity.money);
                PayRecordDataHelper.getInstance().addPayRecord(payRecord);
                Toast.makeText(getApplicationContext(), "Pay successfully. Go to wallet page to check your balance", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), WalletOverviewActivity.class));
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
