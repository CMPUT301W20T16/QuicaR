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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This is the class that handle data transfer between the app and firebase
 */
public class DatabaseHelper {
    private static final String REC_COLL_NAME = "Records";
    private static final String REQ_COLL_NAME = "Requests";
    private static final String USER_COLL_NAME = "Users";

    private static final String RECORD_KEY = "record_data";
    private static final String REQUEST_KEY = "request_data";
    private static final String USER_KEY = "user_account";

    protected static final String TAG = "quicarDB";

    private static FirebaseFirestore db;
    protected static CollectionReference collectionReferenceRec;
    protected static CollectionReference collectionReferenceReq;
    protected static CollectionReference collectionReferenceUser;

    private static ArrayList<Record> records = new ArrayList<>();
    private static ArrayList<Request> requests = new ArrayList<>();
    private static ArrayList<User> users = new ArrayList<>();

    private static String currentUserName;

    /**
     * This is the constructor of database helper which initialize the firebase instance
     * and set up values for interacting.
     */
    public DatabaseHelper() {
        db = FirebaseFirestore.getInstance();

        //  Configure offline persistence
        // The default cache size threshold is 100 MB. Configure "setCacheSizeBytes"
        // for a different threshold (minimum 1 MB) or set to "CACHE_SIZE_UNLIMITED"
        // to disable clean-up.
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);

        collectionReferenceRec = db.collection(REC_COLL_NAME);
        collectionReferenceReq = db.collection(REQ_COLL_NAME);
        collectionReferenceUser = db.collection(USER_COLL_NAME);

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
                            + " update for record");

                    DatabaseHelper.records.clear();

                    String recordID = "record0";

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getData().get(RECORD_KEY)));
                        recordID = doc.getId();
//                        delRecord(recordID);
                        Record record = doc.toObject(Record.class);
                        DatabaseHelper.records.add(record);
                    }
                }
            }
        });

        collectionReferenceReq.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    // notification for local and server update
                    Log.d(TAG,"Got a " +
                            (queryDocumentSnapshots.getMetadata().hasPendingWrites() ? "local" : "server")
                            + " update for request");

                    DatabaseHelper.requests.clear();

                    String requestID = "request0";

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getData().get(REQUEST_KEY)));
                        requestID = doc.getId();
//                        delRequest(requestID);
                        Request request = doc.toObject(Request.class);
                        DatabaseHelper.requests.add(request);
                    }
                }
            }
        });

        collectionReferenceUser.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    // notification for local and server update
                    Log.d(TAG,"Got a " +
                            (queryDocumentSnapshots.getMetadata().hasPendingWrites() ? "local" : "server")
                            + " update for user account");

                    DatabaseHelper.users.clear();

                    String userID = "request0";

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getData().get(USER_KEY)));
                        userID = doc.getId();
//                        delUser(userID);
                        User user = doc.toObject(User.class);
                        DatabaseHelper.users.add(user);
                    }
                }
            }
        });
    }

    /**
     * This is the method that return the current user name stored locally
     * @return
     *  current user name
     */
    public static String getCurrentUserName() {
        return currentUserName;
    }

    /**
     * This is the method that set the current user name locally
     * @param currentUserName
     *  user name
     */
    public static void setCurrentUserName(String currentUserName) {
        DatabaseHelper.currentUserName = currentUserName;
    }

}
