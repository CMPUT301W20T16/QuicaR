package com.example.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.entity.Record;
import com.example.quicar.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryList extends ArrayAdapter<Record>{

    private ArrayList<Record> records;
    private Context context;

    public HistoryList(Context context, ArrayList<Record> records){
        super(context,0, records);
        this.records = records;
        this.context = context;
    }

    public ArrayList<Record> getRecords(){
        return this.records;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.outline_history, parent,false);
        }

        Record record = records.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA);

        TextView riderNum = view.findViewById(R.id.rider_num);
        TextView driver = view.findViewById(R.id.driver_name);
        TextView startLocation = view.findViewById(R.id.start_location);
        TextView destination = view.findViewById(R.id.destination);
        TextView date = view.findViewById(R.id.travel_date);

        riderNum.setText("Order#: " + record.getRequest().getRid());
        driver.setText("Driver: " + record.getRequest().getDriver().getName());
        startLocation.setText("Start Location: " + record.getRequest().getStart().getName());
        destination.setText("Destination: " + record.getRequest().getDestination().getName());
        date.setText(sdf.format(record.getDateTime()));

        return view;

    }
}