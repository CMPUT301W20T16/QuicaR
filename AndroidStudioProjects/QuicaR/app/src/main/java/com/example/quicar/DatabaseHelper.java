package com.example.quicar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;



/**
 * This is the class that handle data transfer between the app and firebase
 */
public class DatabaseHelper {
    final String TAG = "quicarDB";
    private UserState userState;

    private static DatabaseHelper databaseHelper;

    /**
     * This is the constructor of database helper which initialize the firebase instance
     * and set up values for interacting.
     */
    private DatabaseHelper() {
        FirebaseFirestore.getInstance().clearPersistence();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //  Configure offline persistence
        // The default cache size threshold is 100 MB. Configure "setCacheSizeBytes"
        // for a different threshold (minimum 1 MB) or set to "CACHE_SIZE_UNLIMITED"
        // to disable clean-up.
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();

        db.setFirestoreSettings(settings);

        userState = new UserState();

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

    /**
     * This is the method that return the Firebase token of this device
     * @return
     *  Firebase token of this device
     */
    public String getToken() {
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

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

}