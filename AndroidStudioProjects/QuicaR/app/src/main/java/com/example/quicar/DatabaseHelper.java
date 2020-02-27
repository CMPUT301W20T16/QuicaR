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

    /**
     * This is the method that add request to firebase and will notify the listener when success
     * @param request
     *  request to be added
     * @param listener
     *  listener for notification
     */
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

    /**
     * This is the method that check if the request is valid and call addRequest method
     * @param request
     *  candidate request to be added
     * @param listener
     *  listener for notification
     */
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

    /**
     * This is the method that delete request from firebase
     * @param requestID
     *  id of request to be deleted
     */
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

    /**
     * This is the method that update request in firebase
     * @param docID
     *  document id of the request
     * @param request
     *  new request ot be updated
     * @param listener
     *  listener for notification
     */
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

    /**
     * This is the method that add a user to firebase
     * @param newUser
     *  new user to be added
     * @param listener
     *  listener for notification
     */
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

    /**
     * This is the method that delete a user from firebase
     * @param userID
     *  id of user to be deleted
     */
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

    /**
     * This id the method that update a user in firebase
     * @param user
     *  user ot be updated
     * @param userID
     *  id of the user
     * @param listener
     *  listener for notification
     */
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

    /**
     * This method will check if the user already exists and call addUser method
     * @param user
     *  new user to be added
     * @param listener
     *  listener for notification
     */
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

    /**
     * This method will check if the user exists and call updateUser method
     * @param userName
     *  user name of the user to be updated
     * @param listener
     *  listener for notification
     */
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

    /**
     * This method will check if the request is opened and return request data to listener
     * @param riderName
     *  rider name of query
     * @param listener
     *  listener for notification and obtain return value
     */
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

    /**
     * This method will check if there is an active request belongs to the driver and return the
     * request data to listener
     * @param driverName
     *  driver name of query
     * @param listener
     *  listener for notification and obtain return value
     */
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

    /**
     * This method will query all open request and return a list of request to listener
     * @param location
     *  location of the driver
     * @param listener
     *  listener for notification and obtain return value
     */
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

    /**
     * This method will check if there is an inactive request belongs to the rider and set the
     * request to active, then call the updateRequest method
     * @param riderName
     *  rider name of query
     * @param driver
     *  driver of the request to be set
     * @param listener
     *  listener for notification
     */
    public static void setRequestActive(final String riderName, final User driver,
                                        final OnGetRequestDataListener listener) {
//        if (riderName == null || riderName.length() == 0)
//            listener.onFailure("rider name provided is a null or empty");
//        else if (driver == null)
//            listener.onFailure("driver provided is a null object");
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
                            } else if (query.getAccepted() == Boolean.TRUE) {
                                listener.onFailure(riderName + "has an active request already");
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

    /**
     * This method will check if an inactive request exists and call the delRequest method
     * @param riderName
     *  rider name of query
     * @param listener
     *  listener for notification
     */
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

    /**
     * This method will check if an active request exists and call delRequest method follow by
     * addRecord method
     * @param driverName
     *  driver name of the query
     * @param payment
     *  payment of the request
     * @param rating
     *  rating of the request
     * @param listener
     *  listener for notification
     */
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
