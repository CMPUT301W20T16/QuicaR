package com.example.datahelper;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.listener.OnGetRequestDataListener;
import com.example.entity.Record;
import com.example.entity.Request;
import com.example.user.User;
import com.example.util.PopUpNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

/**
 * This class extend DatabaseHelper and mainly handle requests data
 */
public class RequestDataHelper {
    final String TAG = "quicarDB-request";

    public static final String USER_REQ_TAG = "user query request";
    public static final String ALL_REQs_TAG = "all opened request";
    public static final String ADD_REQ_TAG = "add new request";
    public static final String SET_ACTIVE_TAG = "set request active";
    public static final String SET_PICKEDUP_TAG = "set request picked up";
    public static final String SET_ARRIVED_TAG = "set request arrived";
    public static final String CANCEL_REQ_TAG = "cancel opened request";
    public static final String COMPLETE_REQ_TAG = "complete request";

    private OnGetRequestDataListener notifyListener;
    private CollectionReference collectionReferenceReq;
    private static RequestDataHelper requestDataHelper;

    /**
     * This is the constructor of RequestDataHelper
     */
    private RequestDataHelper() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        collectionReferenceReq = db.collection("Requests");

        collectionReferenceReq.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    // notification for local and server update
                    Log.d(TAG,"Got a " +
                            (queryDocumentSnapshots.getMetadata().hasPendingWrites() ? "local" : "server")
                            + " update for requests");
                    if (!queryDocumentSnapshots.getMetadata().hasPendingWrites()) {
                        ArrayList<Request> requests = new ArrayList<>();
                        ArrayList<Request> openRequests = new ArrayList<>();

                        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Request request = doc.toObject(Request.class);
                            //  check if there is a change in the request status of current user
                            if (databaseHelper.getCurrentMode() == "rider") {
                                checkActiveNotification(request);
                                checkPickedUpNotification(request);
                                checkArrivedNotification(request);
                            }
                            requests.add(request);
                            if (!request.getAccepted())
                                openRequests.add(request);
                        }
                        if (databaseHelper.getCurrentMode() == "driver") {
                            notifyAllOpenRequests(openRequests);
                            checkCancelNotification(requests);
                        }
                    }
                }
            }
        });
    }

    /**
     * This method is the only static method that create a singleton for RequestDataHelper
     * @return
     *  return the instance of RequestDataHelper singleton
     */
    public static RequestDataHelper getInstance() {
        if (requestDataHelper == null)
            requestDataHelper = new RequestDataHelper();
        return requestDataHelper;
    }

    /**
     * This method set up the listener for notification of active request
     * @param listener
     *  listener for notification
     */
    public void setOnNotifyListener(OnGetRequestDataListener listener) {
        notifyListener = listener;
    }

    /**
     * This method will notify the latest listener stored in this class that the request is set to
     * active, which means there is a driver accepted the rider's request
     * @param request
     *  the request that is active
     */
    void notifyActive(Request request) {
        if (notifyListener != null) {
            notifyListener.onActiveNotification(request);
        }
    }

    /**
     * This method will notify the latest listener stored in this class that the request is cancel
     */
    void notifyCancel() {
        if (notifyListener != null) {
            notifyListener.onCancelNotification();
        }
    }

    /**
     * This method will notify the latest listener stored in this class that the request is picked up
     */
    void notifyPickedUp(Request request) {
        if (notifyListener != null) {
            notifyListener.onPickedUpNotification(request);
        }
    }

    void notifyArrived(Request request) {
        if (notifyListener != null) {
            notifyListener.onArrivedNotification(request);
        }
    }

    /**
     * This method will notify the latest listener stored in this class that the request is completed
     */
    void notifyComplete() {
        if (notifyListener != null)
            notifyListener.onCompleteNotification();
    }

    /**
     * This method will obtain all open request when a request is updated
     * and return a list of request as a parameter to listener by calling
     *
     */
    void notifyAllOpenRequests(ArrayList<Request> openRequests) {
        if (notifyListener != null)
            notifyListener.onSuccess(openRequests, ALL_REQs_TAG);
    }

    /**
     * This is the method that add request to firebase and will notify the listener when success
     * @param newRequest
     *  request to be added
     * @param listener
     *  listener for notification
     */
    private void addRequest(final Request newRequest, final OnGetRequestDataListener listener) {
        collectionReferenceReq
                .document(newRequest.getRid())
                .set(newRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  " request addition successful");
                        ArrayList<Request> requests = new ArrayList<Request>();
                        requests.add(newRequest);
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
    public void addNewRequest(final Request newRequest, final OnGetRequestDataListener listener) {
        if (newRequest == null || newRequest.getRider().getName() == null
                || newRequest.getRider().getName().length() == 0) {
            listener.onFailure("invalid parameters", ADD_REQ_TAG);
            return;
        }

        if (DatabaseHelper.getInstance().getCurrentMode().equals("driver")) {
            System.out.println("Current user is not in rider mode, unable to add new request");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = db.collection("Requests").document().getId();
        newRequest.setRid(id);
        addRequest(newRequest, listener);
    }

    /**
     * This is the method that delete request from firebase
     * @param requestID
     *  id of request to be deleted
     */
    private void delRequest(final String requestID, final OnGetRequestDataListener listener,
                                   final String mode) {
        collectionReferenceReq
                .document(requestID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, requestID + " deletion successful");
                        if (mode == CANCEL_REQ_TAG) {
                            // update user state of rider
                            DatabaseHelper.getInstance().getUserState().setActive(false);
                            listener.onSuccess(null, CANCEL_REQ_TAG);
                        } else if (mode == COMPLETE_REQ_TAG) {
                            // update user state of driver
                            DatabaseHelper.getInstance().getUserState().setActive(false);
                            DatabaseHelper.getInstance().getUserState().setOnGoing(false);
                            DatabaseHelper.getInstance().getUserState().setOnArrived(false);
                            listener.onSuccess(null, COMPLETE_REQ_TAG);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, requestID + " deletion failed" + e.toString());
                        if (mode.equals(CANCEL_REQ_TAG))
                            listener.onFailure(requestID + " deletion failed", CANCEL_REQ_TAG);
                        else if (mode.equals(COMPLETE_REQ_TAG))
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
    private void updateRequest(final String requestID, final Request request,
                                      final OnGetRequestDataListener listener,
                                      final String updateMode) {

        final DocumentReference reqDocRef = collectionReferenceReq.document(requestID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
                // set userstate of driver
                Log.d(TAG, "Transaction success: " + docID);
                if (updateMode == SET_ACTIVE_TAG) {
                    databaseHelper.getUserState().setActive(true);
                    listener.onSuccess(null, SET_ACTIVE_TAG);
                } else if (updateMode == SET_PICKEDUP_TAG) {
                    databaseHelper.getUserState().setOnGoing(true);
                    listener.onSuccess(null, SET_PICKEDUP_TAG);
                } else if (updateMode == SET_ARRIVED_TAG) {
                    databaseHelper.getUserState().setOnArrived(true);
                    listener.onSuccess(null, SET_ARRIVED_TAG);
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
                if (updateMode == SET_ACTIVE_TAG)
                    listener.onFailure("Transaction failure. " + e, SET_ACTIVE_TAG);
                else if (updateMode == SET_PICKEDUP_TAG)
                    listener.onFailure("Transaction failure. " + e, SET_PICKEDUP_TAG);
                else if (updateMode == SET_ARRIVED_TAG)
                    listener.onFailure("Transaction failure" + e, SET_ACTIVE_TAG);
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
    public void queryUserRequest(final String userName, final String mode,
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
     * @param listener
     *  listener for notification and obtain return value
     */
    public void queryAllOpenRequests(final OnGetRequestDataListener listener) {
        if (DatabaseHelper.getInstance().getCurrentMode().equals("rider")) {
            System.out.println("Current user is not in driver mode, unable to query all open requests");
            return;
        }

        collectionReferenceReq
                .whereEqualTo("isAccepted", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Request> openRequests = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Request query = document.toObject(Request.class);
                                if (!query.getAccepted())
                                    openRequests.add(query);
                            }
                            listener.onSuccess(openRequests, ALL_REQs_TAG);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException(),
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
    public void setRequestActive(final String requestID, final User driver, final Float offeredPrice,
                          final OnGetRequestDataListener listener) {
        if (requestID == null || requestID.length() == 0) {
            listener.onFailure("invalid parameters", SET_ACTIVE_TAG);
            return;
        } else if (driver == null || driver.getName() == null || driver.getName().length() == 0) {
            listener.onFailure("invalid parameters", SET_ACTIVE_TAG);
            return;
        } else if (DatabaseHelper.getInstance().getCurrentMode().equals("rider")) {
            System.out.println("Current user is not in driver mode, unable to update request");
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
                                updateRequest(requestID, query, listener, SET_ACTIVE_TAG);
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
     * @param listener
     *  listener for notification
     */
    public void setRequestPickedUp(final String requestID, final OnGetRequestDataListener listener) {
        if (requestID == null || requestID.length() == 0) {
            listener.onFailure("invalid parameters", SET_PICKEDUP_TAG);
            return;
        }

        if (DatabaseHelper.getInstance().getCurrentMode().equals("rider")) {
            System.out.println("Current user is not in driver mode, unable to update request");
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
                                query.setPickedUp(true);
                                System.out.println("***** " + requestID);
                                updateRequest(requestID, query, listener, SET_PICKEDUP_TAG);
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
     * This method will check if there is an active and picked up but has not arrived reuqest that
     * belongs to the rider and set the request to has arrived, then call the updateRequest method
     * @param requestID
     *  id of the request
     * @param listener
     *  listener for notification
     */
    public void setRequestArrived(final String requestID,  final OnGetRequestDataListener listener) {
        if (requestID == null || requestID.length() == 0) {
            listener.onFailure("invalid parameters", SET_ARRIVED_TAG);
            return;
        }

        if (DatabaseHelper.getInstance().getCurrentMode().equals("rider")) {
            System.out.println("Current user is not in driver mode, unable to update request");
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
                                        SET_ARRIVED_TAG);
                            } else if (!query.getAccepted()) {
                                listener.onFailure(requestID + " is not active yet",
                                        SET_ARRIVED_TAG);
                            } else if (!query.getPickedUp()) {
                                listener.onFailure(requestID + " is not picked up yet",
                                        SET_ARRIVED_TAG);
                            } else if (query.getHasArrived()) {
                                listener.onFailure(requestID + " has already arrived",
                                        SET_ARRIVED_TAG);
                            } else {
                                query.setHasArrived(true);
                                System.out.println("***** " + requestID);
                                updateRequest(requestID, query, listener, SET_ARRIVED_TAG);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException(),
                                    SET_ARRIVED_TAG);
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
    public void cancelRequest(final String requestID, final OnGetRequestDataListener listener) {
        if (requestID == null || requestID.length() == 0) {
            listener.onFailure("invalid parameters", CANCEL_REQ_TAG);
            return;
        }

        if (DatabaseHelper.getInstance().getCurrentMode().equals("driver")) {
            System.out.println("Current user is not in rider mode, unable to cancel request");
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
                                delRequest(requestID, listener, CANCEL_REQ_TAG);
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
    public void completeRequest(final String requestID, final Float payment,
                                       final Float rating,
                                       final OnGetRequestDataListener listener) {
        if (requestID == null || requestID.length() == 0) {
            listener.onFailure("invalid parameters", COMPLETE_REQ_TAG);
            return;
        }

        if (DatabaseHelper.getInstance().getCurrentMode().equals("rider")) {
            System.out.println("Current user is not in driver mode, unable to complete a request");
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
                                delRequest(requestID, listener, COMPLETE_REQ_TAG);
                                Record record = new Record(query, payment, rating);
                                RecordDataHelper.getInstance().addRecord(record);
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



    /**
     * This is the method that check if there is a notification needed to be sent to the rider
     * that there is a request set to active (a driver accepted the rider's request)
     * @param request
     *  candidate request
     */
    private void checkActiveNotification(Request request) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

        if (databaseHelper.getCurrentUserName() == null)
            return;

        UserState userState = databaseHelper.getUserState();
        if (request.getRider().getName().equals(databaseHelper.getCurrentUserName())) {
            if (request.getAccepted() && !userState.getActive()) {
                notifyActive(request);
                new PopUpNotification("Hey " + databaseHelper.getCurrentUserName(),
                        "your request is accepted by " + request.getDriver().getName())
                        .build();
                // update user state of rider
                userState.setActive(Boolean.TRUE);
                userState.setCurrentRequest(request);
                databaseHelper.setUserState(userState);
                System.out.println("-------- Accept Notification sent --------");
            }
        }
    }

    /**
     * This is the method that check if there is a notification needed to be sent to the rider
     * that there is a request picked up by a driver
     * @param request
     *  candidate request
     */
    private void checkPickedUpNotification(Request request) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

        if (databaseHelper.getCurrentUserName() == null)
            return;

        if (databaseHelper.getUserState().getCurrentRequest() == null)
            return;

        UserState userState = databaseHelper.getUserState();
        if (request.getRid().equals(userState.getRequestID())) {
            if (request.getAccepted() &&  request.getPickedUp()
                    && userState.getActive() && !userState.getOnGoing()) {
                notifyPickedUp(request);
                new PopUpNotification("Hey " + databaseHelper.getCurrentUserName(),
                        "you are picked up by " + request.getDriver().getName())
                        .build();
                // update user state of rider
                userState.setOnGoing(Boolean.TRUE);
                databaseHelper.setUserState(userState);
                System.out.println("-------- Picked up Notification sent --------");
            }
        }
    }

    private void checkArrivedNotification(Request request) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

        if (databaseHelper.getCurrentUserName() == null)
            return;

        if (databaseHelper.getUserState().getCurrentRequest() == null)
            return;

        UserState userState = databaseHelper.getUserState();
        if (request.getRid().equals(userState.getRequestID())) {
            if (request.getAccepted() && request.getPickedUp() && request.getHasArrived()
                    && userState.getActive() && userState.getOnGoing() && !userState.getOnArrived()) {
                notifyArrived(request);
                new PopUpNotification("Hey " + databaseHelper.getCurrentUserName(),
                        "you have arrived your destination")
                        .build();
                // update user state of rider
                userState.setOnArrived(Boolean.TRUE);
                databaseHelper.setUserState(userState);
                System.out.println("-------- Arrived Notification sent --------");
            }
        }
    }

    /**
     * This method check if there is a notification needed to be sent to the driver
     * that the request working on is canceled by the rider
     * @param requests
     *  candidate request
     */
    private void checkCancelNotification(ArrayList<Request> requests) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

        if (databaseHelper.getCurrentUserName() == null)
            return;

        UserState userState = databaseHelper.getUserState();
        if (!userState.getOnGoing()) {
            return;
        }

        if (databaseHelper.getUserState().getCurrentRequest() == null)
            return;

        boolean found = false;

        for (Request request: requests) {
            if (request.getRid().equals(userState.getRequestID())) {
                found = true;
                break;
            }
        }
        if (!found) {
            new PopUpNotification("Hey " + databaseHelper.getCurrentUserName(),
                    "this request is canceled")
                    .build();
            notifyCancel();
            // update user state of driver
            userState.setActive(Boolean.FALSE);
            userState.setOnGoing(Boolean.FALSE);
            userState.setCurrentRequest(null);
            databaseHelper.setUserState(userState);
            System.out.println("-------- Cancel Notification sent --------");
        }

    }

}