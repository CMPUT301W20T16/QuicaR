package com.example.quicar;


import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>  {

    private ArrayList<Request> mRequestList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * set listner
     * @param listener
     */

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        public TextView riderName;
        public TextView startAdresse;
        public TextView endAdresse;
        public TextView estimateFare;




        public RequestViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            riderName = itemView.findViewById(R.id.rider_name);
            startAdresse = itemView.findViewById(R.id.start_address);
            endAdresse = itemView.findViewById(R.id.end_address);
            estimateFare = itemView.findViewById(R.id.estimate_fare);

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

    public RequestAdapter(ArrayList<Request> requestList) {
        mRequestList = requestList;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_content, parent, false);
        RequestViewHolder evh = new RequestViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        Request currentItem = mRequestList.get(position);

        holder.riderName.setText("Rider: " + currentItem.getRider().getName());
        //getStart and getEnd currently return lat and lng, need address
        holder.startAdresse.setText("From: " + currentItem.getStartAddrName());
        holder.endAdresse.setText("To: " + currentItem.getDestAddrName());

        holder.estimateFare.setText("Estimated Fare: " + currentItem.getEstimatedCost().toString());
    }

    @Override
    public int getItemCount() {
        return mRequestList.size();
    }


//    @Override
//    // get address name in String from lat and long
//    public String findAddress(double lat, double lng) {
//        // set pick up location automatically as customer's current location
//        Geocoder geocoder = new Geocoder();
//
//        if (lat != 0 && lng != 0) {
//            try {
//                addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            if (address.length() != 0) {
//                return address;
//            }
//        }
//        return null;
//
//    }
}
