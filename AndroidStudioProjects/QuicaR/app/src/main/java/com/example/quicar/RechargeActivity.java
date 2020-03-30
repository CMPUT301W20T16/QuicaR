package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datahelper.DatabaseHelper;
import com.example.datahelper.PayRecordDataHelper;
import com.example.datahelper.UserDataHelper;
import com.example.entity.PayRecord;
import com.example.listener.OnGetUserDataListener;
import com.example.user.BankAccount;
import com.example.user.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RechargeActivity extends AppCompatActivity implements OnGetUserDataListener {
    Spinner card_spinner;
    RadioGroup recharge_amount;
    EditText expiry_date;
    EditText ccv;
    EditText edit_amount;
    Button recharge_confirm;
    BankAccount select_card;
    Date date;
    OnGetUserDataListener listener = this;
    boolean is_select;
    boolean is_amount_fill;
    boolean is_card_select;
    float amount;
    User user;
    MyAdapter cardAdapter;
    ArrayList<BankAccount> cardDataList;
    ArrayList<String> cardNumList;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        card_spinner = (Spinner) findViewById(R.id.card_spinner);
        recharge_amount = (RadioGroup) findViewById(R.id.amount_group);
        expiry_date = (EditText) findViewById(R.id.expiry_date);
        ccv = (EditText) findViewById(R.id.ccv);
        edit_amount = (EditText) findViewById(R.id.enter_amount);
        recharge_confirm = (Button) findViewById(R.id.recharge_confirm);

        user = DatabaseHelper.getInstance().getCurrentUser();
        cardDataList = user.getAccountInfo().getWallet().getBankAccountArrayList();
        cardNumList = new ArrayList<String>();
        for (int i = 0; i < cardDataList.size(); i++){
            //System.out.println(cardDataList.get(i).getCardnumber());
            cardNumList.add("**** **** **** " + cardDataList.get(i).getCardnumber().substring(12));
        }
        cardNumList.add("Select the card");
        cardAdapter = new MyAdapter<String>(this, android.R.layout.simple_spinner_item, cardNumList);
        cardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        card_spinner.setAdapter(cardAdapter);
        card_spinner.setSelection(cardNumList.size() - 1, true);

        card_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                is_card_select = true;
                select_card = cardDataList.get(arg2);
                arg0.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        recharge_amount.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                is_select = true;
                is_amount_fill = true;
                switch (checkedId){
                    case R.id.fifty:
                        amount = 50;
                        edit_amount.setText("");
                        edit_amount.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.one_hundred:
                        amount = 100;
                        edit_amount.setText("");
                        edit_amount.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.two_hundreds:
                        amount = 200;
                        edit_amount.setText("");
                        edit_amount.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.enter:
                        is_select = false;
                        edit_amount.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        recharge_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String stringDate = expiry_date.getText().toString();
                String stringCcv = ccv.getText().toString();
                if (!is_card_select || !is_amount_fill || stringCcv.length() == 0 || stringDate.length() == 0){
                    Toast.makeText(RechargeActivity.this,"Please fill all the information",Toast.LENGTH_SHORT ).show();
                }
                else if (!isDate(stringDate)){
                    Toast.makeText(RechargeActivity.this,"Please enter valid date in the format yyyy/MM/dd",Toast.LENGTH_SHORT ).show();
                }
                else if (!isValidCardInfo(select_card, date, stringCcv)){
                    Toast.makeText(RechargeActivity.this,"Card information does not match",Toast.LENGTH_SHORT ).show();
                }
                else if (stringCcv.length() != 3){
                    Toast.makeText(RechargeActivity.this,"Please enter valid ccv number with length 3",Toast.LENGTH_SHORT ).show();
                }
                else {
                    if (!is_select){
                        amount = Float.parseFloat(edit_amount.getText().toString());
                    }
                    PayRecord payRecord = new PayRecord(user, null, select_card, amount);
                    PayRecordDataHelper.getInstance().addPayRecord(payRecord);
                    amount += user.getAccountInfo().getWallet().getBalance();
                    user.getAccountInfo().getWallet().setBalance(amount);
                    UserDataHelper.getInstance().updateUserProfile(user, listener);
                    Toast.makeText(RechargeActivity.this,"Recharge successfully",Toast.LENGTH_SHORT ).show();
                    startActivity(new Intent(getApplicationContext(), WalletOverviewActivity.class));
                }
            }
        });
    }

    private Boolean isDate(String Date){
        try {
            date = new SimpleDateFormat("yyyy/MM/dd").parse(Date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private Boolean isValidCardInfo(BankAccount select_card, Date date, String ccv){
        //System.out.println("111111111111111111111111111111111  " + date.toString() + "           " + select_card.getExpireDate().toString());
        //System.out.println("111111111111111111111111111111111  " + ccv + "           " + select_card.getCcvCode());
        //System.out.print(select_card.getCcvCode().equals(ccv));
        //System.out.print(select_card.getExpireDate().toString().equals(date.toString()));
        if (!select_card.getExpireDate().toString().equals(date.toString())){
            //System.out.println("222222222222222222222");
            return false;
        }
        else return select_card.getCcvCode().equals(ccv);
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

    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(), WalletOverviewActivity.class));
    }
}
