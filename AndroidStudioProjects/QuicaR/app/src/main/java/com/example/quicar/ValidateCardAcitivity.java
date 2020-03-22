package com.example.quicar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user.BankAccount;
import com.example.util.MyUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidateCardAcitivity extends AppCompatActivity {

    Button confirm;
    EditText name;
    EditText cardNum;
    EditText expiryDate;
    EditText ccv;
    Date date;

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
//        name.setText(card.getNameOnCard());
//        cardNum.setText(card.getCardnumber());
//        expiryDate.setText(card.getExpireDate().toString());
//        ccv.setText(card.getCcvCode());

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                BankAccount card_1 = new BankAccount();
                String stringDate = expiryDate.getText().toString();
                if (name.getText().toString().length() == 0 || ccv.getText().toString().length() == 0
                        || expiryDate.getText().toString().length() == 0){
                    Toast.makeText(ValidateCardAcitivity.this,"Please fill all the information",Toast.LENGTH_SHORT ).show();
                }
                else if (cardNum.getText().toString().length() != 16){
                    Toast.makeText(ValidateCardAcitivity.this,"Please enter valid card number with length 16",Toast.LENGTH_SHORT ).show();
                }
                else if (!isDate(stringDate)){
                    Toast.makeText(ValidateCardAcitivity.this,"Please enter valid date in the format yyyy/MM/dd",Toast.LENGTH_SHORT ).show();
                }
                else if (ccv.getText().toString().length() != 3){
                    Toast.makeText(ValidateCardAcitivity.this,"Please enter valid ccv number with length 3",Toast.LENGTH_SHORT ).show();
                }
                else {
                    card_1.setNameOnCard(name.getText().toString());
                    card_1.setCardnumber(cardNum.getText().toString());
                    card_1.setCcvCode(ccv.getText().toString());
                    card_1.setType(null);
                    card_1.setExpireDate(date);
                    intent.putExtra("card_1", card_1);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (MyUtil.isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                MyUtil.disableSoftInputFromAppearing(this);  //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }

    private Boolean isDate(String Date){
        try {
            date = new SimpleDateFormat("yyyy/MM/dd").parse(Date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
