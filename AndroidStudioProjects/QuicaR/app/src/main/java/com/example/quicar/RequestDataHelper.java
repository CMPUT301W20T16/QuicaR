package com.example.quicar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

/**
 * This class extend DatabaseHelper and mainly handle requests data
 */
public class RequestDataHelper extends DatabaseHelper {

    public static final String USER_REQ_TAG = "user query request";
    public static final String ALL_REQs_TAG = "all opened request";
    public static final String ADD_REQ_TAG = "add new request";
    public static final String SET_ACTIVE_TAG = "set request active";
    public static final String SET_PICKEDUP_TAG = "set request picked up";
    public static final String CANCEL_REQ_TAG = "cancel opened request";
    public static final String COMPLETE_REQ_TAG = "complete request";

    private static OnGetRequestDataListener listener;
    private static CollectionReference collectionReferenceReq;
    private static FirebaseFirestore db;

    /**
     * This is the constructor of RequestDataHelper
     */
    public RequestDataHelper() {
        super();
        RequestDataHelper.collectionReferenceReq = super.getCollectionReferenceReq();
        RequestDataHelper.db = super.getDb();
    }

    /**
     * This method set up the listener for notification of active request
     * @param listener
     *  listener for notification
     */
    public static void setOnNotifyListener(OnGetRequestDataListener listener) {
        RequestDataHelper.listener = listener;
    }

    /**
     * This method will notify the latest listener stored in this class that the request is set to
     * active, which means there is a driver accepted the rider's request
     * @param request
     *  the request that is active
     */
    public static void notifyActive(Request request) {
        if (listener != null) {
            listener.onActiveNotification(request);
        }
    }

    public static void notifyCancel() {
        if (listener != null) {
            listener.onCancelNotification();
        }
    }

    public static void notifyPickedUp(Request request) {
        if (listener != null) {
            listener.onPickedUpNotification(request);
        }
    }

    public static void notifyComplete() {
        if (listener != null)
            listener.onCompleteNotification();
    }

