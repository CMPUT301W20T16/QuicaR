package com.example.quicar;

import android.content.Intent;
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
public class RecordDataHelper extends DatabaseHelper {
    private static CollectionReference collectionReferenceRec;
    //private static String GET_HISTORY_LOC = "get history locations";

    /**
     * This is the constructor of RecordDataHelper
     */
    public RecordDataHelper() {
        super();
        RecordDataHelper.collectionReferenceRec = super.getCollectionReferenceRec();
    }

    /**
     * This is the method that add a record
     * @param newRecord
     *  record to be added
     */
    public static void addRecord(final Record newRecord) {
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
    private static void delRecord(final String recordID) {
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


    public static void queryHistoryLocation(String userName, Integer limit, OnGetRecordDataListener listener) {
        int limitNum = 10;  //  default value
        if (limit != null) {
            limitNum = limit;
        }

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
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        query = document.toObject(Record.class);
                                        //  add start locations in the record of rider name is current user name
                                        if (query.getRequest().getRider().getName().equals(userName))
                                            locations.add(query.getRequest().getStart());
                                        //System.out.println(query.getDateTime());
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
