package com.example.quicar;


import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This is the class that handle data transfer between the app and firebase
 */
public class DatabaseHelper {
    final String TAG = "quicarDB";
    private FirebaseFirestore db;
    private CollectionReference collectionReferenceRec;
    private CollectionReference collectionReferenceReq;
    private CollectionReference collectionReferenceUser;
    private UserState userState;

    private static DatabaseHelper databaseHelper;

    /**
     * This is the constructor of database helper which initialize the firebase instance
     * and set up values for interacting.
     */
    private DatabaseHelper() {
        FirebaseFirestore.getInstance().clearPersistence();
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

        collectionReferenceRec = db.collection("Records");
        collectionReferenceReq = db.collection("Requests");
        collectionReferenceUser = db.collection("Users");

        userState = new UserState();

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

                    String recordID = "record0";
                    ArrayList<Record> records = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                        Log.d(TAG, String.valueOf(doc.getData().get(RECORD_KEY)));
                        recordID = doc.getId();
//                        delRecord(recordID);
                        Record record = doc.toObject(Record.class);
                        records.add(record);
                    }
                    checkCompleteNotification(records);
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
                    if (!queryDocumentSnapshots.getMetadata().hasPendingWrites()) {
                        String requestID = "request0";
                        ArrayList<Request> requests = new ArrayList<>();
                        ArrayList<Request> openRequests = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            requestID = doc.getId();
//                            delRequest(requestID);
                            Request request = doc.toObject(Request.class);
                            //  check if there is a change in the request status of current user
                            if (getCurrentMode() == "rider") {
                                checkActiveNotification(request);
                                checkPickedUpNotification(request);
                                checkArrivedNotification(request);
                            }
                            requests.add(request);
                            if (!request.getAccepted())
                                openRequests.add(request);
                        }
                        RequestDataHelper.getInstance().notifyAllOpenRequests(openRequests);
                        if (getCurrentMode() == "driver")
                            checkCancelNotification(requests);
                    }
                }
            }
        });

        // Get token
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        setToken(token);
                        // Log and toast
                        String msg = "InstanceID Token:" + token;
                        Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END retrieve_current_token]
    }

    /**
     * This method is the only static method that create a singleton for DatabaseHelper
     * @return
     *  return the instance of DatabaseHelper singleton
     */
    public static DatabaseHelper getInstance() {
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper();
        return databaseHelper;
    }

//    /**
//     * This is the method that delete request from firebase
//     * @param requestID
//     *  id of request to be deleted
//     */
//    private void delRequest(final String requestID) {
//        collectionReferenceReq
//                .document(requestID)
//                .delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, requestID + " deletion successful");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, requestID + " deletion failed" + e.toString());
//                    }
//                });
//    }
//
//    private void delRecord(final String recordID) {
//        collectionReferenceRec
//                .document(recordID)
//                .delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, recordID + " deletion successful");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, recordID + " deletion failed" + e.toString());
//                    }
//                });
//    }

    /**
     * This method return the firebase firestore db variable
     * @return
     *  firebase firestore db variable
     */
    FirebaseFirestore getDb() {
        return db;
    }


    /**
     * This method return the firebase collection reference of requests
     * @return
     *  firebase collection reference of requests
     */
    CollectionReference getCollectionReferenceReq() {
        return collectionReferenceReq;
    }

    /**
     * This method return the firebase collection reference of records
     * @return
     *  firebase collection reference of records
     */
    CollectionReference getCollectionReferenceRec() {
        return collectionReferenceRec;
    }

    /**
     * This method return the firebase collection reference of users
     * @return
     *  firebase collection reference of users
     */
    CollectionReference getCollectionReferenceUser() {
        return collectionReferenceUser;
    }


    /**
     * This is the method that return the Firebase token of this device
     * @return
     *  Firebase token of this device
     */
    private String getToken() {
        return userState.getToken();
    }

    /**
     * This is the method that set the value of token in DatabaseHelper
     * @param token
     *  the value of token in DatabaseHelper
     */
    void setToken(String token) {
        userState.setToken(token);
    }

    /**
     * This is the method that return the current user name stored locally
     * @return
     *  current user name
     */
    public String getCurrentUserName() {
        return userState.getCurrentUserName();
    }

