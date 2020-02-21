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

public class DatabaseHelper {
    private static final String REC_COLL_NAME = "Records";
    private static final String REQ_COLL_NAME = "Requests";
    private static final String USER_COLL_NAME = "Users";
    private static final String TAG = "quicarDB";
    private static final String RECORD_KEY = "record_data";
    private static final String REQUEST_KEY = "request_data";
    private static final String USER_KEY = "user_account";

    private static FirebaseFirestore db;
    private static CollectionReference collectionReferenceRec;
    private static CollectionReference collectionReferenceReq;
    private static CollectionReference collectionReferenceUser;

    private static ArrayList<Record> records = new ArrayList<>();
    private static ArrayList<Request> requests = new ArrayList<>();
    private static ArrayList<User> users = new ArrayList<>();

    private static String currentUserName;

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


    public static String getCurrentUserName() {
        return currentUserName;
    }

    public static void setCurrentUserName(String currentUserName) {
        DatabaseHelper.currentUserName = currentUserName;
    }

    private static void addRecord(final Record record) {
        collectionReferenceRec
                .document()
                .set(record)
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


    private static void addRequest(final Request request, final OnGetRequestDataListener listener) {
        collectionReferenceReq
                .document()
                .set(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  " request addition successful");
                        listener.onSuccessAddRequest();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "Request addition failed" + e.toString());
                        listener.onFailure("Request addition failed" + e.toString());
                    }
                });
    }


    public static void addNewRequest(final Request request, final OnGetRequestDataListener listener) {
        if (request == null)
            listener.onFailure("request provided is a null object");
        final String riderName = request.getRider().getName();
        collectionReferenceReq
                .whereEqualTo("rider.account.userName", riderName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            Request query = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                query = document.toObject(Request.class);
                                count++;
                            }
                            if (count > 0) {
                                System.out.println("*****  user \" " + riderName + " \" already has one request");
                                listener.onFailure(riderName + " already has one request");
                            } else {
                                DatabaseHelper.addRequest(request, listener);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }


    private static void delRequest(final String requestID) {
        collectionReferenceReq
                .document(requestID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, requestID + " deletion successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, requestID + " deletion failed" + e.toString());
                    }
                });
    }


    private static void updateRequest(final String docID, final Request request,
                                      final OnGetRequestDataListener listener) {
        collectionReferenceReq
                .document(docID)
                .set(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  " Request update successful");
                        listener.onSuccessSetActive();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "Request update failed" + e.toString());
                        listener.onFailure("Request update failed" + e.toString());
                    }
                });
    }


    private static void addUser(final User newUser, final OnGetUserDataListener listener) {
        collectionReferenceUser
                .document()
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  "User addition successful");
                        listener.onSuccessAddUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "User addition failed" + e.toString());
                    }
                });
    }

    private static void delUser(final String userID) {
        collectionReferenceUser
                .document(userID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, userID + " deletion successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, userID + " deletion failed" + e.toString());
                    }
                });
    }


    private static void updateUser(final User user, final String userID, final OnGetUserDataListener listener) {
        collectionReferenceUser
                .document(userID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  "User update successful");
                        listener.onSuccessUpdateUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "User update failed" + e.toString());
                        listener.onFailure("User update failed" + e.toString());
                    }
                });
    }


    public static void addNewUser(final User user, final OnGetUserDataListener listener) {
        if (user == null)
            listener.onFailure("user provided is a null object");
        final String userName = user.getName();
        collectionReferenceUser
                .whereEqualTo("account.userName", userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            User user = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                user = document.toObject(User.class);
                                count++;
                            }
                            if (count > 0) {
                                System.out.println("*****  user \" " + userName + " \" has an existing account");
                                listener.onFailure(userName + " has anexisting request");
                            } else {
                                DatabaseHelper.addUser(user, listener);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }


    public static void updateUserProfile(final String userName, final OnGetUserDataListener listener) {
        if (userName == null || userName.length() == 0)
            listener.onFailure("user name provided is a null or empty");
        collectionReferenceUser
                .whereEqualTo("account.userName", userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            User user = null;
                            String userID = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                user = document.toObject(User.class);
                                userID = document.getId();
                                count++;
                            }
                            if (count > 1) {
                                System.out.println("*****  user \" " + userName + " \" has more than one account");
                                listener.onFailure(userName + " has more than one request");
                            } else {
                                DatabaseHelper.updateUser(user, userID, listener);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }


    public static void queryRiderOpenRequest(final String riderName, final OnGetRequestDataListener listener) {
        if (riderName == null || riderName.length() == 0)
            listener.onFailure("rider name provided is a null or empty");
        collectionReferenceReq
                .whereEqualTo("rider.account.userName", riderName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            Request query = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                query = document.toObject(Request.class);
                                count++;
                            }
                            if (count > 1) {
                                System.out.println("*****  user \" " + riderName + " \" has more than one request");
                                listener.onFailure(riderName + " has more than one request");
                            } else {
                                listener.onSuccessRiderOpenRequest(query);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }


    public static void queryDriverActiveRequest(final String driverName, final OnGetRequestDataListener listener) {
        if (driverName == null || driverName.length() == 0)
            listener.onFailure("driver name provided is a null or empty");
        collectionReferenceReq
                .whereEqualTo("driver.account.userName", driverName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            Request query = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                query = document.toObject(Request.class);
                                count++;
                            }
                            if (count > 1) {
                                System.out.println("*****  user \" " + driverName + " \" has more than one request");
                                listener.onFailure(driverName + " has more than one request");
                            } else {
                                listener.onSuccessDriverActiveRequest(query);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }


    public static void queryAllOpenRequests(final Location location, final OnGetRequestDataListener listener) {
        //listener.onStart();
        Float latRange = 10.0f;
        Float lonRange = 10.0f;

        collectionReferenceReq
                .whereEqualTo("isAccepted", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Request> activeRequests = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                activeRequests.add(document.toObject(Request.class));
                            }
                            listener.onSuccessAllOpenRequests(activeRequests);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });

//        .whereLessThan("start.lat", location.getLat() + latRange)
//                .whereGreaterThan("start.lat", location.getLat() - latRange)
//                .whereLessThan("destination.lat", location.getLon() + lonRange)
//                .whereGreaterThan("destination.lat", location.getLon() - lonRange)
    }


    public static void setRequestActive(final String riderName, final User driver,
                                        final OnGetRequestDataListener listener) {
        if (riderName == null || riderName.length() == 0)
            listener.onFailure("rider name provided is a null or empty");
        else if (driver == null)
            listener.onFailure("driver provided is a null object");
        collectionReferenceReq
                .whereEqualTo("rider.account.userName", riderName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            Request query = null;
                            String docID = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                query = document.toObject(Request.class);
                                docID = document.getId();
                                count++;
                            }
                            if (count > 1) {
                                System.out.println("*****  user \" " + riderName + " \" has more than one request");
                                listener.onFailure(riderName + " has more than one request");
                            } else if (query == null) {
                                listener.onFailure(riderName + " has no request");
                            } else {
                                query.setAccepted(true);
                                query.setDriver(driver);
                                System.out.println("***** " + docID);
                                DatabaseHelper.updateRequest(docID, query, listener);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }

    public static void cancelRequest(final String riderName, final OnGetRequestDataListener listener) {
        if (riderName == null || riderName.length() == 0)
            listener.onFailure("rider name provided is a null or empty");
        collectionReferenceReq
                .whereEqualTo("rider.account.userName", riderName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            Request query = null;
                            String docID = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                query = document.toObject(Request.class);
                                docID = document.getId();
                                count++;
                            }
                            if (count > 1) {
                                System.out.println("*****  user \" " + riderName + " \" has more than one request");
                                listener.onFailure(riderName + " has more than one request");
                            } else {
                                if (query == null) {
                                    listener.onFailure("Deletion unsuccessful: " +
                                            riderName + " has no request");
                                } else if (query.getAccepted() == true) {
                                    listener.onFailure("Deletion denied: " +
                                            riderName + " has an ongoing request");
                                } else {
                                    System.out.println("***** " + docID);
                                    DatabaseHelper.delRequest(docID);
                                    listener.onSuccessCancel();
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }

    public static void completeRequest(final String driverName, final Float payment, final Float rating,
                                       final OnGetRequestDataListener listener) {
        if (driverName == null || driverName.length() == 0)
            listener.onFailure("driver name provided is a null or empty");
        collectionReferenceReq
                .whereEqualTo("driver.account.userName", driverName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            Request query = null;
                            String docID = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                query = document.toObject(Request.class);
                                docID = document.getId();
                                count++;
                            }
                            if (count > 1) {
                                System.out.println("*****  user \" " + driverName + " \" has more than one request");
                                listener.onFailure(driverName + " has more than one request");
                            } else {
                                if (query == null) {
                                    listener.onFailure("Deletion unsuccessful: " +
                                            driverName + " has no request");
                                } else if (query.getAccepted() != true) {
                                    listener.onFailure("Deletion denied: " +
                                            driverName + " has no ongoing request");
                                } else {
                                    System.out.println("***** " + docID);
                                    DatabaseHelper.delRequest(docID);
                                    Record record = new Record(query, payment, rating);
                                    DatabaseHelper.addRecord(record);
                                    listener.onSuccessComplete();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }
}
