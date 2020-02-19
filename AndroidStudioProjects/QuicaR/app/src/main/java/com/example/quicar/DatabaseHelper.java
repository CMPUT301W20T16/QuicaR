package com.example.quicar;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DatabaseHelper {
    private static FirebaseFirestore db;
    private static CollectionReference collectionReferenceRec;
    private static CollectionReference collectionReferenceReq;

    //static private Integer countRec = 0;
    //static private Integer countReq = 0;

    private static ArrayList<Record> records = new ArrayList<>();
    private static ArrayList<Request> activeRequests = new ArrayList<>();

    final private static String REC_COLL_NAME = "Records";
    final private static String REQ_COLL_NAME = "Requests";
    final private static String TAG = "quicarDB";
    final private static String RECORD_KEY = "record_data";
    final private static String REQUEST_KEY = "request_data";

    private static Request query;

    public Request getQueryRequest() {
        return query;
    }

    public void setQueryRequest(Request queryRequest) {
        this.query = queryRequest;
    }

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
                        //delRecord(recordID);
                        Record record = doc.toObject(Record.class);
                        records.add(record);
                    }
                    // recordID is a string "record" + "ID", so ID is indexing from 6 (length of "record")
                    // countRec = Integer.parseInt(recordID.substring(6));
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
                        //delRequest(requestID);
                        Request request = doc.toObject(Request.class);
                        activeRequests.add(request);
                    }
                    // requestID is a string "request" + "ID", so ID is indexing from 3 (length of "request")
                    //countReq = Integer.parseInt(requestID.substring(7));
                }
            }
        });
    }


    public void addRecord(Record record) {
//        HashMap<String, Record> myRecord = new HashMap<>();
//        myRecord.put(REQUEST_KEY, record);
        collectionReferenceRec
                .document()
                .set(record)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  " record addition successful");
                        //countRec++;
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
                        //countRec--;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, recordID + " deletion failed" + e.toString());
                    }
                });
    }


    public void addRequest(Request request) {
//        HashMap<String, Request> myRequest = new HashMap<>();
//        myRequest.put(REQUEST_KEY, request);

        collectionReferenceReq
                .document()
                .set(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  " request addition successful");
                        //countReq++;
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
                        //countReq--;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, requestID + " deletion failed" + e.toString());
                    }
                });
    }

    public Request queryRequest(String username) {
         // final Request request;

//         collectionReferenceReq
//                .whereEqualTo("rider.name", username)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                //setQueryRequest(document.toObject(Request.class));
//                                query = document.toObject(Request.class);
//                                System.out.println("*****************************");
//                                System.out.println(query.getRider().getName());
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                            //queryRequest = null;
//                        }
//                    }
//            });
         Task<QuerySnapshot> task = collectionReferenceReq.whereEqualTo("rider.name", username).get();
         // wait until the query is done
         while (!task.isSuccessful()) {}

         for (QueryDocumentSnapshot document : task.getResult()) {
             Log.d(TAG, document.getId() + " => " + document.getData());
             //setQueryRequest(document.toObject(Request.class));
             query = document.toObject(Request.class);
             System.out.println("*****************************");
             System.out.println(query.getRider().getName());
         }

         return query;
    }

}
