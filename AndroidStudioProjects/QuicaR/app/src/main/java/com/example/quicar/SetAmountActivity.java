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
    public static float money;
    User toUser;
    User fromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_amount);

        toUsername = (TextView)findViewById(R.id.to_username);
        amount = (EditText)findViewById(R.id.amount);
        confirm = (Button)findViewById(R.id.pay_confirm);

        String info = ScanTransferActivity.result.getText();
        String username = info.split("\n")[1];

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
                money = Float.valueOf(amount.getText().toString());
                fromUser = DatabaseHelper.getInstance().getCurrentUser();
                fromUser.getAccountInfo().getWallet().setBalance(fromUser.getAccountInfo().getWallet().getBalance() - money);
                toUser.getAccountInfo().getWallet().setBalance(toUser.getAccountInfo().getWallet().getBalance() + money);
                UserDataHelper.getInstance().updateUserProfile(fromUser, SetAmountActivity.this);
                UserDataHelper.getInstance().updateUserProfile(toUser, SetAmountActivity.this);
                DatabaseHelper.getInstance().setCurrentUser(toUser);
                DatabaseHelper.getInstance().setCurrentUser(fromUser);

                // finally should be connected with the password enter
                startActivity(new Intent(getApplicationContext(), WalletOverviewActivity.class));

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
