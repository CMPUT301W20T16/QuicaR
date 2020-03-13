package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidateCardAcitivity extends AppCompatActivity {

    Button confirm;
    EditText name;
    EditText cardNum;
    EditText expiryDate;
    EditText ccv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_card);
        confirm = (Button)findViewById(R.id.card_confirm);
        name = (EditText)findViewById(R.id.name_on);
        cardNum = (EditText)findViewById(R.id.card_num);
        expiryDate = (EditText)findViewById(R.id.expiry_date);
        ccv = (EditText)findViewById(R.id.ccv) ;

        BankAccount card = (BankAccount) getIntent().getSerializableExtra("card");
        name.setText(card.getNameOnCard());
        cardNum.setText(card.getCardnumber());
        expiryDate.setText(card.getExpireDate().toString());
        ccv.setText(card.getCcvCode());

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                BankAccount card_1 = new BankAccount();
                card_1.setNameOnCard(name.getText().toString());
                card_1.setCardnumber(cardNum.getText().toString());
                card_1.setCcvCode(ccv.getText().toString());
                card_1.setType(null);
                String Date = expiryDate.getText().toString();
                try {
                    Date date = new SimpleDateFormat("yyyy/MM/dd").parse(Date);
                    card_1.setExpireDate(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                intent.putExtra("card_1", card_1);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
