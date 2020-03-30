package com.example.datahelper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.entity.Request;
import com.example.listener.OnGetUserStateListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserStateDataHelper {
    final String TAG = "quicarDB-userState";
    private CollectionReference collectionReferenceState;
    private static UserStateDataHelper userStateDataHelper;

    private UserStateDataHelper() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        collectionReferenceState = db.collection("UserState");
    }

    /**
     * This method is the only static method that create a singleton for UserStateDataHelper
     * @return
     *  return the instance of UserStateDataHelper singleton
     */
    public static UserStateDataHelper getInstance() {
        if (userStateDataHelper == null)
            userStateDataHelper = new UserStateDataHelper();
        return userStateDataHelper;
    }

    public void recordState() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        UserState userState = databaseHelper.getUserState();
        ProxyUserState proxyUserState = new ProxyUserState(userState);

        collectionReferenceState
                .document(userState.getCurrentUserName())
                .set(proxyUserState)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  " user state recorded successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,  "User state recording failed" + e.toString());
                    }
                });
    }

    public void getUserState(final OnGetUserStateListener listener) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        UserState userState = databaseHelper.getUserState();

        collectionReferenceState
                .document(userState.getCurrentUserName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                ProxyUserState proxyUserState = document.toObject(ProxyUserState.class);
                                DatabaseHelper.getInstance().getUserState().setState(proxyUserState);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                            listener.onStateUpdated();
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }
}
