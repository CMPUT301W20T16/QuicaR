package com.example.quicar;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LocationDataHelper {
    final String TAG = "quicarDB-location";
    private CollectionReference collectionReferenceLoc;

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
                        Location location = doc.toObject(Location.class);
                    }
                }
            }
        });
    }
}
