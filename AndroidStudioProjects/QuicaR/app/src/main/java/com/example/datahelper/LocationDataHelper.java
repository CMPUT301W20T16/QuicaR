package com.example.datahelper;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.entity.Location;
import com.example.listener.OnGetLocationDataListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class LocationDataHelper {
    final String TAG = "quicarDB-location";
    private CollectionReference collectionReferenceLoc;
    private OnGetLocationDataListener notifyListener;
    private static LocationDataHelper locationDataHelper;

    /**
     * This is the constructor of database helper which initialize the firebase instance
     * and set up values for interacting.
     */
    private LocationDataHelper() {
        collectionReferenceLoc = FirebaseFirestore.getInstance().collection("locations");

        collectionReferenceLoc.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
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
                            + " update for locations");
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String userName = doc.getId();
                        Location location = doc.toObject(Location.class);
                        checkUpdateNotification(userName, location);
                    }
                }
            }
        });
    }

    /**
     * This method is the only static method that create a singleton for LocationDataHelper
     * @return
     *  return the instance of LocationDataHelper singleton
     */
    public static LocationDataHelper getInstance() {
        if (locationDataHelper == null)
            locationDataHelper = new LocationDataHelper();
        return locationDataHelper;
    }

    /**
     * This method set up the listener for notification of active request
     * @param listener
     *  listener for notification
     */
    public void setOnNotifyListener(OnGetLocationDataListener listener) {
        notifyListener = listener;
    }

    /**
     * This method update user's location on firebase so that others can obtain the latest location
     * of this user
     * @param userName
     *  user name
     * @param location
     *  location of the user
     */
    public void updateLocation(final String userName, final Location location) {
        final DocumentReference locDocRef = collectionReferenceLoc.document(userName);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.runTransaction(new Transaction.Function<String>() {
            @Override
            public String apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(locDocRef);

                if (snapshot.exists()) {
                    System.out.println("docSnapshot---------" + snapshot);
                    Location locationTmp = snapshot.toObject(Location.class);
                    System.out.println( "request temp---------" + locationTmp);
                    transaction.set(locDocRef, location);
                } else {
                    System.out.println("docSnapshot does not exist, added new entry");
                    transaction.set(locDocRef, location);
                }
                return userName;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }

    /**
     * This method is will notify the listener when there is location updated
     * @param location
     */
    void notifyUpdate(final Location location) {
        if (notifyListener != null)
            notifyListener.onUpdate(location);
    }

    /**
     * This method check if there is any update for user related to current request
     * @param userName
     *  user name of updated location
     * @param location
     *  updated location
     */
    void checkUpdateNotification(final String userName, final Location location) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        String currentMode = databaseHelper.getCurrentMode();

        if (databaseHelper.getUserState().getCurrentRequest() == null)
            return;
        else if (databaseHelper.getUserState().getCurrentRequest().getRid() == null)
            return;

        if (currentMode.equals("rider")) {
            if (userName.equals(databaseHelper.getUserState().getCurrentRequest().getDriver().getName()))
                notifyUpdate(location);
        } else {
            if (userName.equals(databaseHelper.getUserState().getCurrentRequest().getRider().getName()))
                notifyUpdate(location);
        }
    }



}
