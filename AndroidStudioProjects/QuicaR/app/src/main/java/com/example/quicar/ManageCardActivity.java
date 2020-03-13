package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ManageCardActivity extends AppCompatActivity {

    User user;
    Button addCard;
    ListView cardList;
    ArrayAdapter<BankAccount> cardAdapter;
    ArrayList<BankAccount> cardDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_manage_card);

        addCard = (Button)findViewById(R.id.add_card);
        cardList = (ListView)findViewById(R.id.card_list);

        //cardDataList = user.getAccountInfo().getWallet().getBankAccountArrayList();
        cardDataList = new ArrayList<BankAccount>();
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
                    System.out.println("1111111111111111111111111111111111111111111111111111111111111111111");
                }
        }
    }
}
