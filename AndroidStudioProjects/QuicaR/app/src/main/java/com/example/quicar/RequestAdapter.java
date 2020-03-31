package com.example.quicar;


import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entity.Request;

import java.util.ArrayList;

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
        TextView riderName;
        TextView startAdresse;
        TextView endAdresse;
        TextView estimateFare;


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
        holder.startAdresse.setText("From: " + currentItem.getStart().getAddressName());
        holder.endAdresse.setText("To: " + currentItem.getDestination().getAddressName());

        holder.estimateFare.setText("Estimated Fare: " + currentItem.getEstimatedCost());
    }

    @Override
    public int getItemCount() {
        return mRequestList.size();
    }

}
