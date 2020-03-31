package com.example.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.datahelper.DatabaseHelper;
import com.example.entity.PayRecord;
import com.example.quicar.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PayHistoryList extends ArrayAdapter<PayRecord>{

    private ArrayList<PayRecord> payRecords;
    private Context context;

    public PayHistoryList(Context context, ArrayList<PayRecord> payRecords){
        super(context,0, payRecords);
        this.payRecords = payRecords;
        this.context = context;
    }

    public ArrayList<PayRecord> getRecords(){
        return this.payRecords;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.outline_payrecord, parent,false);
        }

        PayRecord payRecord = payRecords.get(position);
        User user = DatabaseHelper.getInstance().getCurrentUser();
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.CANADA);
        TextView paymentAmount = view.findViewById(R.id.payment_amount);
        TextView title = view.findViewById(R.id.title);
        TextView payee_or_payer = view.findViewById(R.id.payee_or_payer);
        TextView date = view.findViewById(R.id.payment_date);
        String amount = decimalFormat.format(payRecord.getPayment());
        date.setText(sdf.format(payRecord.getDateTime()));

        if (payRecord.getToUser().getName().equals(user.getName())){
            paymentAmount.setText("+" + amount);
            title.setText("Payer: ");
            if (payRecord.getFromCard() == null){
                payee_or_payer.setText("user " + payRecord.getFromUser().getName());
            }else{
                payee_or_payer.setText("card **** **** **** " + payRecord.getFromCard().getCardnumber().substring(12));
            }
        }else{
            paymentAmount.setText("-" + amount);
            title.setText("Payee: ");
            payee_or_payer.setText("user " + payRecord.getToUser().getName());
        }

        return view;

    }
}