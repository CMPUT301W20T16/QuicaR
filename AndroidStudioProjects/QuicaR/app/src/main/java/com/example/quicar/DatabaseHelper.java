package com.example.quicar;


import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is the class that handle data transfer between the app and firebase
 */
public class DatabaseHelper {
    private static final String REC_COLL_NAME = "Records";
    private static final String REQ_COLL_NAME = "Requests";
    private static final String USER_COLL_NAME = "Users";

    protected static final String TAG = "quicarDB";
    private static String oldServerKey;

    private static FirebaseFirestore db;
    private static CollectionReference collectionReferenceRec;
    private static CollectionReference collectionReferenceReq;
    private static CollectionReference collectionReferenceUser;

    //private static ArrayList<Record> records = new ArrayList<>();
    //private static ArrayList<Request> requests = new ArrayList<>();
    //private static ArrayList<User> users = new ArrayList<>();

    private static UserState userState = new UserState();

    /**
     * This is the constructor of database helper which initialize the firebase instance
     * and set up values for interacting.
     */
    public DatabaseHelper() {

        if (db != null)
            return;
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

                    //DatabaseHelper.requests.clear();
                    if (!queryDocumentSnapshots.getMetadata().hasPendingWrites()) {
                        String requestID = "request0";
                        ArrayList<Request> requests = new ArrayList<>();
                        ArrayList<Request> openRequests = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            //                        Log.d(TAG, String.valueOf(doc.getData().get(REQUEST_KEY)));
                            requestID = doc.getId();
//                            delRequest(requestID);
                            Request request = doc.toObject(Request.class);
                            //  check if there is a change in the request status of current user
                            if (DatabaseHelper.getCurrentMode() == "rider") {
                                checkActiveNotification(request);
                                checkPickedUpNotification(request);
                            }
                            requests.add(request);
                            if (!request.getAccepted())
                                openRequests.add(request);
                        }
                        RequestDataHelper.notifyAllOpenRequests(openRequests);
                        if (DatabaseHelper.getCurrentMode() == "driver")
                            checkCancelNotification(requests);
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

                    //DatabaseHelper.users.clear();

                    String userID = "request0";

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                        Log.d(TAG, String.valueOf(doc.getData().get(USER_KEY)));
//                        userID = doc.getId();
//                        delUser(userID);
                        User user = doc.toObject(User.class);
                        //DatabaseHelper.users.add(user);
                    }
                }
            }
        });
    }

