package com.example.quicar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

public class RiderDialog extends AppCompatDialogFragment {
    private TextView textRider;
    public boolean isOpen = true;

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.rider_dialog, null);

        builder.setView(view)
                .setTitle("Rider Information")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isOpen = false;
                    }
                });
        textRider = view.findViewById(R.id.rider_info);
        textRider.setText(ScanActivity.result.getText());

        return builder.create();
    }
}
