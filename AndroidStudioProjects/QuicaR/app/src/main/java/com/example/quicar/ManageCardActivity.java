package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ManageCardActivity extends AppCompatActivity implements OnGetUserDataListener{

    User user;
    Button addCard;
    ListView cardList;
    ArrayAdapter<BankAccount> cardAdapter;
    ArrayList<BankAccount> cardDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_manage_card);

        user = DatabaseHelper.getInstance().getCurrentUser();
        addCard = (Button)findViewById(R.id.add_card);
        cardList = (ListView)findViewById(R.id.card_list);

        //cardDataList = user.getAccountInfo().getWallet().getBankAccountArrayList();
        cardDataList = user.getAccountInfo().getWallet().getBankAccountArrayList();
        cardAdapter = new CardList(this, cardDataList);
        cardList.setAdapter(cardAdapter);

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                BankAccount card = new BankAccount();
            // set from mainActivity we start secondaryActivity
                intent.setClass(ManageCardActivity.this, ValidateCardAcitivity.class);
            // put a value in the intent
                intent.putExtra("card", card);
           // start the second activity
                startActivityForResult(intent, 1);
            }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        //according to different click control different action
//        if (item.getItemId() == R.id.add_card){
//            // create a new intent instance
//            Intent intent = new Intent();
//            BankAccount card = new BankAccount();
//            // set from mainActivity we start secondaryActivity
//            intent.setClass(ManageCardActivity.this, ValidateCardAcitivity.class);
//            // put a value in the intent
//            intent.putExtra("card", card);
//            // start the second activity
//            startActivityForResult(intent, 1);
//        }
//        return true;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    BankAccount card_1 = (BankAccount) data.getExtras().getSerializable("card_1");
                    cardDataList.add(card_1);
                    cardAdapter.notifyDataSetChanged();
                    DatabaseHelper.getInstance().setCurrentUser(user);
                    UserDataHelper.getInstance().updateUserProfile(user, this);
                    System.out.println("1111111111111111111111111111111111111111111111111111111111111111111");
                }
        }
    }

    @Override
    public void onSuccess(User user, String tag) {
        Toast.makeText(ManageCardActivity.this,
                "Successfully add a new card", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {
        Toast.makeText(ManageCardActivity.this,
                "Failure to  add a new card", Toast.LENGTH_SHORT).show();
    }
}
