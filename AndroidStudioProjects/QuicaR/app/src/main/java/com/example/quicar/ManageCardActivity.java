package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.UserDataHelper;
import com.example.listener.OnGetUserDataListener;
import com.example.user.BankAccount;
import com.example.user.CardList;
import com.example.user.User;

import java.util.ArrayList;

public class ManageCardActivity extends AppCompatActivity implements OnGetUserDataListener {

    User user;
    Button addCard;
    ListView cardList;
    ArrayAdapter<BankAccount> cardAdapter;
    ArrayList<BankAccount> cardDataList;
    int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_card);

        user = DatabaseHelper.getInstance().getCurrentUser();
        addCard = (Button)findViewById(R.id.add_card);
        cardList = (ListView)findViewById(R.id.card_list);

        //cardDataList = user.getAccountInfo().getWallet().getBankAccountArrayList();
        cardDataList = user.getAccountInfo().getWallet().getBankAccountArrayList();
        cardAdapter = new CardList(this, cardDataList);
        cardList.setAdapter(cardAdapter);

        //set context menu
        registerForContextMenu(cardList);


        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                BankAccount card = new BankAccount();
            // set from mainActivity we start secondaryActivity
                intent.setClass(ManageCardActivity.this, ValidateCardActivity.class);
            // put a value in the intent
                intent.putExtra("card", card);
           // start the second activity
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.card_actions_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e("", "Bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
            case R.id.delete:
                cardDataList.remove(info.position);
                cardAdapter.notifyDataSetChanged();
                DatabaseHelper.getInstance().setCurrentUser(user);
                UserDataHelper.getInstance().updateUserProfile(user, this);
                System.out.println("DELETE------------------------");
                break;

//            case R.id.edit:
//                currentPosition = info.position;
//                BankAccount chosedCard = (BankAccount) cardAdapter.getItem(info.position);
//                Intent intent = new Intent();
//                intent.setClass(ManageCardActivity.this, ValidateCardActivity.class);
//                intent.putExtra("chosed card", chosedCard);
//                startActivityForResult(intent, 2);

        }
        return super.onContextItemSelected(item);

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
//            intent.setClass(ManageCardActivity.this, ValidateCardActivity.class);
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
                break;
//            case 2:
//                if (resultCode == RESULT_OK) {
//                    BankAccount editCard = (BankAccount) data.getExtras().getSerializable("edited card");
//                    cardDataList.set(currentPosition, editCard);
//
//
//                    cardAdapter.notifyDataSetChanged();
//                    DatabaseHelper.getInstance().setCurrentUser(user);
//                    UserDataHelper.getInstance().updateUserProfile(user, this);
//                    System.out.println("222222222222222222222222222");
//
//                }
        }
    }

    @Override
    public void onSuccess(User user, String tag) {
        Toast.makeText(ManageCardActivity.this,
                "Successfully update card information", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateNotification(User user) {

    }

    @Override
    public void onFailure(String errorMessage) {
        Toast.makeText(ManageCardActivity.this,
                "Failure to update card information", Toast.LENGTH_SHORT).show();
    }
}