//    /**
//     * This is the method that set the current user name locally
//     * @param currentUserName
//     *  user name
//     */
//    public void setCurrentUserName(String currentUserName) {
//        userState.setCurrentUserName(currentUserName);
//    }

    /**
     * This method return current user object
     * @return
     *  current user object
     */
    User getCurrentUser() {
        return userState.getCurrentUser();
    }

    /**
     * This method set the value of current user object
     * @param user
     *  candidate user object
     */
    void setCurrentUser(User user) {
        userState.setCurrentUser(user);
    }

    /**
     * This is the method that set the current mode of the user, either rider or driver mode
     * @return
     */
    String getCurrentMode() {
        return userState.getCurrentMode();
    }

    /**
     * This is the method that return the current mode of the user, either rider or driver mode
     * @param currentMode
     *  the current mode of the user
     */
    void setCurrentMode(String currentMode) {
        if (currentMode != "rider" && currentMode != "driver")
            throw new IllegalArgumentException("user mode can only be rider or driver, but an alternative obtained!");

        userState.setCurrentMode(currentMode);
    }

    /**
     * This is the method that check if there is a notification needed to be sent to the rider
     * that there is a request set to active (a driver accepted the rider's request)
     * @param request
     *  candidate request
     */
    private void checkActiveNotification(Request request) {
        if (getCurrentUserName() == null)
            return;
        if (request.getRider().getName().equals(getCurrentUserName())) {
            if (request.getAccepted() && !userState.getActive()) {
                RequestDataHelper.getInstance().notifyActive(request);
                sendPopUpNotification("Hey" + getCurrentUserName(),
                        "your request is accepted by " + request.getDriver().getName());
                userState.setActive(Boolean.TRUE);
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
        if (getCurrentUserName() == null)
            return;
        if (request.getRider().getName().equals(getCurrentUserName())) {
            if (request.getAccepted() &&  request.getPickedUp()
                    && userState.getActive() && !userState.getOnGoing()) {
                RequestDataHelper.getInstance().notifyPickedUp(request);
                sendPopUpNotification("Hey" + getCurrentUserName(),
                        "you are picked up by " + request.getDriver().getName());
                userState.setOnGoing(Boolean.TRUE);
                System.out.println("-------- Picked up Notification sent --------");
            }
        }
    }

    private void checkArrivedNotification(Request request) {
        if (getCurrentUserName() == null)
            return;
        if (request.getRider().getName().equals(getCurrentUserName())) {
            if (request.getAccepted() && request.getPickedUp() && request.getHasArrived()
                    && userState.getActive() && userState.getOnGoing() && !userState.getOnArrived()) {
                RequestDataHelper.getInstance().notifyArrived(request);
                sendPopUpNotification("Hey" + getCurrentUserName(),
                        "you have arrived your destination");
                userState.setOnArrived(Boolean.TRUE);
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
        if (getCurrentUserName() == null)
            return;
        if (!userState.getOnGoing()) {
            System.out.println("user is not in on going state, unable to get cancel notification!");
            return;
        }

        boolean found = false;
        for (Request request: requests) {
            if (request.getDriver().getName().equals(getCurrentUserName())) {
                found = true;
                break;
            }
        }
        if (!found) {
            sendPopUpNotification("Hey" + getCurrentUserName(), "this request is canceled");
            RequestDataHelper.getInstance().notifyCancel();
            userState.setOnGoing(Boolean.FALSE);
            System.out.println("-------- Cancel Notification sent --------");
        }

    }

    /**
     * This method check if there is a notification needed to be sent to the rider
     * that the rider's request is completed
     * @param records
     *  list of records
     */
    private void checkCompleteNotification(ArrayList<Record> records) {
        if (getCurrentUserName() == null)
            return;
        for (Record record: records) {
            if (record.getRequest().getRider().getName().equals(getCurrentUserName())
                    && getCurrentMode().equals("rider")  && userState.getOnGoing()) {
                // might want to check if userstate.getOngoing is updated
                sendPopUpNotification("Hey" + getCurrentUserName(), "your ride is completed");
                userState.setActive(Boolean.FALSE);
                userState.setOnGoing(Boolean.FALSE);
                RequestDataHelper.getInstance().notifyComplete();
                System.out.println("-------- Notification sent --------");
                break;
            }
        }
    }


    /**
     * This method send a pop up notification to this device
     * @param msg
     *  message body in the notification
     */
    void sendPopUpNotification(final String title, final String msg) {
        System.out.println("_---------------- notify please");
        new Notify().execute(title, msg);
    }



    /**
     * This is the class that generate a notification
     */
    private static class Notify extends AsyncTask<String, Void, Void> {
        // https://www.youtube.com/watch?v=v29x4dKNBJw
        // https://stackoverflow.com/questions/9671546/asynctask-android-example
        @Override
        protected Void doInBackground(String... data) {
            try {

                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");


                conn.setRequestProperty("Authorization", "key=AIzaSyDKHMO4xM-b9y2-TMGR5KvPTvlT9jqGbYs");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();

                json.put("to", DatabaseHelper.getInstance().getToken());

                String title = data[0];
                String body = data[1];

                JSONObject info = new JSONObject();
                info.put("title", title);   // Notification title
                info.put("body", body); // Notification body

                json.put("notification", info);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();

            } catch (Exception e) {
                Log.d("Error",""+e);
            }
            return null;
        }
    }

}
