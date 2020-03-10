package com.example.quicar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Implement a dialog fragment for delete function
 * User is able to select between "ok" and "cancel"
 * Selecting "ok" results in the application deleting selected measurement record
 * source: CMPUT 301 lab 3 activity
 */

public class DriverAcceptRideDialogue extends DialogFragment {


    DriverAcceptRideDialogue.OnFragmentInteractionListener listener;

    /**
     * If user pressed OK, remove selected measurement record
     * the actual method is implemented in MainActivity
     */
    public interface OnFragmentInteractionListener {
        void onOkPressed();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.driver_accept_ride_dialogue, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("ACCEPTED")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onOkPressed();
                    }}).create();
    }
}
