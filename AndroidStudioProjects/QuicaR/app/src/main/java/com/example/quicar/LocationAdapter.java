package com.example.quicar;


import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entity.Location;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private ArrayList<Location> mLocationList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }



    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnLongClickListener{
        public TextView startAdresse;
        public TextView startLocation;
        CardView mCardView;
        RecyclerViewClickListener recyclerViewClickListener;


        public interface RecyclerViewClickListener
        {
            public int recyclerViewListClicked(int position);
        }


        public LocationViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            startAdresse = itemView.findViewById(R.id.start_address);
            startLocation = itemView.findViewById(R.id.start_location);
            mCardView = itemView.findViewById(R.id.location_card_view);

            mCardView.setOnCreateContextMenuListener((View.OnCreateContextMenuListener) this);


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


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Use as");
            MenuItem start = menu.add(Menu.NONE,1,1,"Start");
            MenuItem end = menu.add(Menu.NONE,2,2,"Destination");

        }


        @Override
        public boolean onLongClick(View v) {
            recyclerViewClickListener.recyclerViewListClicked(this.getPosition());
            return false;
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
        holder.startAdresse.setText(currentItem.getAddressName());
        holder.startLocation.setText(currentItem.getName());

    }

    @Override
    public int getItemCount() {
        return mLocationList.size();
    }

}
