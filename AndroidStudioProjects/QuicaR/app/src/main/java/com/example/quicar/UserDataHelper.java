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


/**
 * This class extend DatabaseHelper and mainly handle user data
 */
public class UserDataHelper extends DatabaseHelper {
    public static String ADD_USER_TAG = "add user";
    public static String UPDATE_USER_TAG = "update user";
    public static String GET_USER_TAG = "get user";

    private static CollectionReference collectionReferenceUser;
    private static FirebaseFirestore db;
    private static boolean uniqueTag;


    /**
     * This is the constructor of UserDataHelper
     */
    public UserDataHelper() {
        super();
        UserDataHelper.collectionReferenceUser = super.getCollectionReferenceUser();
        UserDataHelper.db = super.getDb();
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
                        listener.onSuccess(null, ADD_USER_TAG);
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

        final DocumentReference reqDocRef = collectionReferenceUser.document(userID);

        db.runTransaction(new Transaction.Function<String>() {
            @Override
            public String apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(reqDocRef);
                System.out.println("docSnapshot---------" + snapshot);
                Request requestTmp = snapshot.toObject(Request.class);
                System.out.println( "request temp---------" + requestTmp);
                if (!requestTmp.getAccepted()) {
                    transaction.set(reqDocRef, user);
                    return userID;
                } else {
                    throw new FirebaseFirestoreException("Request has already been accepted",
                            FirebaseFirestoreException.Code.ABORTED);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String userID) {
                Log.d(TAG, "Transaction success: " + userID);
                listener.onSuccess(null, UPDATE_USER_TAG);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                        listener.onFailure("Transaction failure. " + e);
                    }
                });
    }

    public static boolean getUser(final String userName, final OnGetUserDataListener listener) {
        if (userName == null || userName.length() == 0) {
            listener.onFailure("user provided is a null object");
            return false;
        }
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
                                listener.onFailure(userName + " has more than one account");
                                uniqueTag = false;
                            } else if (user == null) {
                                listener.onFailure(userName + " has no account");
                                uniqueTag = false;
                            } else {
                                listener.onSuccess(user, GET_USER_TAG);
                                uniqueTag = false;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
        if (!uniqueTag) {
            return false;
        }
        return true;
    }

    /**
     * This method will check if the user already exists and call addUser method
     * @param newUser
     *  new user to be added
     * @param listener
     *  listener for notification
     */
    public static void addNewUser(final User newUser, final OnGetUserDataListener listener) {
        if (newUser == null) {
            listener.onFailure("user provided is a null object");
            return;
        }
        final String userName = newUser.getName();
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
//                            User user = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                user = document.toObject(User.class);
                                count++;
                            }
                            if (count > 0) {
                                System.out.println("*****  user \" " + userName + " \" has an existing account");
                                listener.onFailure(userName + " has an existing account");


                            } else {
                                UserDataHelper.addUser(newUser, listener);

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
     * @param user
     *  user object to be updated
     * @param listener
     *  listener for notification
     */
    public static void updateUserProfile(final User user, final OnGetUserDataListener listener) {
        if (user == null || user.getName() == null || user.getName().length() == 0) {
            listener.onFailure("user name provided is a null or empty");
            return;
        }
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
//                            User user = null;
                            String userID = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                user = document.toObject(User.class);
                                userID = document.getId();
                                count++;
                            }
                            if (count > 1) {
                                System.out.println("*****  user \" " + userName + " \" has more than one account");
                                listener.onFailure(userName + " has more than one request");
                            } else {
                                UserDataHelper.updateUser(user, userID, listener);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onFailure("Error getting documents: " + task.getException());
                        }
                    }
                });
    }
}