//    /**
//     * This is the method that delete request from firebase
//     * @param requestID
//     *  id of request to be deleted
//     */
//    private static void delRequest(final String requestID) {
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
//    private static void delRecord(final String recordID) {
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
     * This method return the firebase collection reference of requests
     * @return
     *  firebase collection reference of requests
     */
    protected CollectionReference getCollectionReferenceReq() {
        return collectionReferenceReq;
    }

    /**
     * This method return the firebase collection reference of records
     * @return
     *  firebase collection reference of records
     */
    protected CollectionReference getCollectionReferenceRec() {
        return collectionReferenceRec;
    }

    /**
     * This method return the firebase collection reference of users
     * @return
     *  firebase collection reference of users
     */
    protected CollectionReference getCollectionReferenceUser() {
        return collectionReferenceUser;
    }

    /**
     * This method return the firebase firestore db variable
     * @return
     *  firebase firestore db variable
     */
    protected FirebaseFirestore getDb() {
        return db;
    }

    /**
     * This is the method that return the Firebase token of this device
     * @return
     *  Firebase token of this device
     */
    public static String getToken() {
        return userState.getToken();
    }

    /**
     * This is the method that set the value of token in DatabaseHelper
     * @param token
     *  the value of token in DatabaseHelper
     */
    public static void setToken(String token) {
        userState.setToken(token);
    }

    /**
     * This is the method that return the old server key of quicar firebase
     * @return
     *  the old server key of quicar firebase
     */
    public static String getOldServerKey() {
        return oldServerKey;
    }

    /**
     * This is the method that set the value of old server key in DatabaseHelper
     * @param oldServerKey
     *  value of old server key in DatabaseHelper
     */
    public static void setOldServerKey(String oldServerKey) {
        DatabaseHelper.oldServerKey = oldServerKey;
    }


    /**
     * This is the method that return the current user name stored locally
     * @return
     *  current user name
     */
    public static String getCurrentUserName() {
        return userState.getCurrentUserName();
    }

    /**
     * This is the method that set the current user name locally
     * @param currentUserName
     *  user name
     */
    public static void setCurrentUserName(String currentUserName) {
        userState.setCurrentUserName(currentUserName);
    }

    /**
     * This method return current user object
     * @return
     *  current user object
     */
    public static User getCurrentUser() {
        return userState.getCurrentUser();
    }

    /**
     * This method set the value of current user object
     * @param user
     *  candidate user object
     */
    public static void setCurrentUser(User user) {
        userState.setCurrentUser(user);
    }

    /**
     * This is the method that set the current mode of the user, either rider or driver mode
     * @return
     */
    public static String getCurrentMode() {
        return userState.getCurrentMode();
    }

    /**
     * This is the method that return the current mode of the user, either rider or driver mode
     * @param currentMode
     *  the current mode of the user
     */
    public static void setCurrentMode(String currentMode) {
        if (currentMode != "rider" && currentMode != "driver")
            throw new IllegalArgumentException("user mode can only be rider or driver, but an alternative obtained!");

        userState.setCurrentMode(currentMode);
    }

    /**
     * This is the method that return the first location selected by rider
     * @return
     *  first location selected by rider
     */
    public static Location getFirstLocation() {
        return userState.getFirstSelectedLocation();
    }

    /**
     * This is the method that set the value of first location selected by rider
     * @param location
     *  first location selected by rider
     */
    public static void setFirstLocation(Location location) {
        userState.setFirstSelectedLocation(location);
    }

    /**
     * This is the method that return the second location selected by rider
     * @return
     *  second location selected by rider
     */
    public static Location getSecondLocation() {
        return userState.getSecondSelectedLocation();
    }

    /**
     * This is the method that set the value of second location in DatabaseHelper
     * @param location
     *  value of second location in DatabaseHelper
     */
    public static void setSecondLocation(Location location) {
        userState.setSecondSelectedLocation(location);
    }

    /**
     * This is the method that check if there is a notification needed to be sent to the rider
     * that there is a request set to active (a driver accepted the rider's request)
     * @param request
     *  candidate request
     */
    private void checkActiveNotification(Request request) {
        if (DatabaseHelper.getCurrentUserName() == null)
            return;
        if (request.getRider().getName().equals(DatabaseHelper.getCurrentUserName())) {
            if (request.getAccepted() && !userState.getActive()) {
                RequestDataHelper.notifyActive(request);
                //sendPopUpNotification("request is accepted");
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
        if (DatabaseHelper.getCurrentUserName() == null)
            return;
        if (request.getRider().getName().equals(DatabaseHelper.getCurrentUserName())) {
            if (request.getAccepted() &&  request.getPickedUp()
                    && userState.getActive() && !userState.getOnGoing()) {
                RequestDataHelper.notifyPickedUp(request);
                //sendPopUpNotification("Notification test", "rider is picked up", this);
                userState.setOnGoing(Boolean.TRUE);
                System.out.println("-------- Picked up Notification sent --------");
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
        if (DatabaseHelper.getCurrentUserName() == null)
            return;
        if (!userState.getOnGoing()) {
            System.out.println("user is not in on going state, unable to get cancel notification!");
            return;
        }

        boolean found = false;
        for (Request request: requests) {
            if (request.getDriver().getName().equals(DatabaseHelper.getCurrentUserName())) {
                found = true;
                break;
            }
        }
        if (!found) {
            //sendPopUpNotification("Notification test", "Request is canceled", this);
            RequestDataHelper.notifyCancel();
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
        if (DatabaseHelper.getCurrentUserName() == null)
            return;
        for (Record record: records) {
            if (record.getRequest().getRider().getName().equals(DatabaseHelper.getCurrentUserName())
                    && DatabaseHelper.getCurrentMode().equals("rider")  && userState.getOnGoing()) {
                // might want to check if userstate.getOngoing is updated
                //sendPopUpNotification("Notification test", "ride is completed", this);
                userState.setActive(Boolean.FALSE);
                userState.setOnGoing(Boolean.FALSE);
                RequestDataHelper.notifyComplete();
                System.out.println("-------- Notification sent --------");
                break;
            }
//            if (record.getRequest().getDriver().getName().equals(DatabaseHelper.getCurrentUserName())
//                    && DatabaseHelper.getCurrentMode().equals("driver") && userState.getOnGoing()) {
//                //sendPopUpNotification("Notification test", "ride is completed", this);
//                userState.setActive(Boolean.FALSE);
//                userState.setOnGoing(Boolean.FALSE);
//                RequestDataHelper.notifyComplete();
//                System.out.println("-------- Notification sent --------");
//            }
        }

    }


    /**
     * This method send a pop up notification to this device
     * @param msg
     *  message body in the notification
     */
    public static void sendPopUpNotification(String title, String msg) {
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


                conn.setRequestProperty("Authorization", "key=" + DatabaseHelper.getOldServerKey() );
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();

                json.put("to", DatabaseHelper.getToken());

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