    /**
     * This is the method that add request to firebase and will notify the listener when success
     * @param newRequest
     *  request to be added
     * @param listener
     *  listener for notification
     */
    private static void addRequest(final Request newRequest, final OnGetRequestDataListener listener) {
        ArrayList<Request> requests = new ArrayList<Request>();
        requests.add(newRequest);
        collectionReferenceReq
                .document(newRequest.getRid())
                .set(newRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  " request addition successful");
                        listener.onSuccess(requests, ADD_REQ_TAG);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "Request addition failed" + e.toString());
                        listener.onFailure("Request addition failed" + e.toString(), ADD_REQ_TAG);
                    }
                });
    }

    /**
     * This is the method that check if the request is valid and call addRequest method
     * @param newRequest
     *  candidate request to be added
     * @param listener
     *  listener for notification
     */
    public static void addNewRequest(final Request newRequest, final OnGetRequestDataListener listener) {
        if (newRequest == null || newRequest.getRider().getName() == null
                || newRequest.getRider().getName().length() == 0) {
            listener.onFailure("invalid parameters", ADD_REQ_TAG);
            return;
        }
        String id = db.collection("collection_name").document().getId();
        newRequest.setRid(id);
        RequestDataHelper.addRequest(newRequest, listener);
    }

    /**
     * This is the method that delete request from firebase
     * @param requestID
     *  id of request to be deleted
     */
    private static void delRequest(final String requestID, final OnGetRequestDataListener listener,
                                   final String mode) {
        collectionReferenceReq
                .document(requestID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, requestID + " deletion successful");
                        if (mode == "cancel")
                            listener.onSuccess(null, CANCEL_REQ_TAG);
                        else if (mode == "complete")
                            listener.onSuccess(null, COMPLETE_REQ_TAG);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, requestID + " deletion failed" + e.toString());
                        if (mode == "cancel")
                            listener.onFailure(requestID + " deletion failed", CANCEL_REQ_TAG);
                        else if (mode == "complete")
                            listener.onFailure(requestID + " deletion failed", COMPLETE_REQ_TAG);
                    }
                });
    }

    /**
     * This is the method that update request in firebase
     * @param requestID
     *  document id of the request
     * @param request
     *  new request ot be updated
     * @param listener
     *  listener for notification
     */
    private static void updateRequest(final String requestID, final Request request,
                                      final OnGetRequestDataListener listener,
                                      final boolean setActiveMode) {

        final DocumentReference reqDocRef = collectionReferenceReq.document(requestID);

        db.runTransaction(new Transaction.Function<String>() {
          @Override
          public String apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(reqDocRef);
                System.out.println("docSnapshot---------" + snapshot);
                Request requestTmp = snapshot.toObject(Request.class);
                System.out.println( "request temp---------" + requestTmp);
                if (!requestTmp.getAccepted()) {
                    transaction.set(reqDocRef, request);
                    return requestID;
                } else {
                    throw new FirebaseFirestoreException("Request has already been accepted",
                            FirebaseFirestoreException.Code.ABORTED);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String docID) {
                Log.d(TAG, "Transaction success: " + docID);
                if (setActiveMode)
                    listener.onSuccess(null, SET_ACTIVE_TAG);
                else
                    listener.onSuccess(null, SET_PICKEDUP_TAG);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
                if (setActiveMode)
                    listener.onFailure("Transaction failure. " + e, SET_ACTIVE_TAG);
                else
                    listener.onFailure("Transaction failure. " + e, SET_PICKEDUP_TAG);
            }
        });
    }


    /**
     * This method will check if the request is opened and return request data to listener
     * @param userName
     *  user name of query
     * @param mode
     *  mode of the user
     * @param listener
     *  listener for notification and obtain return value
     */
    public static void queryUserRequest(final String userName, final String mode,
                                             final OnGetRequestDataListener listener) {
        if (userName == null || userName.length() == 0) {
            listener.onFailure("invalid parameters", USER_REQ_TAG);
            return;
        }
        String field;
        if (mode == "rider")
            field = "rider.account.userName";
        else
            field = "driver.account.userName";

        collectionReferenceReq
                .whereEqualTo(field, userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            ArrayList<Request> requests = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                requests.add(document.toObject(Request.class));
                                count++;
                            }
                            listener.onSuccess(requests, USER_REQ_TAG);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure(
                                    "Error getting documents: " + task.getException(),
                                    USER_REQ_TAG);
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
    public static void queryAllOpenRequests(final OnGetRequestDataListener listener) {

        collectionReferenceReq
                .whereEqualTo("isAccepted", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Request> openRequests = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Request query = document.toObject(Request.class);
                                openRequests.add(query);
                            }
                            listener.onSuccess(openRequests, ALL_REQs_TAG);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure(
                                    "Error getting documents: " + task.getException(),
                                    ALL_REQs_TAG);
                        }
                    }
                });
    }

    /**
     * This method will check if there is an inactive request belongs to the rider and set the
     * request to active, then call the updateRequest method
     * @param requestID
     *  id of the request
     * @param driver
     *  driver of the request to be set
     * @param listener
     *  listener for notification
     */
    public static void setRequestActive(final String requestID, final User driver, final Float offeredPrice,
                                        final OnGetRequestDataListener listener) {
        if (requestID == null || requestID.length() == 0) {
            listener.onFailure("invalid parameters", SET_ACTIVE_TAG);
            return;
        } else if (driver == null || driver.getName() == null || driver.getName().length() == 0) {
            listener.onFailure("invalid parameters", SET_ACTIVE_TAG);
            return;
        }

        collectionReferenceReq
                .whereEqualTo("requestID", requestID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            Request query = null;
//                            String docID = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                query = document.toObject(Request.class);
//                                docID = document.getId();
                                count++;
                            }
                            if (query == null) {
                                listener.onFailure(requestID + " does not exist", SET_ACTIVE_TAG);
                            } else if (query.getAccepted()) {
                                listener.onFailure(requestID + " is already active",
                                        SET_ACTIVE_TAG);
                            } else {
                                query.setAccepted(true);
                                query.setEstimatedCost(offeredPrice);
                                query.setDriver(driver);
                                System.out.println("***** " + requestID);
                                RequestDataHelper.updateRequest(requestID, query, listener, true);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException(),
                                    SET_ACTIVE_TAG);
                        }
                    }
                });
    }

    /**
     * This method will check if there is an active and not picked up request belongs to the rider and set the
     * request to picked up, then call the updateRequest method
     * @param requestID
     *  id of the request
     * @param driver
     * @param listener
     */
    public static void setRequestPickedUp(final String requestID, final User driver,
                                        final OnGetRequestDataListener listener) {
        if (requestID == null || requestID.length() == 0) {
            listener.onFailure("invalid parameters", SET_PICKEDUP_TAG);
            return;
        } else if (driver == null) {
            listener.onFailure("invalid parameters", SET_PICKEDUP_TAG);
            return;
        }

        collectionReferenceReq
                .whereEqualTo("requestID", requestID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            Request query = null;
//                            String docID = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                query = document.toObject(Request.class);
//                                docID = document.getId();
                                count++;
                            }
                            if (query == null) {
                                listener.onFailure(requestID + " does not exist",
                                        SET_PICKEDUP_TAG);
                            } else if (!query.getAccepted()) {
                                listener.onFailure(requestID + " is not active yet",
                                        SET_PICKEDUP_TAG);
                            } else if (query.getPickedUp()) {
                                listener.onFailure(requestID + " is picked up already",
                                        SET_PICKEDUP_TAG);
                            } else {
                                query.setAccepted(true);
                                query.setDriver(driver);
                                System.out.println("***** " + requestID);
                                RequestDataHelper.updateRequest(requestID, query, listener, false);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException(),
                                    SET_PICKEDUP_TAG);
                        }
                    }
                });
    }

    /**
     * This method will check if an inactive request exists and call the delRequest method
     * @param requestID
     *  rider name of query
     * @param listener
     *  listener for notification
     */
    public static void cancelRequest(final String requestID, final OnGetRequestDataListener listener) {
        if (requestID == null || requestID.length() == 0) {
            listener.onFailure("invalid parameters", CANCEL_REQ_TAG);
            return;
        }
        collectionReferenceReq
                .whereEqualTo("requestID", requestID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            Request query = null;
//                            String docID = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                query = document.toObject(Request.class);
//                                docID = document.getId();
                                count++;
                            }
                            if (query == null) {
                                    listener.onFailure("Deletion unsuccessful: " +
                                            requestID + " does not exist", CANCEL_REQ_TAG);
                            } else if (query.getPickedUp() == true) {
                                listener.onFailure("Deletion denied: " +
                                        requestID + " is an ongoing request", CANCEL_REQ_TAG);
                            } else {
                                System.out.println("***** " + requestID);
                                RequestDataHelper.delRequest(requestID, listener, "cancel");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException(),
                                    CANCEL_REQ_TAG);
                        }
                    }
                });
    }

    /**
     * This method will check if an active request exists and call delRequest method follow by
     * addRecord method
     * @param requestID
     *  id of the request
     * @param payment
     *  payment of the request
     * @param rating
     *  rating of the request
     * @param listener
     *  listener for notification
     */
    public static void completeRequest(final String requestID, final Float payment,
                                       final Float rating,
                                       final OnGetRequestDataListener listener) {
        if (requestID == null || requestID.length() == 0) {
            listener.onFailure("invalid parameters", COMPLETE_REQ_TAG);
            return;
        }
        collectionReferenceReq
                .whereEqualTo("requestID", requestID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            //  this for loop should only loop for once
                            //  user should not have more than one requests exist in the db
                            int count = 0;
                            Request query = null;
//                            String docID = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                query = document.toObject(Request.class);
//                                docID = document.getId();
                                count++;
                            }
                            if (query == null) {
                                listener.onFailure("Deletion unsuccessful: " +
                                        requestID + " does not exist", COMPLETE_REQ_TAG);
                            } else if (query.getAccepted() != true || query.getPickedUp() != true) {
                                listener.onFailure("Deletion denied: " +
                                        requestID + " is not an ongoing request", COMPLETE_REQ_TAG);
                            } else {
                                System.out.println("***** " + requestID);
                                RequestDataHelper.delRequest(requestID, listener, "complete");
                                Record record = new Record(query, payment, rating);
                                RecordDataHelper.addRecord(record);
                                listener.onSuccess( null, COMPLETE_REQ_TAG);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException(),
                                    COMPLETE_REQ_TAG);
                        }
                    }
                });
    }

}