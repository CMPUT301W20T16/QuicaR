package com.example.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quicar.R;
import com.example.user.BankAccount;

import java.util.ArrayList;

public class CardList extends ArrayAdapter<BankAccount>{

    private ArrayList<BankAccount> cards;
    private Context context;

    public CardList(Context context, ArrayList<BankAccount> cards){
        super(context,0, cards);
        this.cards = cards;
        this.context = context;
    }

    public ArrayList<BankAccount> getCards(){
        return this.cards;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.outline_card, parent,false);
        }

        BankAccount card = cards.get(position);

        TextView cardType = view.findViewById(R.id.card_type);
        TextView lastFour = view.findViewById(R.id.last_four);

        cardType.setText(card.getType());
        lastFour.setText(card.getLastFour());

        return view;

    }
}
