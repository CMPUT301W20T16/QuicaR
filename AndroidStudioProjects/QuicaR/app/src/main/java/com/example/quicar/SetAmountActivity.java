package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class SetAmountActivity extends AppCompatActivity implements OnGetUserDataListener{
    TextView toUsername;
    EditText amount;
    Button confirm;
    float money;
    User toUser;
    User fromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_amount);

        toUsername = (TextView)findViewById(R.id.to_username);
        amount = (EditText)findViewById(R.id.amount);
        confirm = (Button)findViewById(R.id.pay_confirm);

        String info = ScanTransferActivity.result.getText().toString();
        String username = info.split("\n")[1];
        toUsername.setText(username);

        Gson gson = new Gson();
        if (username != null){
            toUser = gson.fromJson(username, User.class);
        } else{
            Toast.makeText(SetAmountActivity.this,
                    "Cannot transfer to a user not exists.", Toast.LENGTH_SHORT).show();
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money = Float.valueOf(amount.getText().toString());
                fromUser = DatabaseHelper.getCurrentUser();
                fromUser.getAccountInfo().getWallet().setBalance(fromUser.getAccountInfo().getWallet().getBalance() - money);
                toUser.getAccountInfo().getWallet().setBalance(fromUser.getAccountInfo().getWallet().getBalance() + money);
                UserDataHelper.updateUserProfile(fromUser, SetAmountActivity.this);
                UserDataHelper.updateUserProfile(toUser, SetAmountActivity.this);
                //Float.parseFloat

            }
        });

    }

    @Override
    public void onSuccess(User user, String tag) {

    }

    @Override
    public void onUserExists(Boolean exists, String tag) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }
}
