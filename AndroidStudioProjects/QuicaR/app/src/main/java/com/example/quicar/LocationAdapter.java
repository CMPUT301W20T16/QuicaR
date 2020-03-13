package com.example.quicar;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder>  {

    private ArrayList<Location> mLocationList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        public TextView startAdresse;
        public TextView endAdresse;




        public LocationViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            startAdresse = itemView.findViewById(R.id.start_address);
            endAdresse = itemView.findViewById(R.id.end_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public LocationAdapter(ArrayList<Location> locationList) {
        mLocationList = locationList;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_content, parent, false);
        LocationViewHolder evh = new LocationViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        Location currentItem = mLocationList.get(position);

        //getStart and getEnd currently return lat and lng, need address
        holder.startAdresse.setText("From: " + currentItem.getLat() + currentItem.getLon().toString());
        holder.endAdresse.setText("To: " + currentItem.getLat().toString() + currentItem.getLon().toString());

    }

    @Override
    public int getItemCount() {
        return mLocationList.size();
    }

}
