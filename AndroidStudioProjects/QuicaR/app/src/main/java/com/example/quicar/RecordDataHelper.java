package com.example.quicar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * This class extend DatabaseHelper and mainly handle records data
 */
public class RecordDataHelper {
    final String TAG = "quicarDB-record";
    private CollectionReference collectionReferenceRec;
    private static RecordDataHelper recordDataHelper;

    /**
     * This is the constructor of RecordDataHelper
     */
    private RecordDataHelper() {
        collectionReferenceRec = DatabaseHelper.getInstance().getCollectionReferenceRec();
    }

    public static RecordDataHelper getInstance() {
        if (recordDataHelper == null)
            recordDataHelper = new RecordDataHelper();
        return recordDataHelper;
    }

    /**
     * This is the method that add a record
     * @param newRecord
     *  record to be added
     */
    void addRecord(final Record newRecord) {
        collectionReferenceRec
                .document()
                .set(newRecord)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  " record addition successful");
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "Record addition failed" + e.toString());
                    }
                });
    }

    /**
     * This is the method that delete a record from firebase
     * @param recordID
     *  id of record to be deleted
     */
    void delRecord(final String recordID) {
        collectionReferenceRec
                .document(recordID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, recordID + " deletion successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, recordID + " deletion failed" + e.toString());
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
    void queryHistoryLocation(final String userName, final Integer limit,
                              final OnGetRecordDataListener listener) {
        collectionReferenceRec
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Record query = null;
                                ArrayList<Location> locations = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (limit != null && locations.size() == limit)
                                        break;
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    query = document.toObject(Record.class);
                                    //  add start locations in the record of rider name is current user name
                                    Location start_location = query.getRequest().getStart();
                                    Location destination = query.getRequest().getDestination();
                                    if (query.getRequest().getRider().getName().equals(userName)) {
                                        if (!locations.contains(start_location)) {
                                            locations.add(start_location);
                                        }
                                        if (!locations.contains(destination)) {
                                            locations.add(destination);
                                        }
                                    }
                                }
                                if (query == null) {
                                    listener.onFailure(userName + " has no history");
                                } else {
                                    listener.onSuccess(locations);
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
