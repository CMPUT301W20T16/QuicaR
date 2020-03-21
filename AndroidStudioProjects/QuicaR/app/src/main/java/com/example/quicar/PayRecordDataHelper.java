package com.example.quicar;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PayRecordDataHelper {
    final String TAG = "quicarDB-payRecord";
    private CollectionReference collectionReferencePay;
    private static PayRecordDataHelper payRecordDataHelper;

    private PayRecordDataHelper() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        collectionReferencePay = db.collection("PayRecords");
    }

    /**
     * This method is the only static method that create a singleton for PayRecordDataHelper
     * @return
     *  return the instance of PayRecordDataHelper singleton
     */
    public static PayRecordDataHelper getInstance() {
        if (payRecordDataHelper == null)
            payRecordDataHelper = new PayRecordDataHelper();
        return payRecordDataHelper;
    }

    /**
     * This is the method that add a record
     * @param newRecord
     *  record to be added
     */
    void addPayRecord(final PayRecord newRecord) {
        collectionReferencePay
                .document()
                .set(newRecord)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  " payment record addition successful");
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "Payment record addition failed" + e.toString());
                    }
                });
    }

    /**
     * This method will query for all records that the user is a rider to obtain selected
     * locations in the past and sort them by date and time of the record in descending order.
     * @param userName
     *  User name
     * @param listener
     *  listener for notification
     */
    void queryHistoryPayments(final String userName, final Integer limit,
                              final OnGetPayDataListener listener) {
        collectionReferencePay
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           if (task.isSuccessful()) {
                               PayRecord query = null;
                               ArrayList<PayRecord> records = new ArrayList<>();
                               for (QueryDocumentSnapshot document : task.getResult()) {
                                   if (limit != null && records.size() == limit)
                                       break;
                                   Log.d(TAG, document.getId() + " => " + document.getData());
                                   query = document.toObject(PayRecord.class);
                                   if (query.getFromUser().getName().equals(userName)
                                           || query.getToUser().getName().equals(userName)) {
                                       records.add(query);
                                   }
                               }
                               if (query == null) {
                                   listener.onFailure(userName + " has no history");
                               } else {
                                   listener.onSuccess(records);
                               }

                           } else {
                               Log.d(TAG, "Error getting documents: ", task.getException());
                               listener.onFailure("Error getting documents: " + task.getException());
                           }
                       }
                   }
                );
    }


}
