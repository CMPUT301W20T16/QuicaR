package com.example.quicar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ComplaintDataHelper {
    final String TAG = "quicarDB-payRecord";
    private CollectionReference collectionReferenceCom;
    private static ComplaintDataHelper complaintDataHelper;

    private ComplaintDataHelper() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        collectionReferenceCom = db.collection("PayRecords");
    }

    /**
     * This method is the only static method that create a singleton for ComplaintDataHelper
     * @return
     *  return the instance of ComplaintDataHelper singleton
     */
    public static ComplaintDataHelper getInstance() {
        if (complaintDataHelper == null)
            complaintDataHelper = new ComplaintDataHelper();
        return complaintDataHelper;
    }

    /**
     * This is the method that add a record
     * @param newRecord
     *  record to be added
     */
    void addComplaint(final ComplaintRecord newRecord) {
        collectionReferenceCom
                .document()
                .set(newRecord)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  " complaint record addition successful");
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "Complaint record addition failed" + e.toString());
                    }
                });
    }
}
