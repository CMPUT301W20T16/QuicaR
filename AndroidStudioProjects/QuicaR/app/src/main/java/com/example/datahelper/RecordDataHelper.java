package com.example.datahelper;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.entity.Location;
import com.example.listener.OnGetRecordDataListener;
import com.example.entity.Record;
import com.example.util.PopUpNotification;
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        collectionReferenceRec = db.collection("Records");

        collectionReferenceRec.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen error", e);
                    return;
                }
                if (queryDocumentSnapshots != null) {
                    // notification for local and server update
                    Log.d(TAG,"Got a " +
                            (queryDocumentSnapshots.getMetadata().hasPendingWrites() ? "local" : "server")
                            + " update for records");
                    ArrayList<Record> records = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Record record = doc.toObject(Record.class);
                        records.add(record);
                    }
                    checkCompleteNotification(records);
                }
            }
        });
    }

    /**
     * This method is the only static method that create a singleton for RecordDataHelper
     * @return
     *  return the instance of RecordDataHelper singleton
     */
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
    public void queryHistoryLocation(final String userName, final Integer limit,
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

    /**
     * This method check if there is a notification needed to be sent to the rider
     * that the rider's request is completed
     * @param records
     *  list of records
     */
    private void checkCompleteNotification(ArrayList<Record> records) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

        if (databaseHelper.getCurrentUser() == null)
            return;

        UserState userState = databaseHelper.getUserState();
        for (Record record: records) {
            if (record.getRequest().getRider().getName().equals(databaseHelper.getCurrentUserName())
                    && databaseHelper.getCurrentMode().equals("rider")
                    && userState.getRequestID().equals(record.getRequest().getRid())) {
                // might want to check if userstate.getOngoing is updated
                new PopUpNotification("Hey " + databaseHelper.getCurrentUserName()
                        , "your ride is completed")
                        .build();
                // update user state of rider
                userState.setActive(Boolean.FALSE);
                userState.setOnGoing(Boolean.FALSE);
                userState.setOnArrived(Boolean.FALSE);
                userState.setCurrentRequest(null);
                databaseHelper.setUserState(userState);
                RequestDataHelper.getInstance().notifyComplete();
                System.out.println("-------- Notification sent --------");
                break;
            }
        }
    }
}
