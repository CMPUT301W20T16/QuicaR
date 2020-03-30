package com.example.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.entity.PayRecord;
import com.example.quicar.R;
import com.example.user.BankAccount;

import java.util.ArrayList;

public class PayList extends ArrayAdapter<PayRecord>{

    private ArrayList<PayRecord> payRecords;
    private Context context;

    public PayList(Context context, ArrayList<PayRecord> payRecords){
        super(context,0, payRecords);
        this.payRecords = payRecords;
        this.context = context;
    }

    public ArrayList<PayRecord> getPayRecords(){
        return this.payRecords;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.outline_card, parent,false);
        }

        PayRecord payRecord = payRecords.get(position);

//        TextView cardType = view.findViewById(R.id.card_type);
//        TextView lastFour = view.findViewById(R.id.last_four);
//
//        cardType.setText(card.getType());
//        lastFour.setText(card.getLastFour());

        return view;

    }
}