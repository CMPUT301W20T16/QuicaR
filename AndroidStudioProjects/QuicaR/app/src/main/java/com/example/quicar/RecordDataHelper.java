package com.example.quicar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;


/**
 * This class extend DatabaseHelper and mainly handle records data
 */
public class RecordDataHelper extends DatabaseHelper {
    private static CollectionReference collectionReferenceRec;

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
}
