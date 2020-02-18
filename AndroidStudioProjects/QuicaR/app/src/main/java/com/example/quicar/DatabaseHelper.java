package com.example.quicar;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DatabaseHelper {
    private FirebaseFirestore db;
    private CollectionReference collectionReferenceRec;
    private CollectionReference collectionReferenceReq;

    static private Integer countRec = 0;
    static private Integer countReq = 0;

    private ArrayList<Record> records = new ArrayList<>();
    private ArrayList<Request> activeRequests = new ArrayList<>();

    final private String REC_COLL_NAME = "Records";
    final private String REQ_COLL_NAME = "Requests";
    final private String TAG = "quicarDB";
    final private String RECORD_KEY = "record_data";
    final private String REQUEST_KEY = "request_data";

    public DatabaseHelper() {
        db = FirebaseFirestore.getInstance();
        collectionReferenceRec = db.collection(REC_COLL_NAME);

        collectionReferenceRec.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    // notification for local and server update
                    Log.d(TAG,"Got a " +
                            (queryDocumentSnapshots.getMetadata().hasPendingWrites() ? "local" : "server")
                            + " update");

                    records.clear();

                    String recordID = "record0";

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getData().get(RECORD_KEY)));
                        recordID = doc.getId();
                        Record record = (Record) doc.getData().get(RECORD_KEY);
                        records.add(record);
                    }
                    // recordID is a string "record" + "ID", so ID is indexing from 6 (length of "record")
                    countRec = Integer.parseInt(recordID.substring(6));
                }
            }
        });

        collectionReferenceReq = db.collection(REQ_COLL_NAME);

        collectionReferenceReq.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    // notification for local and server update
                    Log.d(TAG,"Got a " +
                            (queryDocumentSnapshots.getMetadata().hasPendingWrites() ? "local" : "server")
                            + " update");

                    activeRequests.clear();

                    String requestID = "request0";

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getData().get(REQUEST_KEY)));
                        requestID = doc.getId();
                        Request request = (Request) doc.getData().get(REQUEST_KEY);
                        activeRequests.add(request);
                    }
                    // requestID is a string "request" + "ID", so ID is indexing from 3 (length of "request")
                    countReq = Integer.parseInt(requestID.substring(7));
                }
            }
        });
    }


    public void addRecord(Record record) {
        collectionReferenceRec
                .add(record)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, documentReference.getId() + " addition successful");
                        countRec++;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "Record addition failed" + e.toString());
                    }
                });
    }


    public void delRecord(final String recordID) {
        collectionReferenceRec
                .document(recordID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, recordID + " deletion successful");
                        countRec--;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, recordID + " deletion failed" + e.toString());
                    }
                });
    }


    public void addRecquest(Request request) {
        collectionReferenceReq
                .add(request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, documentReference.getId() + " addition successful");
                        countReq++;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "Request addition failed" + e.toString());
                    }
                });
    }

    public void delRequest(final String requestID) {
        collectionReferenceReq
                .document(requestID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, requestID + " deletion successful");
                        countReq--;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, requestID + " deletion failed" + e.toString());
                    }
                });
    }

}
