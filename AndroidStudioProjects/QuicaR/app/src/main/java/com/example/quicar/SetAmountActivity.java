package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.UserDataHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.User;
import com.google.gson.Gson;

// the activity that let the user set the transfer amount
public class SetAmountActivity extends AppCompatActivity implements OnGetUserDataListener {
    TextView toUsername;
    EditText amount;
    Button confirm;
    String info;
    String username;
    float balance;
    public static float money;
    public static User toUser;
    public static User fromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_amount);

        toUsername = (TextView)findViewById(R.id.to_username);
        amount = (EditText)findViewById(R.id.amount);
        confirm = (Button)findViewById(R.id.pay_confirm);

        info = ScanTransferActivity.result.getText();
        username = info.split("\n")[1];

        // read the user object from the gson string
        Gson gson = new Gson();
        if (username != null){
            toUser = gson.fromJson(username, User.class);
        } else{
            Toast.makeText(SetAmountActivity.this,
                    "Cannot transfer to a user not exists.", Toast.LENGTH_SHORT).show();
        }
        toUsername.setText(toUser.getAccountInfo().getUserName());

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("33333333333333333333333333333333333333");
                // finally should be connected with the password enter
                money = Float.valueOf(amount.getText().toString());
                fromUser = DatabaseHelper.getInstance().getCurrentUser();
                balance = fromUser.getAccountInfo().getWallet().getBalance();
                // the balance is not enough to pay
                if (money > balance){
                    Toast.makeText(getApplicationContext(), "Balance is not enough to pay.", Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(SetAmountActivity.this, PayPasswordEnterActivity.class));
//                System.out.println("33333333333333333333333333333333333333");
//                fromUser.getAccountInfo().getWallet().setBalance(fromUser.getAccountInfo().getWallet().getBalance() - money);
//                toUser.getAccountInfo().getWallet().setBalance(toUser.getAccountInfo().getWallet().getBalance() + money);
//                UserDataHelper.getInstance().updateUserProfile(fromUser, SetAmountActivity.this);
//                UserDataHelper.getInstance().updateUserProfile(toUser, SetAmountActivity.this);
//                System.out.println("444444444444444444444444444444444444444444");
                    //startActivity(new Intent(SetAmountActivity.this, WalletOverviewActivity.class));
                }
            }
        });

    }

    @Override
    public void onSuccess(User user, String tag) {
        startActivity(new Intent(getApplicationContext(), WalletOverviewActivity.class));
    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }
}
